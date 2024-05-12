package com.smartform.rest.client;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class FormsflowClientHeaderFactory implements ClientHeadersFactory {
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_FORMIO_JWT = "X-Jwt-Token";
	public static final String TOKEN_BEARER = "Bearer";
	@ConfigProperty(name = "formio.jwt-secret")
    String jwtSecret;
	@Override
	public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
			MultivaluedMap<String, String> clientOutgoingHeaders) {
		// TODO Auto-generated method stub
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        List<String> bearToken = incomingHeaders.get(HEADER_AUTHORIZATION.toLowerCase());
        result.addAll(HEADER_AUTHORIZATION, bearToken);
        return result;
	}
}
