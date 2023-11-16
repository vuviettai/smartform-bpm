package com.smartform.resources;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

import com.smartform.rest.client.FormioService;
import com.smartform.rest.client.FormsflowService;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/form")
public class FormResource {
	
	@RestClient 
    FormioService formioService;
	
	@RestClient 
    FormsflowService formsflowService;
	
	@GET
	@Path("/{formId}")
	public String getForm(@RestPath String formId) {
		return formId;
	}
}
