package com.smartform.resources;


import java.util.List;
import java.util.Map;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;
import org.jboss.resteasy.reactive.RestResponse.Status;

import io.vertx.core.http.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

public abstract class AbstractResource {
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
}
