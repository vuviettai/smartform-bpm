package com.smartform.customize.fnt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.smartform.models.SubmissionRef;
import com.smartform.rest.client.FormioService;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;
import com.smartform.utils.StringUtil;
import com.smartform.utils.SubmissionUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class FntService {
	public static final int RECEIPT_CODE_LENGTH = 5;
	public static final int PACKAGE_CODE_LENGTH = 3;
	public static final String ACTION_GENERATE_PACKAGE = "generatePackage";
    
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
}
