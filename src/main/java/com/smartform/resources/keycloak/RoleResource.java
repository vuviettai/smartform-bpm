package com.smartform.resources.keycloak;

import java.util.List;

import org.jboss.resteasy.reactive.RestPath;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import io.quarkus.security.Authenticated;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

/*
 * Setup service account client with client Roles
 * Realm-Management:
 * 		manage-clients
 * 		manage-users
 * 		query-groups
 * 		query-users
 * 		view-users
 */

@Path("/admin/realms/{realm}/roles")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
@Authenticated
public class RoleResource {
    @Inject
    Keycloak keycloak;

    // @PostConstruct
    // public void initKeycloak() {
    // keycloak = KeycloakBuilder.builder()
    // .serverUrl("https://idm.smartform.click/auth")
    // .realm(realm)
    // .clientId(clientId)
    // .clientSecret(clientSecret)
    // .grantType("client_credentials")
    //// .username(userName)
    //// .password(password)
    // .build();
    // }

    // @PreDestroy
    // public void closeKeycloak() {
    // keycloak.close();
    // }

    @GET
    @Path("")
    public List<RoleRepresentation> getRoles(@RestPath String realm) {
        List<RoleRepresentation> response = null;
        try {
            response = keycloak.realm(realm).roles().list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @POST
    @Path("")
    public Response createRole(@RestPath String realm, RoleRepresentation role) {
        Response response = null;
        try {
            keycloak.realm(realm).roles().create(role);
            response = Response.ok(role).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @GET
    @Path("/{roleName}")
    public RoleRepresentation getGroupById(@RestPath String realm, String roleName) {
        RoleRepresentation response = null;
        try {
            if (realm != null && roleName != null) {
                response = keycloak.realm(realm).roles().get(roleName).toRepresentation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @PUT
    @Path("/{roleName}")
    public Response updateRoleByName(@RestPath String realm, String roleName, RoleRepresentation role) {
        Response response = null;
        try {
            if (realm != null && roleName != null && role != null) {
                keycloak.realm(realm).roles().get(roleName).update(role);
                response = Response.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.serverError().build();
        }
        return response;
    }

    @DELETE
    @Path("/{roleName}")
    public Response deleteRole(@RestPath String realm, String roleName) {
        Response response = null;
        try {
            if (realm != null && roleName != null) {
                keycloak.realm(realm).roles().deleteRole(roleName);
                response = Response.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.serverError().build();
        }
        return response;
    }
}
