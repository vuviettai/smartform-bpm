package com.smartform.rest.client;

import java.util.Set;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.smartform.rest.model.FormioForm;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/formsflow")
@RegisterRestClient(configKey = "formsflow-api")
public interface FormsflowService {
    @GET
    Set<FormioForm> getById(@QueryParam("id") String id);
}
