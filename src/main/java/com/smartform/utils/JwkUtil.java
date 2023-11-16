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
//		JwtClaims jwtClaims = new JwtClaims();
//		jwtClaims.setClaim("external", true);
//		jwtClaims.setClaim("form", true);
//		
		return builder.signWithSecret(jwtSecret);
	}
}



//@after_this_request
//def add_jwt_token_as_header(response):
//    if response.status_code != 200:
//        return response
//    _role_ids = [
//        role["roleId"]
//        for role in list(
//            filter(
//                filter_user_based_role_ids,
//                response.json.get("form"),
//            )
//        )
//    ]
//    _resource_id = next(
//        role["roleId"]
//        for role in response.json.get("form")
//        if role["type"] == FormioRoles.RESOURCE_ID.value
//    )
//
//    unique_user_id = (
//        user.email or f"{user.user_name}@formsflow.ai"
//    )  # Email is not mandatory in keycloak
//    project_id: str = current_app.config.get("FORMIO_PROJECT_URL")
//    payload: Dict[str, any] = {
//        "external": True,
//        "form": {"_id": _resource_id},
//        "user": {"_id": unique_user_id, "roles": _role_ids},
//    }
//    if project_id:
//        payload["project"] = {"_id": project_id}
//    response.headers["x-jwt-token"] = jwt.encode(
//        payload=payload,
//        key=current_app.config.get("FORMIO_JWT_SECRET"),
//        algorithm="HS256",
//    )
//    response.headers["Access-Control-Expose-Headers"] = "x-jwt-token"
//    if DESIGNER_GROUP not in user.roles:
//        response.set_data(json.dumps({"form": []}))
//        return response
//    return response