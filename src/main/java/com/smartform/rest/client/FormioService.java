package com.smartform.rest.client;

import java.util.List;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

@Path("/form")
@RegisterRestClient(configKey = "formio-api")
@RegisterClientHeaders(FormioClientHeaderFactory.class)
//@ClientHeaderParam(name = "X-Jwt-Token", value = "${formio.jwt-secret}")
public interface FormioService {
	
	@GET
	@Path("{formId}")
	FormioForm getForm(@PathParam("formId") String formId);
	
	@GET
	@Path("{formId}/submission")
	List<Submission> getSubmissions(@PathParam("formId") String formId, 
			@QueryParam("limit") Integer limit, 
			@QueryParam("skip") Integer skip,
			@QueryParam("select") String select,
			@QueryParam("sort") String sort);
	
	@POST
	@Path("{formId}/submission")
	Submission createSubmission(@PathParam("formId") String formId, Submission submission);

	
	@GET
	@Path("{formId}/submission/{submissionId}")
	Submission getSubmission(@PathParam("formId") String formId, @PathParam("submissionId") String submissionId);
	
	@PUT
	@Path("{formId}/submission/{submissionId}")
	Submission putSubmission(@PathParam("formId") String formId, @PathParam("submissionId") String submissionId, Submission submission);

	@DELETE
	@Path("{formId}/submission/{submissionId}")
	Submission deleteSubmission(@PathParam("formId") String formId, @PathParam("submissionId") String submissionId);

}
