package com.smartform.utils;

import java.util.Date;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.keycloak.jose.jwk.JWKParser;

public class JwkUtil {
	public void parseJwt(String token) {
		JWKParser parser = JWKParser.create().parse(token);

	}
	public static JwtClaims parseJWT(String token) throws Exception {              
       JwtConsumer consumer = new JwtConsumerBuilder()
               .setSkipAllValidators()
               .setDisableRequireSignature()
               .setSkipSignatureVerification()
               .build();
       JwtClaims claims = consumer.processToClaims(token);   
       return claims;
	}
}
