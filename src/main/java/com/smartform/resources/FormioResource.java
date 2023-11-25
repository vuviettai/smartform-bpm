package com.smartform.resources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

import com.smartform.rest.client.FormioService;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;
import com.smartform.rest.model.Submissions;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
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
	@Path("/{formId}/submission")
	@GET
	public List<Submission> getSubmissions(@RestPath String formId, 
			@QueryParam("limit") Integer limit, 
			@QueryParam("skip") Integer skip,
			@QueryParam("select") String select,
			@QueryParam("sort") String sort) {
		List<Submission> submissions = null;
		try {
			submissions = formioService.getSubmissions(formId, limit, skip, select, sort);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return submissions;
	}
	@Path("/{formId}/submission")
	@POST
	public Submission createSubmission(@RestPath String formId, Submission submission) {
		Submission createdSubmission = null;
		try {
			createdSubmission = formioService.createSubmission(formId, submission);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return createdSubmission;
	}
	@Path("/{formId}/submission/upload")
	@POST
	public List<Submission> uploadSubmissions(@RestPath String formId, Submissions submissions) {
		List<Submission> uploadedSubmissions = new ArrayList<Submission>();
		if (submissions != null) {
			List<Submission> payload = submissions.toSubmissionList();
			for(Submission submission : payload) {
				try {
					Submission createdSubmission = formioService.createSubmission(formId, submission);
					uploadedSubmissions.add(createdSubmission);
				} catch (WebApplicationException e) {
					e.printStackTrace();
				}
			}
		}
		return uploadedSubmissions;
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
	
	@Path("/{formId}/submission/{submissionId}")
	@PUT
	public Submission putSubmission(@RestPath String formId, @RestPath String submissionId, Submission submission) {
		Submission updated = null;
		try {
			updated = formioService.putSubmission(formId, submissionId, submission);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return updated;
	}
	@Path("/{formId}/submission/{submissionId}")
	@DELETE
	public Submission deleteSubmission(@RestPath String formId, @RestPath String submissionId) {
		Submission deleted = null;
		try {
			deleted = formioService.deleteSubmission(formId, submissionId);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return deleted;
	}
}
