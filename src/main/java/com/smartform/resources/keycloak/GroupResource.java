package com.smartform.resources.keycloak;

import java.util.List;

import org.jboss.resteasy.reactive.RestPath;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
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

@Path("/admin/realms/{realm}/groups")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
@Authenticated
public class GroupResource {
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
    public List<GroupRepresentation> getGroups(@RestPath String realm) {
        List<GroupRepresentation> response = null;
        try {
            response = keycloak.realm(realm).groups().groups();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @POST
    @Path("")
    public Response createGroup(@RestPath String realm, GroupRepresentation group) {
        Response response = null;
        try {
            response = keycloak.realm(realm).groups().add(group);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @GET
    @Path("/{groupId}")
    public GroupRepresentation getGroupById(@RestPath String realm, String groupId) {
        GroupRepresentation response = null;
        try {
            if (realm != null && groupId != null) {
                response = keycloak.realm(realm).groups().group(groupId).toRepresentation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @GET
    @Path("/{groupId}/members")
    public List<UserRepresentation> getGroupMembers(@RestPath String realm, String groupId) {
        List<UserRepresentation> response = null;
        try {
            if (realm != null && groupId != null) {
                response = keycloak.realm(realm).groups().group(groupId).members();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @PUT
    @Path("/{groupId}")
    public Response updateGroupById(@RestPath String realm, String groupId, GroupRepresentation group) {
        Response response = null;
        try {
            if (realm != null && groupId != null && group != null) {
                keycloak.realm(realm).groups().group(groupId).update(group);
                response = Response.ok().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.serverError().build();
        }
        return response;
    }

    @POST
    @Path("/{groupId}/children")
    public Response createSubGroup(@RestPath String realm, String groupId,
            GroupRepresentation subGroup) {
        Response response = null;
        try {
            if (realm != null && groupId != null && subGroup != null) {
                response = keycloak.realm(realm).groups().group(groupId).subGroup(subGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = Response.serverError().build();
        }
        return response;
    }

    @POST
    @Path("/{groupId}/subgroups")
    public List<GroupRepresentation> getSubGroups(@RestPath String realm, String groupId) {
        List<GroupRepresentation> response = null;
        try {
            if (realm != null && groupId != null) {
                response = keycloak.realm(realm).groups().group(groupId).getSubGroups(0, 10, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
