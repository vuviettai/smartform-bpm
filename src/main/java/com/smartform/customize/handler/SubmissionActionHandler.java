package com.smartform.customize.handler;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import com.smartform.customize.fnt.FntService;
import com.smartform.customize.vgec.CommissionPolicy;
import com.smartform.customize.vgec.CommissionService;
import com.smartform.models.ActionResult;
import com.smartform.rest.client.FormioClient;
import com.smartform.rest.model.Submission;
import com.smartform.utils.SubmissionUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;


/*
 * Hard code logic
 * Todo: Improve logic by Dependency Injection
 */
@ApplicationScoped
public class SubmissionActionHandler {
	public static final String ACTION = "action";
	
	@RestClient
	@Inject
	FormioClient formioService;
	@Inject
	SubmissionUtil submissionUtil;
	@Inject
	CommissionService commissionService;
	@Inject
	FntService fntService;
	
	public ActionResult handleAction(String formId, String submissionId, Map<String, Object> params) {
		ActionResult result = new ActionResult();
		String actionName = (String) params.get(ACTION);
		if (CommissionService.ACTION_CALCULATE.equalsIgnoreCase(actionName)) {
			try {
				MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
				List<Submission> detailCommissions = null;
				//queryParams.add("name", CommissionPolicy.FORM_DETAIL);
//				List<FormioForm> detailForms = formioService.queryForms(queryParams);
//				if (detailForms != null && detailForms.size() >= 1) {
//					RestResponse<List<Submission>> response = formioService.getSubmissions(detailForms.get(0).get_id(),
//							null);
//					detailCommissions = response.getEntity();
//				}
				String formDetailId = (String)params.get(CommissionPolicy.FORM_DETAIL);
				if (formDetailId != null) {
					RestResponse<List<Submission>> response = formioService.getSubmissions(formDetailId, null);
					detailCommissions = response.getEntity();
				}
				if (detailCommissions != null && detailCommissions.size() > 0) {
					// Load reference;
					submissionUtil.loadReferenceSubmissions(detailCommissions);
					Submission headerSubmission = (Submission) SubmissionUtil.getFieldValue(detailCommissions.get(0),
							"policy");
					CommissionPolicy commissionPolicy = new CommissionPolicy(headerSubmission, detailCommissions,
							params);
					result = commissionService.calculateCommision(commissionPolicy);
				}
			} catch (WebApplicationException e) {
				e.printStackTrace();
			}
		}
		if (FntService.ACTION_GENERATE_PACKAGE.equalsIgnoreCase(actionName)) {
			Submission receipt = formioService.getSubmission(formId, submissionId);
			if (receipt != null) {
				Map<String, List<Map<String, Object>>> mapPackageItems = fntService.allocateManifestItems(receipt);
				fntService.generatePackage(receipt, mapPackageItems, params);
			}
		}
		return result;
	}
	
	public ActionResult prepareSubmission(String formId, Submission submission, String customAction) {
		ActionResult result = new ActionResult();
		if (FntService.ACTION_SUBMIT_RECEIPT.equalsIgnoreCase(customAction)) {
			//Generate receiptCode
			String dataField = FntService.RECEIPT_CODE;
			String currentValue = (String)SubmissionUtil.getFieldValue(submission, dataField);
			if (currentValue == null || currentValue.isEmpty()) {
				List<String> receiptCodes = fntService.generateDataFieldCode(formId, dataField, FntService.PREFIX_RECEIPT, FntService.RECEIPT_CODE_LENGTH, 1, null, "YY");
				if (receiptCodes != null && receiptCodes.size() == 1) {
					submission.setField(dataField, receiptCodes.get(0));
				}
			}
		} else if (FntService.ACTION_SUBMIT_PHIEUNHAPKHO.equalsIgnoreCase(customAction)) {
			//Generate receiptCode
			String dataField = "importCode";
			String currentValue = (String)SubmissionUtil.getFieldValue(submission, dataField);
			if (currentValue == null || currentValue.isEmpty()) {
				MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
				Object maLoFnt = SubmissionUtil.getFieldValue(submission, "maLoFnt");
				if (maLoFnt instanceof Map) {
					params.putSingle("data.maLoFnt._id", (String)((Map)maLoFnt).get(Submission._ID));
				}
				
				List<String> fieldCodes = fntService.generateDataFieldCode(formId, dataField, FntService.PREFIX_NHAP_KHO, FntService.NHAPKHO_CODE_LENGTH, 1, params, null);
				if (fieldCodes != null && fieldCodes.size() == 1) {
					submission.setField(dataField, fieldCodes.get(0));
				}
			}
		} else if (FntService.ACTION_SUBMIT_PHIEUXUATKHO.equalsIgnoreCase(customAction)) {
			//Generate receiptCode
			String dataField = "exportCode";
			String currentValue = (String)SubmissionUtil.getFieldValue(submission, dataField);
			if (currentValue == null || currentValue.isEmpty()) {
				MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
				List<String> fieldCodes = fntService.generateDataFieldCode(formId, dataField, FntService.PREFIX_XUAT_KHO, FntService.XUATKHO_CODE_LENGTH, 1, params, null);
				if (fieldCodes != null && fieldCodes.size() == 1) {
					submission.setField(dataField, fieldCodes.get(0));
				}
			}
		}
		return result;
	}
	public ActionResult onSubmissionCreated(String formId, Submission submission, String customAction) {
		ActionResult result = new ActionResult();
		if (FntService.ACTION_SUBMIT_RECEIPT.equalsIgnoreCase(customAction)) {
			result = fntService.onCreatedReceipt(formId, submission);
		} else if (FntService.ACTION_SUBMIT_PHIEUNHAPKHO.equalsIgnoreCase(customAction)) {
			result = fntService.onCreatedPhieuNhapKho(formId, submission);
		}
		return result;
	}
	public ActionResult onSubmissionUpdated(String formId, Submission submission, String customAction) {
		ActionResult result = new ActionResult();
		if (FntService.ACTION_SUBMIT_RECEIPT.equalsIgnoreCase(customAction)) {
			result = fntService.onReceiptUpdated(formId, submission);
		}
		return result;
	}
}
