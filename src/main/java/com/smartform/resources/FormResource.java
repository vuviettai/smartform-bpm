package com.smartform.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

import com.smartform.rest.client.FormioService;
import com.smartform.rest.client.FormsflowService;
import com.smartform.rest.model.Formsflow;
import com.smartform.rest.model.FormsflowPage;

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
			Map<String,Formsflow> mapForms = new HashMap<String, Formsflow>();
			//Get forms
			FormsflowPage forms = formsflowService.getForms(1, Integer.MAX_VALUE, sortBy, sortOrder, FORM_TYPE_FORM, null);
			result.setTotalCount(forms.getTotalCount());
			for (Formsflow form : forms.getForms()) {
				if (form.getFormType() == null) {
					form.setFormType(FORM_TYPE_FORM);
				}
				mapForms.put(form.getFormId(), form);
			}
			result.getForms().addAll(forms.getForms());
			/*
			 * Get resouces
			 * Need two call due to processing in formsflow.
			 */
			
			FormsflowPage resources = formsflowService.getForms(1, Integer.MAX_VALUE, sortBy, sortOrder, FORM_TYPE_RESOURCE, null);
			for (Formsflow form : resources.getForms()) {
				Formsflow loadedForm = mapForms.get(form.getFormId());
				if (loadedForm != null) {
					loadedForm.setFormType(null);
				} else {
					form.setFormType(FORM_TYPE_RESOURCE);
					result.getForms().add(form);
					result.increaseTotalCount(1);
				}
			}
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
	public Formsflow getForm(@RestPath String formId) {
		Formsflow formsflow = formsflowService.getById(formId);
		return formsflow;
	}
}
