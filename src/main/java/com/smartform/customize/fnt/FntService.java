package com.smartform.customize.fnt;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
	public static final String ACTION_NHAP_KHO = "nhapKho";
	public static final String ACTION_XUAT_KHO = "xuatKho";
	public static final String FORM_HANG_VE = "form_hangve";
	public static final String FORM_PACKAGE = "form_package";
	public static final String FORM_NHAP_KHO = "form_nhapKho";
	public static final String FORM_XUAT_KHO = "form_xuatKho";
	public static final String FORM_HANG_NHAP_KHO = "form_hangNhapKho";
	public static final String FORM_HANG_XUAT_KHO = "form_hangXuatKho";
	public static final String SUBMISSION_IDS = "submissionIds";
	public static final String SUBMISSION_REF = "submissionRef";
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
	
	public List<Submission> generatePackage(Submission receipt, Map<String, Object> requestParams) {
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
			if (end > start) {
			List<Submission> listPackages = createPackages(receipt, start, end);
				for(Submission packageSub : listPackages) {
					Submission createdPackage = formioService.createSubmission(createFormId, packageSub);
					createdPackages.add(createdPackage);
				}
			}
		}
		return createdPackages;
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
		String receiptCode = (String)SubmissionUtil.getFieldValue(receipt, "maLoFnt");
		String packageCode = createPackageCode(receiptCode, ind);
		pkgEntity.setField("packageCode", packageCode);
		pkgEntity.setField("loFnt", Map.of(Submission.FORM, receipt.getForm(), Submission._ID, receipt.get_id()));
		String[] fields = new String[]{"partner","detail","maLoFnt","serviceType","recipient", "address", "note"};
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
		pkgEntity.setField("status", Status.Packing.INITED.toValue());
		return pkgEntity;
	}
	public ActionResult onReceiptCreated(String formId, Submission receipt) {
		ActionResult result = new ActionResult();
		Object formPackageId = receipt.getExtraValue(FntService.PARAM_CREATING_FORM_ID);
		if (formPackageId != null) {
			Map<String, Object> params = Map.of(FntService.PARAM_CREATING_FORM_ID, formPackageId);
			generatePackage(receipt, params);
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
			Map<String, Object> params = Map.of(FntService.PARAM_CREATING_FORM_ID, formPackageId);
			queryParams.putSingle("form", formPackageId);
			queryParams.putSingle("data.maLoFnt._id", receipt.get_id());
			List<Submission> listPackages = formioService.getSubmissions((String)formPackageId, queryParams).getEntity();
			if (listPackages == null || listPackages.size() == 0) {
				generatePackage(receipt, params);
			} else if (listPackages.size() > packageCounter) {
				//Delete extra packages
				String receiptCode = (String)SubmissionUtil.getFieldValue(receipt, "receiptCode");
				for (Submission pkg : listPackages) {
					String pkgCode = (String) SubmissionUtil.getFieldValue(pkg, "packageCode");
					String pkgIndex = pkgCode != null ? pkgCode.substring(receiptCode.length() + StringUtil.SEPARATOR_CODE.length()) : null;
					if (pkgIndex != null) {
						try {
							int ind = Integer.parseInt(pkgIndex);
							if (ind <= packageCounter) {
								pkg.setField("totalPackage", packageCounter);
								formioService.putSubmission(formPackageId, pkg.get_id(), pkg);
							} else {
								formioService.deleteSubmission(formPackageId, pkg.get_id());
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
				generatePackage(receipt, params);
				for (Submission pkg : listPackages) {
					pkg.setField("totalPackage", packageCounter);
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
		String formHangVe = (String) requestParams.get(FORM_HANG_VE);
		String loHangVeId = (String) requestParams.get(SUBMISSION_REF);
		Submission submissionHangVe = submissionUtil.getSubmissionById(formHangVe, loHangVeId);
		Object packageNumber = SubmissionUtil.getFieldValue(submissionHangVe, "packageNumber");
		Object importedPackageNumber = SubmissionUtil.getFieldValue(submissionHangVe, "importedPackageNumber");
		List<String> idKienHangVes = (List<String>)requestParams.get(SUBMISSION_IDS);
		String formHangNhapKho = (String) requestParams.get(FORM_HANG_NHAP_KHO);
		String formNhapKho = (String)requestParams.get(FORM_NHAP_KHO);
		List<Submission> listKienHangVe = submissionUtil.getSubmissionByIds(formKienHangVe, idKienHangVes);
		if (listKienHangVe.size() > 0 && formNhapKho != null && formHangNhapKho != null) {
			int importPackage = (importedPackageNumber instanceof Number) ? ((Number)importedPackageNumber).intValue() : 0;
			if(packageNumber instanceof Number) {
				int count = ((Number)packageNumber).intValue();
				if(count > listKienHangVe.size()) {
					submissionHangVe.setField("status", Status.LoHangVe.PARTLY_IMPORTED.toString());
					submissionHangVe.setField("importedPackageNumber", importPackage + listKienHangVe.size());
				} else if (count == listKienHangVe.size()){
					submissionHangVe.setField("status", Status.LoHangVe.IMPORTED.toString());
					submissionHangVe.setField("importedPackageNumber", importPackage + listKienHangVe.size());
				} else {
					
				}
			}
			MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
			params.putSingle("data.maLoFnt._id", loHangVeId);
			List<String> maNhapKho = createImportCodes(formNhapKho, 1, params);
			Submission firstItem = listKienHangVe.get(0);
			createdMaster = new Submission(formNhapKho);
			createdMaster.setField("importDate", new Date());
			if (maNhapKho.size() > 0) {
				createdMaster.setField("importCode", maNhapKho.get(0));
			}
			for(String field : new String[] {"maLoFnt", "mawb"}) {
				createdMaster.setField(field,SubmissionUtil.getFieldValue(firstItem, field));
			}
			createdMaster.setField("packageCounter", listKienHangVe.size());
			createdMaster.setField("createdUser", identity.getPrincipal().getName());
			createdMaster.setField("status", Status.Store.CREATED.toString());
			//createdMaster.setField("note", "");
			createdMaster = formioService.createSubmission(formNhapKho, createdMaster);
			Map<String, String> ref = Map.of(Submission.FORM, formNhapKho, Submission._ID, createdMaster.get_id());
			for(Submission hangVe: listKienHangVe) {
				Submission hangNhapKho = new Submission(formHangNhapKho);
				hangNhapKho.setField("master", ref);
				hangNhapKho.setField("package", Map.of(Submission.FORM, listKienHangVe, Submission._ID, hangVe.get_id()));
				for(String field : new String[] {"packageCode", "partner"}) {
					hangNhapKho.setField(field,SubmissionUtil.getFieldValue(hangVe, field));
				}
				hangNhapKho.setField("category", Status.Store.NORMAL.toString());
				hangNhapKho.setField("deliveryMethod", Status.Store.NORMAL.toString());
				hangNhapKho.setField("status", Status.Store.CREATED.toString());
				
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
			createdMaster.setField("status", Status.Store.CREATED.toString());
			createdMaster.setField("shipper", "");
			//createdMaster.setField("note", "");
			createdMaster = formioService.createSubmission(formXuatKho, createdMaster);
			Map<String, String> ref = Map.of(Submission.FORM, formXuatKho, Submission._ID, createdMaster.get_id());
			for(Submission pkg: listPackages) {
				Submission hangXuatKho = new Submission(formHangXuatKho);
				hangXuatKho.setField("master", ref);
				hangXuatKho.setField("package", Map.of(Submission.FORM, formHangTrongKho, Submission._ID, pkg.get_id()));
				for(String field : new String[] {"packageCode", "partner"}) {
					hangXuatKho.setField(field,SubmissionUtil.getFieldValue(pkg, field));
				}
				hangXuatKho.setField("category", Status.Store.NORMAL.toString());
				hangXuatKho.setField("status", Status.Store.DELIVERING.toString());
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
		Integer lastSequence = parseLastCode(lastCode, prefix);
		result = genCodes(prefix, length, lastSequence, counter, dateFormat);
		
		return result;
	}
	
	public List<String> createReceiptCode(String formReceipt, int counter) {
		List<String> result = generateDataFieldCode(formReceipt, "receiptCode", PREFIX_RECEIPT, RECEIPT_CODE_LENGTH, counter, null, "YY");		
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
	private Integer parseLastCode(String lastCode, String prefix) {
		if (lastCode == null) return null;
		if (prefix != null) {
			lastCode = lastCode.substring(prefix.length());
		}
		YearMonth yearMonth = YearMonth.now();
		try {
			Integer year = Integer.parseInt(lastCode.substring(0,2));
			if (year == yearMonth.getYear() % 2000) {
				return Integer.parseInt(lastCode.substring(2));
			} else {
				return 0;
			}
			
		} catch (Exception e) {
			return null;
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
			if (dateFormat != null || dateFormat.isEmpty()) {
				Date curDate = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				try {
					sb.append(sdf.format(curDate)).append(seqStr);
					result.add(sb.toString());
				} catch (Exception e) {
					
				}
			}
		}
		return result;
	}
}
