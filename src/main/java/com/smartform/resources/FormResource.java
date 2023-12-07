package com.smartform.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

import com.smartform.rest.client.FormioService;
import com.smartform.rest.client.FormsflowService;
import com.smartform.rest.model.Formsflow;
import com.smartform.rest.model.FormsflowPage;
import com.smartform.rest.model.Submission;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;

@Path("/formsflow")
public class FormResource {
	public static String FORM_TYPE_FORM = "form";
	public static String FORM_TYPE_RESOURCE = "resource";
	@RestClient 
    FormioService formioService;
	
	@RestClient 
    FormsflowService formsflowService;
	
	@GET
	@Path("/all")
	public FormsflowPage getAllForms(
			@QueryParam("sortBy") String sortBy,
			@QueryParam("sortOrder") String sortOrder) {
		FormsflowPage result = new FormsflowPage();
		result.setForms(new ArrayList<Formsflow>());
		result.setTotalCount(0);
		try {
			String formTypes = FORM_TYPE_FORM + "," + FORM_TYPE_RESOURCE;
			FormsflowPage forms = formsflowService.getForms(1, Integer.MAX_VALUE, sortBy, sortOrder, formTypes, null);
			result.setTotalCount(result.getTotalCount() + forms.getTotalCount());
			for (Formsflow form : forms.getForms()) {
				if (form.getFormType() == null) {
					form.setFormType(FORM_TYPE_FORM);
				}
				result.getForms().add(form);
			}
//			FormsflowPage resources = formsflowService.getForms(1, Integer.MAX_VALUE, sortBy, sortOrder, FORM_TYPE_RESOURCE, null);
//			result.setTotalCount(result.getTotalCount() + resources.getTotalCount());
//			for (Formsflow form : resources.getForms()) {
//				form.setFormType(FORM_TYPE_RESOURCE);
//				result.getForms().add(form);
//			}
			result.setLimit(Integer.MAX_VALUE);
			result.setPageNo(1);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@GET
	public FormsflowPage getForms(
			@QueryParam("pageNo") Integer pageNo, 
			@QueryParam("limit") Integer limit, 
			@QueryParam("sortBy") String sortBy,
			@QueryParam("sortOrder") String sortOrder,
			@QueryParam("formType") String formType,
			@QueryParam("formName") String formName) {
		FormsflowPage formsPage = null;
		try {
			formsPage = formsflowService.getForms(pageNo, limit, sortBy, sortOrder, formType, formName);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return formsPage;
	}
	@GET
	@Path("/{formId}")
	public String getForm(@RestPath String formId) {
		return formId;
	}
}
