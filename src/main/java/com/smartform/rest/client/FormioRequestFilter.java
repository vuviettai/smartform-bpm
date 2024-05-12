package com.smartform.rest.client;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.UriBuilder;

public class FormioRequestFilter implements ClientRequestFilter {

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
         //requestContext.setUri(URI.create(requestContext.getUri() + ""));
         if (requestContext.getMethod().equals(HttpMethod.GET)) {
        	 Object entity = requestContext.getEntity();
        	 URI uri = requestContext.getUri();
             UriBuilder uriBuilder = UriBuilder.fromUri(uri);
             if (entity instanceof Map) {
	             Map allParam = (Map)entity;
	             for (Object key : allParam.keySet()) {
	                 uriBuilder.queryParam(key.toString(), allParam.get(key));
	             }
	             requestContext.setUri(uriBuilder.build());
	             requestContext.setEntity(null);
             }
         }
	}

}
