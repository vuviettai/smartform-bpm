package com.smartform.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.keycloak.jose.jwk.JWKParser;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

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

	public static String createJwt(JwtClaims bearerClaims, String jwtSecret) {
		JwtClaimsBuilder builder = Jwt.claims();
		Map<String, Object> user = new HashMap<String, Object>();
		List<String> roles = new ArrayList<String>();
		Map<String, Object> form = new HashMap<String, Object>();
		user.put("_id", "");
		user.put("roles", roles);
		builder.claim("external", true)
				.claim("user", user)
				.claim("form", form);
		// JwtClaims jwtClaims = new JwtClaims();
		// jwtClaims.setClaim("external", true);
		// jwtClaims.setClaim("form", true);
		//
		return builder.signWithSecret(jwtSecret);
	}
}
