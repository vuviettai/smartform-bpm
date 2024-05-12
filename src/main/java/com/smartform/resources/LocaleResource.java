package com.smartform.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

import com.smartform.rest.client.FormioClient;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/locales")
public class LocaleResource {
	
	@RestClient 
	@Inject
    FormioClient formioService;
	
	@Path("/{lang}/{ns}.json")
	@GET
	public Map<String,Object> getLocale(@RestPath String lang, String ns) {
		Map<String, Object> localeResult = new HashMap<String, Object>();
		
		return localeResult;
	}
}
