package com.smartform.resources;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import com.appbike.jdbc.pg.PostgresQuery;
import com.mongodb.client.MongoClient;
import com.smartform.rest.client.FormioService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/metadata")
public class MetadataResource extends AbstractResource {

	@RestClient
	@Inject
	FormioService formioService;

	@Inject
	MongoClient client;

	@Inject
	PostgresQuery pgQuery;
	
	@Path("/tables")
	@GET
	public RestResponse<List<String>> getAllTables() {
		List<String> tables = pgQuery.getAllTables();
		return ResponseBuilder.ok(tables).build();
	}

}
