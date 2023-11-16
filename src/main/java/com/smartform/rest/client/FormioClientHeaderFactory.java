package com.smartform.rest.client;

import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
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
	@ConfigProperty(name = "formio.jwt-secret")
    String jwtSecret;
	@Override
	public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
			MultivaluedMap<String, String> clientOutgoingHeaders) {
		// TODO Auto-generated method stub
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        List<String> jwtToken = incomingHeaders.get(HEADER_FORMIO_JWT.toLowerCase());
        if (jwtToken == null) {
	        List<String> bearerTokens = incomingHeaders.get(HEADER_AUTHORIZATION);
	        if (bearerTokens != null && bearerTokens.size() > 0) {
	        	String token = bearerTokens.get(0).substring(TOKEN_BEARER.length() + 1);
	        	try {
					JwtClaims claims = JwkUtil.parseJWT(token);
					String formioJwt = JwkUtil.createJwt(claims, jwtSecret);
					System.out.println("Formio Jwt");
					System.out.println(formioJwt);
					//result.add(HEADER_FORMIO_JWT, formioJwt);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
        } else {
        	result.addAll(HEADER_FORMIO_JWT, jwtToken);
        }
        return result;
	}
}


//public String generateToken(CustomUserDetails userDetails) {
//    // Lấy thông tin user
//    Date now = new Date();
//    Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
//    // Tạo chuỗi json web token từ username.
//    return Jwts.builder()
//            .setSubject(userDetails.getUsername())
//            .claim(ORG_TYPE, userDetails.getOrgType())
//            .claim(ORGANIZATION, userDetails.getOrganization())
//            .claim(ORG_ADDR, userDetails.getPartner().getAddress())
//            .claim(ORG_PHONE, userDetails.getPartner().getContactPhone())
//            .claim(ORG_EMAIL, userDetails.getPartner().getContactEmail())
//            .setIssuedAt(now)
//            .setExpiration(expiryDate)
//            .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
//            .compact();
//}
//
//public String getUserNameFromJWT(String token) {
//    Claims claims = Jwts.parser()
//            .setSigningKey(JWT_SECRET)
//            .parseClaimsJws(token)
//            .getBody();
//
//    return claims.getSubject();
//}


