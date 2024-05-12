package com.smartform.customize.handler;

import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.smartform.customize.fnt.FntService;
import com.smartform.customize.vgec.CommissionService;
import com.smartform.models.ActionResult;
import com.smartform.rest.client.FormioService;
import com.smartform.utils.SubmissionUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


/*
 * Hard code logic
 * Todo: Improve logic by Dependency Injection
 */
@ApplicationScoped
public class FormActionHandler {
	public static final String ACTION = "action";
	@RestClient
	@Inject
	FormioService formioService;
	@Inject
	SubmissionUtil submissionUtil;
	@Inject
	CommissionService commissionService;
	@Inject
	FntService fntService;
	
	public ActionResult handleAction(String formId, Map<String, Object> params) {
		ActionResult result = new ActionResult();
		String actionName = (String) params.get(ACTION);
		if (FntService.ACTION_NHAP_KHO.equalsIgnoreCase(actionName)) {
			result = fntService.generateNhapKho(formId, params);
		} else if (FntService.ACTION_XUAT_KHO.equalsIgnoreCase(actionName)) {
			result = fntService.generateXuatKho(formId, params);
		}
		return result;
	}
}
