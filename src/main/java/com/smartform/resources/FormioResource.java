package com.smartform.resources;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

import com.smartform.rest.client.FormioService;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;

@Path("/form")
public class FormioResource {

	@RestClient 
    FormioService formioService;
	
	@Path("/{formId}")
	@GET
	public FormioForm getForm(@RestPath String formId) {
		FormioForm formioForm = null;
		try {
			formioForm = formioService.getForm(formId);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		return formioForm;
	}
	
	@Path("/{formId}/submission/{submissionId}")
	@GET
	public Submission getSubmission(@RestPath String formId, @RestPath String submissionId) {
		Submission submission = null;
		try {
			submission = formioService.getSubmission(formId, submissionId);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return submission;
	}
}
