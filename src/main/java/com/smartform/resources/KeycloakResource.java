package com.smartform.resources;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;

import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/admin/keycloak")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
@Authenticated
public class KeycloakResource {
    // @Inject
    // Keycloak keycloak;

    @GET
    @Path("/{realm}/roles")
    public List<RoleRepresentation> getRoles(@RestPath String realm) {
        // return keycloak.realm(realm).roles().list();
        return null;
    }
}
