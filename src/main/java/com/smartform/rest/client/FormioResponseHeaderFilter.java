package com.smartform.rest.client;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;

public class FormioResponseHeaderFilter implements ClientResponseFilter {
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		// TODO Auto-generated method stub
		MultivaluedMap<String, String> headers = responseContext.getHeaders();
		headers.remove(ACCESS_CONTROL_ALLOW_ORIGIN);
		
	}

}
