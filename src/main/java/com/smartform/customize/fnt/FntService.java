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

@ApplicationScoped
public class FntService {
	public static final int COMMON_CODE_LENGTH = 5;
	public static final int RECEIPT_CODE_LENGTH = 5;
	public static final int PACKAGE_CODE_LENGTH = 3;
	public static final String ACTION_GENERATE_PACKAGE = "generatePackage";
	public static final String ACTION_NHAP_KHO = "nhapKho";
	public static final String ACTION_XUAT_KHO = "xuatKho";
	public static final String FORM_NHAP_KHO = "form_nhapKho";
	public static final String FORM_XUAT_KHO = "nhapKho";
	public static final String FORM_HANG_NHAP_KHO = "form_hangNhapKho";
	public static final String FORM_HANG_XUAT_KHO = "form_hangXuatKho";
	public static final String SUBMISSION_IDS = "submissionIds";
    public static final String PREFIX_XUAT_KHO = "";
    public static final String PREFIX_NHAP_KHO = "";
    public static final String PREFIX_RECEIPT = "";
    
    
	@Inject
    SecurityIdentity identity;
	
	@Inject
	private SubmissionUtil submissionUtil;
	@RestClient
	FormioService formioService;
	
	public List<Submission> generatePackage(String formId, String submissionId, Map<String, Object> requestParams) {
		List<Submission> createdPackages = new ArrayList<Submission>();
		Submission receipt = formioService.getSubmission(formId, submissionId);
		receipt.setField("status", "packageGenerated");
		formioService.putSubmission(formId, receipt.get_id(), receipt);
		String createFormId = (String)requestParams.get("createFormId");
		if (createFormId != null ) {
			List<Submission> listPackages = createPackages(receipt);
			for(Submission packageSub : listPackages) {
				Submission createdPackage = formioService.createSubmission(createFormId, packageSub);
				createdPackages.add(createdPackage);
			}
		}
		return createdPackages;
	}
	private List<Submission> createPackages(Submission receipt) {
		List<Submission> result = new ArrayList<Submission>();
		Object value = SubmissionUtil.getFieldValue(receipt, "packageCounter");
		if (value instanceof Number) {
			for(int ind = 1; ind <= ((Number)value).intValue(); ind++) {
				Submission receiptPackage = createPackage(receipt, ind);
				receiptPackage.setField("totalPackage", value);
				result.add(receiptPackage);
			}
		}
		return result;
	}
	private Submission createPackage(Submission receipt, Integer ind) {
		Submission pkgEntity = new Submission();
		String receiptCode = (String)SubmissionUtil.getFieldValue(receipt, "receiptCode");
		String packageCode = createPackageCode(receiptCode, ind);
		pkgEntity.setField("packageCode", packageCode);
		pkgEntity.setField("receipt", Map.of(Submission.FORM, receipt.getForm(), Submission._ID, receipt.get_id()));
		for(String field: new String[]{"partner","detail", "receiptCode"}) {
			pkgEntity.setField(field, SubmissionUtil.getFieldValue(receipt, field));
		}
		pkgEntity.setField("status", Status.Packing.INITED.toValue());
		return pkgEntity;
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
	
	public ActionResult generateNhapKho(String formHangVe, Map<String, Object> requestParams) {
		ActionResult result = new ActionResult();
		Submission createdMaster;
		List<Submission> createdDetails = new ArrayList<Submission>();
		List<String> submissionIds = (List<String>)requestParams.get(SUBMISSION_IDS);
		String formHangNhapKho = (String) requestParams.get(FORM_HANG_NHAP_KHO);
		String formNhapKho = (String)requestParams.get(FORM_NHAP_KHO);
		List<Submission> listHangVe = submissionUtil.getSubmissionByIds(formHangVe, submissionIds);
		if (listHangVe.size() > 0) {
			List<String> maNhapKho = createImportCodes(formNhapKho, 1);
			Submission firstItem = listHangVe.get(0);
			createdMaster = new Submission(formNhapKho);
			createdMaster.setField("importDate", new Date());
			if (maNhapKho.size() > 0) {
				createdMaster.setField("importCode", maNhapKho.get(0));
			}
			for(String field : new String[] {"maLoFnt", "mawb"}) {
				createdMaster.setField(field,SubmissionUtil.getFieldValue(firstItem, field));
			}
			createdMaster.setField("packageCounter", listHangVe.size());
			createdMaster.setField("createdUser", identity.getPrincipal().getName());
			createdMaster.setField("status", Status.Store.CREATED.toString());
			//createdMaster.setField("note", "");
			createdMaster = formioService.createSubmission(formNhapKho, createdMaster);
			Map<String, String> ref = Map.of(Submission.FORM, formNhapKho, Submission._ID, createdMaster.get_id());
			for(Submission hangVe: listHangVe) {
				Submission hangNhapKho = new Submission(formHangNhapKho);
				hangNhapKho.setField("master", ref);
				hangNhapKho.setField("package", Map.of(Submission.FORM, formHangVe, Submission._ID, hangVe.get_id()));
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
		if (listPackages.size() > 0) {
			Submission firstItem = listPackages.get(0);
			String formHangXuatKho = (String) requestParams.get(FORM_HANG_XUAT_KHO);
			String formXuatKho = (String)requestParams.get(FORM_XUAT_KHO);
			List<String> exportCodes = createExportCodes(formXuatKho, 1);
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
	private List<String> createReceiptCode(String formReceipt, int counter) {
		List<String> result = new ArrayList<String>();
		Submission lastSubmission = submissionUtil.getLastSubmission(formReceipt, Arrays.asList("data.receiptCode"));
		String lastCode = (lastSubmission != null) ? (String)SubmissionUtil.getFieldValue(lastSubmission, "receiptCode") : null;
		Integer lastSequence = parseLastCode(lastCode, PREFIX_RECEIPT);
		result = genCodes(PREFIX_RECEIPT, COMMON_CODE_LENGTH, lastSequence, counter);
		
		return result;
	}
	private List<String> createImportCodes(String formNhapKho, int counter) {
		List<String> result = new ArrayList<String>();
		Submission lastSubmission = submissionUtil.getLastSubmission(formNhapKho, Arrays.asList("data.importCode"));
		String lastCode = (lastSubmission != null) ? (String)SubmissionUtil.getFieldValue(lastSubmission, "importCode") : null;
		Integer lastSequence = parseLastCode(lastCode, PREFIX_NHAP_KHO);
		result = genCodes(PREFIX_NHAP_KHO, COMMON_CODE_LENGTH, lastSequence, counter);
		
		return result;
	}
	
	private  List<String> createExportCodes(String formXuatKho, int counter) {
		List<String> result = new ArrayList<String>();
		Submission lastSubmission = submissionUtil.getLastSubmission(formXuatKho, Arrays.asList("data.exportCode"));
		String lastCode = (lastSubmission != null) ? (String)SubmissionUtil.getFieldValue(lastSubmission, "exportCode") : null;
		Integer lastSequence = parseLastCode(lastCode, PREFIX_XUAT_KHO);
		result = genCodes(PREFIX_XUAT_KHO, COMMON_CODE_LENGTH, lastSequence, counter);
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
	private List<String> genCodes(String prefix, int length, Integer lastSequence, int counter) {
		List<String> result = new ArrayList<String>();
		for(int ind = 1; ind <= counter; ind++) {
			StringBuilder sb = new StringBuilder(prefix);
			Integer val = (lastSequence != null) ? lastSequence + ind : ind;
			String seqStr = "";
			for (int i = 0; i < length; i++) {
				Integer digit = val % 10;
				val = val / 10;
				seqStr = String.valueOf(digit) + seqStr;
			}
			Date curDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YY");
			sb.append(sdf.format(curDate)).append(seqStr);

			result.add(sb.toString());
		}
		return result;
	}
}
