package com.smartform.resources.keycloak;

import java.util.List;

import org.jboss.resteasy.reactive.RestPath;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/admin/realms/{realm}/clients")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
@Authenticated
public class ClientResource {
	@Inject
    Keycloak keycloak;
	
	@GET
    @Path("")
    public List<ClientRepresentation> getClients(@RestPath String realm) {
        List<ClientRepresentation> response = null;
        try {
            response = keycloak.realm(realm).clients().findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
	@GET
    @Path("/{clientId}")
    public List<ClientRepresentation> getClientByClientId(@RestPath String realm, @RestPath String clientId) {
        List<ClientRepresentation> response = null;
        try {
            response = keycloak.realm(realm).clients().findByClientId(clientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
	
	@GET
    @Path("/{id}/roles")
    public List<RoleRepresentation> getClientRolesById(@RestPath String realm, @RestPath String id) {
		List<RoleRepresentation> response = null;
        try {
            response = keycloak.realm(realm).clients().get(id).roles().list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
