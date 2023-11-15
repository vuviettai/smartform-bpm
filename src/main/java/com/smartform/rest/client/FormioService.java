package com.smartform.rest.client;

import java.util.List;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

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
	List<Submission> getSubmissions(@PathParam("formId") String formId);
	
	@GET
	@Path("{formId}/submission/{submissionId}")
	Submission getSubmission(@PathParam("formId") String formId, @PathParam("submissionId") String submissionId);
}
