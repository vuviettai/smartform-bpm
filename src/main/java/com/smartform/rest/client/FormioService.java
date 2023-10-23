package com.smartform.rest.client;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.Path;

@Path("/formio")
@RegisterRestClient
public interface FormioService {

}
