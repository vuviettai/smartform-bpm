package com.smartform.rest.client;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.jose4j.jwt.JwtClaims;

import com.smartform.utils.JwkUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class FormioClientHeaderFactory implements ClientHeadersFactory {
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_FORMIO_JWT = "X-Jwt-Token";
	public static final String TOKEN_BEARER = "Bearer";
	@Override
	public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
			MultivaluedMap<String, String> clientOutgoingHeaders) {
		// TODO Auto-generated method stub
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>(incomingHeaders);
        List<String> bearerTokens = incomingHeaders.get(HEADER_AUTHORIZATION);
        if (bearerTokens != null && bearerTokens.size() > 0) {
        	String token = bearerTokens.get(0).substring(TOKEN_BEARER.length() + 1);
        	try {
				JwtClaims claims = JwkUtil.parseJWT(token);
				String formioJwt = createJwt(claims);
				System.out.println("Formio Jwt");
				System.out.println(formioJwt);
				//result.add(HEADER_FORMIO_JWT, formioJwt);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //incomingHeaders.get(result)
        //result.add("X-request-uuid", UUID.randomUUID().toString());
        return result;
	}
	
	private String createJwt(JwtClaims claims) {
		
		return "";
	}
}
