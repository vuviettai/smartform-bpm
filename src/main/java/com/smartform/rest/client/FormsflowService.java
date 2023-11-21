package com.smartform.rest.client;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.smartform.rest.model.Formsflow;
import com.smartform.rest.model.FormsflowPage;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/form")
@RegisterRestClient(configKey = "formsflow-api")
@RegisterClientHeaders(FormsflowClientHeaderFactory.class)
public interface FormsflowService {
	
	@GET
	FormsflowPage getForms(
			@QueryParam("pageNo") Integer pageNo, 
			@QueryParam("limit") Integer limit, 
			@QueryParam("sortBy") String sortBy,
			@QueryParam("sortOrder") String sortOrder,
			@QueryParam("formType") String formType,
			@QueryParam("formName") String formName);
    @GET
    @Path("{formId}")
    Formsflow getById(@QueryParam("id") String id);
}
