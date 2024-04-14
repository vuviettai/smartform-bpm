package com.smartform.customize.fnt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.smartform.models.ActionResult;
import com.smartform.rest.client.FormioService;
import com.smartform.rest.model.Submission;
import com.smartform.utils.StringUtil;
import com.smartform.utils.SubmissionUtil;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class FntService {
	public static final int COMMON_CODE_LENGTH = 5;
	public static final int RECEIPT_CODE_LENGTH = 5;
	public static final int PACKAGE_CODE_LENGTH = 3;
	public static final int NHAPKHO_CODE_LENGTH = 1;
	public static final int XUATKHO_CODE_LENGTH = 1;
	public static final String ACTION_GENERATE_PACKAGE = "generatePackage";
	public static final String ACTION_SUBMIT_RECEIPT = "submitReceipt";
	public static final String ACTION_SUBMIT_PHIEUNHAPKHO = "submitPhieuNhapKho";
	public static final String ACTION_SUBMIT_PHIEUXUATKHO = "submitPhieuXuatKho";
	public static final String ACTION_NHAP_KHO = "nhapKho";
	public static final String ACTION_XUAT_KHO = "xuatKho";
	public static final String FORM_HANG_VE = "form_hangve";
	public static final String FORM_PACKAGE = "form_package";
	public static final String FORM_NHAP_KHO = "form_nhapKho";
	public static final String FORM_XUAT_KHO = "form_xuatKho";
	public static final String FORM_HANG_NHAP_KHO = "form_hangNhapKho";
	public static final String FORM_HANG_XUAT_KHO = "form_hangXuatKho";
	public static final String MANIFEST_ITEMS = "manifestItems";
	public static final String RECEIPT = "receipt";
	public static final String RECEIPT_CODE = "receiptCode";
	public static final String PACKAGE_CODE = "packageCode";
	public static final String SUBMISSION_IDS = "submissionIds";
	public static final String REF_FORM = "refForm";
	public static final String REF_SUBMISSION = "refSubmission";
	public static final String PARAM_CREATING_FORM_ID = "createFormId";
	public static final String PARAM_START_INDEX = "startIndex";
	public static final String PARAM_END_INDEX = "endIndex";
    public static final String PREFIX_XUAT_KHO = "Lan";
    public static final String PREFIX_NHAP_KHO = "Lan";
    public static final String PREFIX_RECEIPT = "";
    
    
	@Inject
    SecurityIdentity identity;
	
	@Inject
	private SubmissionUtil submissionUtil;
	
	@RestClient
	@Inject
	FormioService formioService;
	
	public List<Submission> generatePackage(Submission receipt, Map<String, List<Map<String, Object>>> mapPackageItems, Map<String, Object> requestParams) {
		List<Submission> createdPackages = new ArrayList<Submission>();
		String createFormId = (String)requestParams.get(FntService.PARAM_CREATING_FORM_ID);
		if (createFormId != null ) {
			int start = 1, end = 0;
			Object value = requestParams.get(PARAM_START_INDEX);
			if (value instanceof Number) {
				start = ((Number)value).intValue();
			}
			value = requestParams.get(PARAM_END_INDEX);
			if (value == null) {
				value = SubmissionUtil.getFieldValue(receipt, "packageCounter");
			}
			if (value instanceof Number) {
				end = ((Number)value).intValue();
			} 	
			if (end >= start) {
				List<Submission> listPackages = createPackages(receipt, start, end);
				//Auto allocate manifest items
				
				for(Submission packageSub : listPackages) {
					String pkgCode = (String)SubmissionUtil.getFieldValue(packageSub, PACKAGE_CODE);
					List<Map<String, Object>> pkgItems = mapPackageItems.get(pkgCode);
					if (pkgItems != null) {
						SubmissionUtil.setDataValue(packageSub, MANIFEST_ITEMS, pkgItems);
					}
;					Submission createdPackage = formioService.createSubmission(createFormId, packageSub);
					createdPackages.add(createdPackage);
				}
			}
		}
		return createdPackages;
	}
	/*
	 * Item's fields: electronic, note, price, quantity, tax
	 * Loop qua moi manifest item, voi tung manifest 'item quantity ramdomize package index
	 */
	public Map<String, List<Map<String, Object>>> allocateManifestItems(Submission receipt) {
		Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
		Object packageCounter = SubmissionUtil.getFieldValue(receipt, "packageCounter");
		String receiptCode = (String)SubmissionUtil.getFieldValue(receipt, RECEIPT_CODE);
		List<Map<String, Object>> manifestItems = (List<Map<String, Object>>)SubmissionUtil.getFieldValue(receipt, MANIFEST_ITEMS);
		if (packageCounter != null && packageCounter instanceof Number && manifestItems != null && manifestItems.size() > 0) {
			int counter = ((Number)packageCounter).intValue();
			for (Map<String, Object> manifestItem : manifestItems) {
				Object quantity = manifestItem.get("quantity");
				Map<String, Integer> mapPackageQuantity = new HashMap<String, Integer>();
				if (quantity instanceof Number && ((Number)quantity).intValue() > 0) {
					for(int i = 0; i < ((Number)quantity).intValue(); i++) {
						int index = (int)Math.floor(counter * Math.random());
						//Package index start from 1
						String packageCode = createPackageCode(receiptCode, index + 1);
						int pkgQuantity = mapPackageQuantity.getOrDefault(packageCode, 0) + 1;
						mapPackageQuantity.put(packageCode, pkgQuantity);
					}
				}
				for(Map.Entry<String, Integer> entry : mapPackageQuantity.entrySet()) {
					List<Map<String, Object>> pkgItems = result.getOrDefault(entry.getKey(), new ArrayList<Map<String, Object>>());
					Map<String, Object> item = new HashMap<String, Object>(manifestItem);
					item.put("quantity", entry.getValue());
					pkgItems.add(item);
					result.put(entry.getKey(), pkgItems);
				}
			}
		}
		
		return result;
	}
	/*
	 * Sinh package co index tu start toi end (bao gom ca end)
	 * Su dung ca trong truong hop update packageNumber len
	 */
	private List<Submission> createPackages(Submission receipt, int start, int end) {
		List<Submission> result = new ArrayList<Submission>();
		for(int ind = start; ind <= end; ind++) {
			Submission receiptPackage = createPackage(receipt, ind);
			receiptPackage.setField("totalPackage", end);
			result.add(receiptPackage);
		}
	
		return result;
	}
	private Submission createPackage(Submission receipt, Integer ind) {
		Submission pkgEntity = new Submission();
		String receiptCode = (String)SubmissionUtil.getFieldValue(receipt, RECEIPT_CODE);
		String packageCode = createPackageCode(receiptCode, ind);
		pkgEntity.setField(PACKAGE_CODE, packageCode);
		pkgEntity.setField(RECEIPT, Map.of(Submission.FORM, receipt.getForm(), Submission._ID, receipt.get_id()));
		setPackageData(pkgEntity, receipt);
		pkgEntity.setField("status", Status.Packing.INITED.getValue());
		return pkgEntity;
	}
	private void setPackageData(Submission pkgEntity, Submission receipt) {
		String[] fields = new String[]{"partner","detail",RECEIPT_CODE,"serviceType","recipient", "recipientPhone", "address", "note"};
		for(String field: fields) {
			pkgEntity.setField(field, SubmissionUtil.getFieldValue(receipt, field));
		}
		//Copy distrist, province, ward
		fields = new String[]{"province","district","ward"};
		for(String field: fields) {
			Object fieldValue = SubmissionUtil.getFieldValue(receipt, field);
			if (fieldValue instanceof Submission) {
				pkgEntity.setField(field, SubmissionUtil.getFieldValue((Submission)fieldValue, "name"));
			} else {
				pkgEntity.setField(field, fieldValue);
			}
			
		}
	}
	public ActionResult onCreatedReceipt(String formId, Submission receipt) {
		ActionResult result = new ActionResult();
		Object formPackageId = receipt.getExtraValue(FntService.PARAM_CREATING_FORM_ID);
		if (formPackageId != null) {
			Map<String, List<Map<String, Object>>> packageItems = allocateManifestItems(receipt);
			Map<String, Object> params = Map.of(FntService.PARAM_CREATING_FORM_ID, formPackageId);
			generatePackage(receipt, packageItems, params);
		}
		return result;
	}
	//Cap nhap lai so kien
	public ActionResult onCreatedPhieuNhapKho(String formId, Submission phieuNhapKho) {
		ActionResult result = new ActionResult();
		Object maLoFnt = SubmissionUtil.getFieldValue(phieuNhapKho, "maLoFnt");
		if (maLoFnt instanceof Submission) {
			Submission submissionHangVe = (Submission) maLoFnt;
			Object storedTime = SubmissionUtil.getFieldValue(submissionHangVe, "storedTime");
			if (storedTime == null) {
				submissionHangVe.setField("storedTime", new Date());
			}
			Object sokiennhap = SubmissionUtil.getFieldValue(phieuNhapKho, "packageCounter");
			Object packageCounter = SubmissionUtil.getFieldValue(submissionHangVe, "packageCounter");
			Object storedPackageCounter = SubmissionUtil.getFieldValue(submissionHangVe, "storedPackageCounter");
			int storedPackage = (storedPackageCounter instanceof Number) ? ((Number)storedPackageCounter).intValue() : 0;
			//So kien hang nhap kho tinh ca action hien tai
			if (sokiennhap instanceof Number) {
				storedPackage += ((Number) sokiennhap).intValue();
			}
			submissionHangVe.setField("storedPackageCounter", storedPackage);
			if(packageCounter instanceof Number) {
				int count = ((Number)packageCounter).intValue();
				if(count > storedPackage) {
					//Van con kien hang chua nhap
					submissionHangVe.setField("status", Status.LoHangVe.PARTLY_IMPORTED.getValue());
				} else if (count == storedPackage){
					//Da nhap kho toan bo lo hang
					submissionHangVe.setField("status", Status.LoHangVe.IMPORTED.getValue());
				} else {
					
				}
			}
			formioService.putSubmission(submissionHangVe.getForm(), submissionHangVe.get_id(), submissionHangVe);
			
		}
		return result;
	}
	public ActionResult onReceiptUpdated(String formId, Submission receipt) {
		ActionResult result = new ActionResult();
		String formPackageId = (String)receipt.getExtraValue(FntService.PARAM_CREATING_FORM_ID);
		if (formPackageId != null) {
			Object counter = SubmissionUtil.getFieldValue(receipt, "packageCounter");
			Integer packageCounter = 0;
			if (counter instanceof Number) {
				packageCounter = ((Number)counter).intValue();
			}
			MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(FntService.PARAM_CREATING_FORM_ID, formPackageId);
			//queryParams.putSingle("form", formPackageId);
			queryParams.putSingle("data.receipt._id", receipt.get_id());
			Map<String, List<Map<String, Object>>> mapPkgItems = allocateManifestItems(receipt);
			List<Submission> listPackages = formioService.getSubmissions(formPackageId, queryParams).getEntity();
			if (listPackages == null || listPackages.size() == 0) {
				generatePackage(receipt, mapPkgItems, params);
			} else {
				//Update manifestItems for old packages
				for (int i = 0; i < listPackages.size() && i < packageCounter; i++) {
					Submission pkgSubmission = listPackages.get(i);
					String pkgCode = (String)SubmissionUtil.getFieldValue(pkgSubmission, PACKAGE_CODE);
					List<Map<String, Object>> pkgItems = mapPkgItems.get(pkgCode);
					SubmissionUtil.setDataValue(pkgSubmission, MANIFEST_ITEMS, pkgItems);
					try {
						formioService.putSubmission(formPackageId, pkgSubmission.get_id(), pkgSubmission);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				formioService.getSubmissions(formPackageId, queryParams);
				if (listPackages.size() > packageCounter) {
					//Delete extra packages
					String receiptCode = (String)SubmissionUtil.getFieldValue(receipt, RECEIPT_CODE);
					for (Submission pkgEntity : listPackages) {
						String pkgCode = (String) SubmissionUtil.getFieldValue(pkgEntity, PACKAGE_CODE);
						String pkgIndex = pkgCode != null ? pkgCode.substring(receiptCode.length() + StringUtil.SEPARATOR_CODE.length()) : null;
						if (pkgIndex != null) {
							try {
								int ind = Integer.parseInt(pkgIndex);
								if (ind <= packageCounter) {
									pkgEntity.setField("totalPackage", packageCounter);
									setPackageData(pkgEntity, receipt);
									formioService.putSubmission(formPackageId, pkgEntity.get_id(), pkgEntity);
								} else {
									formioService.deleteSubmission(formPackageId, pkgEntity.get_id());
								}
							} catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
					//And update field TotalPackage of remain Packages
				} else if (listPackages.size() < packageCounter) {
					//Generate extra packages
					params.put(FntService.PARAM_START_INDEX, listPackages.size() + 1);
					params.put(FntService.PARAM_END_INDEX, packageCounter);
					generatePackage(receipt, mapPkgItems, params);
					for (Submission pkg : listPackages) {
						pkg.setField("totalPackage", packageCounter);
					}
				}
			}
		}
		return result;
	}
	private String createPackageCode(String receiptCode, Integer index) {
		String result = "";
		if (index != null) {
			int value = index.intValue();
			int counter = PACKAGE_CODE_LENGTH;
			while (value > 0 || counter > 0) {
				result = String.valueOf(value % 10) + result;
				value = value / 10;
				counter--;
			}
		}
		return receiptCode + StringUtil.SEPARATOR_CODE + result;
	}
	
	public ActionResult generateNhapKho(String formKienHangVe, Map<String, Object> requestParams) {
		ActionResult result = new ActionResult();
		Submission createdMaster;
		List<Submission> createdDetails = new ArrayList<Submission>();
		String formHangVe = (String) requestParams.get(REF_FORM);
		String loHangVeId = (String) requestParams.get(REF_SUBMISSION);
		Submission submissionHangVe = (formHangVe != null && loHangVeId != null) ? submissionUtil.getSubmissionById(formHangVe, loHangVeId) : null;
		if (submissionHangVe == null) return result;
		Object packageCounter = SubmissionUtil.getFieldValue(submissionHangVe, "packageCounter");
		Object storedPackageCounter = SubmissionUtil.getFieldValue(submissionHangVe, "storedPackageCounter");
		List<String> idKienHangVes = (List<String>)requestParams.get(SUBMISSION_IDS);
		String formHangNhapKho = (String) requestParams.get(FORM_HANG_NHAP_KHO);
		String formNhapKho = (String)requestParams.get(FORM_NHAP_KHO);
		List<Submission> listKienHangVe = submissionUtil.getSubmissionByIds(formKienHangVe, idKienHangVes);
		listKienHangVe.removeIf(submission -> {
			String status = (String)SubmissionUtil.getFieldValue(submission, "status");
			return Status.PackageStatus.STORED.equals(status);
		});
		if (listKienHangVe.size() > 0 && formNhapKho != null && formHangNhapKho != null) {
			//So kien hang da nhap kho truoc action hien tai
			int storedPackage = (storedPackageCounter instanceof Number) ? ((Number)storedPackageCounter).intValue() : 0;
			//So kien hang nhap kho tinh ca action hien tai
			storedPackage += listKienHangVe.size();
			submissionHangVe.setField("storedPackageCounter", storedPackage);
			if(packageCounter instanceof Number) {
				int count = ((Number)packageCounter).intValue();
				if(count > storedPackage) {
					//Van con kien hang chua nhap
					submissionHangVe.setField("status", Status.LoHangVe.PARTLY_IMPORTED.getValue());
				} else if (count == storedPackage){
					//Da nhap kho toan bo lo hang
					submissionHangVe.setField("status", Status.LoHangVe.IMPORTED.getValue());
				} else {
					
				}
			}
			formioService.putSubmission(formHangVe, loHangVeId, submissionHangVe);
			MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
			params.putSingle("data.maLoFnt._id", loHangVeId);
			List<String> maNhapKho = createImportCodes(formNhapKho, 1, params);
			Submission firstItem = listKienHangVe.get(0);
			createdMaster = new Submission(formNhapKho);
			createdMaster.setField("importDate", new Date());
			if (maNhapKho.size() > 0) {
				createdMaster.setField("importCode", maNhapKho.get(0));
			}
			createdMaster.setField("maLoFnt",Map.of(Submission.FORM, formHangVe, Submission._ID, loHangVeId));
			for(String field : new String[] {"mawb"}) {
				createdMaster.setField(field,SubmissionUtil.getFieldValue(firstItem, field));
			}
			createdMaster.setField("packageCounter", listKienHangVe.size());
			createdMaster.setField("createdUser", identity.getPrincipal().getName());
			createdMaster.setField("status", Status.Store.CREATED.getValue());
			//createdMaster.setField("note", "");
			createdMaster = formioService.createSubmission(formNhapKho, createdMaster);
			Map<String, String> ref = Map.of(Submission.FORM, formNhapKho, Submission._ID, createdMaster.get_id());
			for(Submission hangVe: listKienHangVe) {
				Submission hangNhapKho = new Submission(formHangNhapKho);
				hangNhapKho.setField("master", ref);
				hangNhapKho.setField("package", Map.of(Submission.FORM, formKienHangVe, Submission._ID, hangVe.get_id()));
				for(String field : new String[] {PACKAGE_CODE, "partner", "partnerCode"}) {
					hangNhapKho.setField(field,SubmissionUtil.getFieldValue(hangVe, field));
				}
				hangNhapKho.setField("category", Status.Store.NORMAL.getValue());
				hangNhapKho.setField("deliveryMethod", Status.Store.NORMAL.getValue());
				hangNhapKho.setField("status", Status.Store.CREATED.getValue());
				hangVe.setField("status", Status.PackageStatus.STORED.getValue());
				formioService.putSubmission(formKienHangVe, hangVe.get_id(), hangVe);
				hangNhapKho = formioService.createSubmission(formHangNhapKho, hangNhapKho);
				createdDetails.add(hangNhapKho);
			}
		}
		return result;
	}
	
	public ActionResult generateXuatKho(String formHangTrongKho, Map<String, Object> requestParams) {
		ActionResult result = new ActionResult();
		Submission createdMaster;
		List<Submission> createdDetails = new ArrayList<Submission>();
		List<String> submissionIds = (List<String>)requestParams.get(SUBMISSION_IDS);
		List<Submission> listPackages = submissionUtil.getSubmissionByIds(formHangTrongKho, submissionIds);
		String formHangXuatKho = (String) requestParams.get(FORM_HANG_XUAT_KHO);
		String formXuatKho = (String)requestParams.get(FORM_XUAT_KHO);
		if (listPackages.size() > 0 && formXuatKho != null && formHangXuatKho != null) {
			Submission firstItem = listPackages.get(0);
			MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
			//params.putSingle("data.maLoFnt._id", loHangVeId);
			List<String> exportCodes = createExportCodes(formXuatKho, 1, params);
			createdMaster = new Submission(formXuatKho);
			createdMaster.setField("exportDate", new Date());
			if (exportCodes.size() > 0) {
				createdMaster.setField("exportCode", exportCodes.get(0));
			}
			for(String field : new String[] {"maLoFnt", "mawb"}) {
				createdMaster.setField(field,SubmissionUtil.getFieldValue(firstItem, field));
			}
			createdMaster.setField("packageCounter", listPackages.size());
			createdMaster.setField("createdUser", identity.getPrincipal().getName());
			createdMaster.setField("status", Status.Store.CREATED.getValue());
			createdMaster.setField("shipper", "");
			//createdMaster.setField("note", "");
			createdMaster = formioService.createSubmission(formXuatKho, createdMaster);
			Map<String, String> ref = Map.of(Submission.FORM, formXuatKho, Submission._ID, createdMaster.get_id());
			for(Submission pkg: listPackages) {
				Submission hangXuatKho = new Submission(formHangXuatKho);
				hangXuatKho.setField("master", ref);
				hangXuatKho.setField("package", Map.of(Submission.FORM, formHangTrongKho, Submission._ID, pkg.get_id()));
				for(String field : new String[] {PACKAGE_CODE, "partner"}) {
					hangXuatKho.setField(field,SubmissionUtil.getFieldValue(pkg, field));
				}
				hangXuatKho.setField("category", Status.Store.NORMAL.getValue());
				hangXuatKho.setField("status", Status.Store.DELIVERING.getValue());
				hangXuatKho = formioService.createSubmission(formHangXuatKho, hangXuatKho);
				createdDetails.add(hangXuatKho);
			}
		}
		return result;
	}
	
	//Todo: for large system need some locking methods
	public List<String> generateDataFieldCode(String formId, String fieldName, String prefix, 
			int length, int counter, MultivaluedMap<String, String> params, String dateFormat) {
		if (params == null) {
			params = new MultivaluedHashMap<String, String>();
		}
		params.putSingle(FormioService.LIMIT, "1");
		params.putSingle(FormioService.SORT, "-created");
		params.putSingle(FormioService.SELECT,"data." + fieldName);
		List<String> result = new ArrayList<String>();
		Submission lastSubmission = submissionUtil.getLastSubmission(formId, Arrays.asList("data." + fieldName));
		String lastCode = (lastSubmission != null) ? (String)SubmissionUtil.getFieldValue(lastSubmission, fieldName) : null;
		Integer lastSequence = parseLastCode(lastCode, prefix, dateFormat);
		result = genCodes(prefix, length, lastSequence, counter, dateFormat);
		
		return result;
	}
	
	public List<String> createReceiptCode(String formReceipt, int counter) {
		List<String> result = generateDataFieldCode(formReceipt, RECEIPT_CODE, PREFIX_RECEIPT, RECEIPT_CODE_LENGTH, counter, null, "YY");		
		return result;
	}
	public List<String> createImportCodes(String formNhapKho, int counter, MultivaluedMap<String, String> params) {
		List<String> result = generateDataFieldCode(formNhapKho, "importCode", PREFIX_NHAP_KHO, NHAPKHO_CODE_LENGTH, counter, params, null);		
		
		return result;
	}
	
	public  List<String> createExportCodes(String formXuatKho, int counter, MultivaluedMap<String, String> params) {
		List<String> result = generateDataFieldCode(formXuatKho, "exportCode", PREFIX_XUAT_KHO, XUATKHO_CODE_LENGTH, counter, params, null);
		return result;
	}
	/*
	 * return last sequence from last code
	 */
	private Integer parseLastCode(String lastCode, String prefix, String dateFormat) {
		if (lastCode == null) return null;
		if (prefix != null) {
			lastCode = lastCode.substring(prefix.length());
		}
		int datePart =  (dateFormat != null && !dateFormat.isEmpty()) ? dateFormat.length() : 0;
//		if (dateFormat != null && !dateFormat.isEmpty()) {
//			YearMonth yearMonth = YearMonth.now();
//			try {
//				Integer year = Integer.parseInt(lastCode.substring(0,2));
//				if (year == yearMonth.getYear() % 2000) {
//					return Integer.parseInt(lastCode.substring(2));
//				} else {
//					return 0;
//				}
//				
//			} catch (Exception e) {
//				return null;
//			}
//		}
		try {
			return Integer.parseInt(lastCode.substring(datePart));
		} catch (Exception e) {
			return 0;
		}
	}
	private List<String> genCodes(String prefix, int length, Integer lastSequence, int counter, String dateFormat) {
		List<String> result = new ArrayList<String>();
		for(int ind = 1; ind <= counter; ind++) {
			StringBuilder sb = new StringBuilder(prefix);
			Integer val = (lastSequence != null) ? lastSequence + ind : ind;
			String seqStr = "";
			int i = 0, digit;
			do {
				digit = val % 10;
				val = val / 10;
				seqStr = String.valueOf(digit) + seqStr;
				i++;
			} while (i < length || digit > 0);
			if (dateFormat != null && !dateFormat.isEmpty()) {
				Date curDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				try {
					sb.append(sdf.format(curDate));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sb.append(seqStr);
			result.add(sb.toString());
		}
		return result;
	}
}
