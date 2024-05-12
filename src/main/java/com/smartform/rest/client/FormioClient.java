package com.smartform.rest.client;

import java.util.List;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;

import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MultivaluedMap;


@Path("/form")
@RegisterRestClient(configKey = "formio-api")
@RegisterProvider(FormioResponseHeaderFilter.class)		//Remove header, use config from rest resource
@RegisterClientHeaders(FormioClientHeaderFactory.class)
//@ClientHeaderParam(name = "X-Jwt-Token", value = "${formio.jwt-secret}")
public interface FormioClient {
	public static final String LIMIT 	= "limit";
	public static final String SORT 	= "sort";
	public static final String SELECT 	= "select";
	@GET
	@Path("{formId}")
	FormioForm getForm(@PathParam("formId") String formId);
	
	@GET
	@Path("")
	List<FormioForm> queryForms(@RestQuery MultivaluedMap<String, String> queryParams);
	
	@GET
	@Path("{formId}/submission")
	RestResponse<List<Submission>> getSubmissions(@PathParam("formId") String formId,
			@RestQuery MultivaluedMap<String, String> queryParams
//			@QueryParam("limit") Integer limit, 
//			@QueryParam("skip") Integer skip,
//			@QueryParam("select") String select,
//			@QueryParam("sort") String sort
			);
	
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
