package com.smartform.resources;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;
import org.jboss.resteasy.reactive.RestResponse.Status;

import com.smartform.rest.client.FormioService;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.SecurityIdentity;
import io.vertx.core.http.HttpHeaders;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

public abstract class AbstractResource {
	public static final String KEYCLOAK_CLAIM_GROUPS = "groups";
	public static final String KEYCLOAK_GROUP_PARTNER = "/partner/";
	public static final String FORMIO_COMPONENTS = "components";
	public static final String FORMIO_COMPONENT_KEY = "key";
	public static final String FORMIO_COMPONENT_PARTNER = "partner";
	@RestClient
	@Inject
	protected FormioService formioService;
	
	protected<T> ResponseBuilder<T> createResponseBuilder(RestResponse<T> clientResponse) {
		if (clientResponse == null) {
			return ResponseBuilder.<T>create(Status.INTERNAL_SERVER_ERROR);
		}
		T entity = clientResponse.getEntity();
		if (entity == null) {
			return ResponseBuilder.<T>noContent();
		}
		ResponseBuilder<T> builder = ResponseBuilder.ok(entity, MediaType.APPLICATION_JSON);
		for(Map.Entry<String, List<Object>> entry : clientResponse.getHeaders().entrySet()) {
			if (entry.getValue() == null) continue;
			Object value = entry.getValue().size() == 1 ? entry.getValue().get(0) : entry.getValue();
			if (entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_RANGE.toString())) {
				builder = builder.header(HttpHeaders.CONTENT_RANGE.toString(), value);
			}
		}		
		return builder;
	}
	protected void injectQueryParams(SecurityIdentity identity, String formId, MultivaluedMap<String, String> queryParams) {
		Optional<String> groupName = getPartnerGroupName(identity);
		if(groupName.isPresent()) {
			try {
				FormioForm formioForm = formioService.getForm(formId);
				Optional<Object> fieldPartnerGroup = getField(formioForm.getComponents(), FORMIO_COMPONENT_PARTNER);
				if (fieldPartnerGroup.isPresent()) {
					queryParams.putSingle("data.partner", groupName.get());
				}
			} catch (WebApplicationException e) {
				e.printStackTrace();
			}
			
		}
	}
	protected void injectPartnerGroup(SecurityIdentity identity, Submission submission) {
		Optional<String> groupName = getPartnerGroupName(identity);
		if(groupName.isPresent() && submission != null) {
			if (submission.getData() == null) {
				submission.setData(new HashMap<String, Object>());
			}
			submission.getData().put("data.partner", groupName.get());
		}
	}
	protected void injectPartnerGroup(SecurityIdentity identity, List<Submission> submissions) {
		Optional<String> groupName = getPartnerGroupName(identity);
		if(groupName.isPresent() && submissions != null) {
			for (Submission submission : submissions) {
				if (submission.getData() == null) {
					submission.setData(new HashMap<String, Object>());
				}
				submission.getData().put("data.partner", groupName.get());
			}
		}
	}
	protected Optional<String> getPartnerGroupName(SecurityIdentity identity) {
		OidcJwtCallerPrincipal principal = (OidcJwtCallerPrincipal) identity.getPrincipal();
		Optional<Object> claimGroups = principal.claim(KEYCLOAK_CLAIM_GROUPS);
		if (claimGroups.isPresent() && claimGroups.get() instanceof Set) {
			Set<String> groups = (Set<String>)claimGroups.get();
			for (String groupPath : groups) {
				if (groupPath.startsWith(KEYCLOAK_GROUP_PARTNER)) {
					String groupName = groupPath.substring(KEYCLOAK_GROUP_PARTNER.length());
					return Optional.of(groupName);
				}
			}
		}
		return Optional.empty();
	}
	protected Optional<Object> getField(List<Object> components, String fieldName) {
		if (components != null) {
			LinkedList<Object> queue = new LinkedList<Object>(components);
			Object component = queue.pollFirst(); 
			while (component != null) {
				if (component instanceof Map) {
					Map<String, Object> map = (Map<String,Object>) component;
					if (fieldName.equals(map.get(FORMIO_COMPONENT_KEY))) {
						return Optional.of(component);
					}
					Object children = map.get(FORMIO_COMPONENTS);
					if (children instanceof List) {
						queue.addAll((List<Object>)children);
					}
				}
				component = queue.pollFirst(); 
			}
		}
		return Optional.empty();
	}
}
