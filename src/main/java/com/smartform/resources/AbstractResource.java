package com.smartform.resources;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;
import org.jboss.resteasy.reactive.RestResponse.Status;
import org.keycloak.admin.client.Keycloak;

import com.smartform.rest.model.FormComponent;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;
import com.smartform.services.FormioService;

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
	public static final String FORMIO_COLUMNS = "columns";
	public static final String FORMIO_COMPONENT_KEY = "key";
	public static final String FORMIO_COMPONENT_PARTNER = "partner";
	
	private static final Logger LOG = Logger.getLogger(AbstractResource.class);
	
	@Inject
    SecurityIdentity identity;
	
	@Inject
    Keycloak keycloak;
	
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

	
	protected void injectQueryParams(String formId, MultivaluedMap<String, String> queryParams) {
		Optional<String> groupName = getPartnerGroupName(identity);
		if(groupName.isPresent() && hasPartnerField(formId)) {
			LOG.debugf("Inject group name %s", groupName.get());
			queryParams.putSingle("data.partner", groupName.get());
		}
	}
	protected boolean hasPartnerField(String formId) {
		try {
			FormioForm formioForm = formioService.getFormModelById(formId);
			Optional<Object> fieldPartnerGroup = getField(formioForm.getComponents(), FORMIO_COMPONENT_PARTNER);
			return fieldPartnerGroup.isPresent();
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		return false;
	}
	protected void injectPartnerGroup(Submission submission) {
		Optional<String> groupName = getPartnerGroupName(identity);
		if(groupName.isPresent() && submission != null) {
			if (submission.getData() == null) {
				submission.setData(new HashMap<String, Object>());
			}
			submission.getData().put(FORMIO_COMPONENT_PARTNER, groupName.get());
		}
	}
	protected void injectPartnerGroup(List<Submission> submissions) {
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
	protected Optional<Object> getField(List<FormComponent> components, String fieldName) {
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
					} else if ((children = map.get(FORMIO_COLUMNS)) instanceof List) {
						queue.addAll((List<Object>)children);
					}
				}
				component = queue.pollFirst(); 
			}
		}
		return Optional.empty();
	}
}
