package com.smartform.resources.keycloak;

import java.util.List;
import java.util.Map;

import org.jboss.resteasy.reactive.RestPath;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import io.quarkus.security.Authenticated;
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

@Path("/admin/realms/{realm}/users")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
@Authenticated
public class UserResource {
	
	@Inject
	Keycloak keycloak;

    @GET
    @Path("")
    public List<UserRepresentation> getUsers(@RestPath String realm) {
        List<UserRepresentation> list = null;
        try {
        	list = keycloak.realm(realm).users().list();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return list;
    }
    @POST
    @Path("")
    public Response createUser(@RestPath String realm, UserRepresentation user) {
    	Response result = null;
        try {
        	result = keycloak.realm(realm).users().create(user);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return result;
    }
    @GET
    @Path("/{userId}")
    public UserRepresentation getUserById(@RestPath String realm, String userId) {
    	UserRepresentation result = null;
        try {
        	result = keycloak.realm(realm).users().get(userId).toRepresentation();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return result;
    }
    @GET
    @Path("/{userId}/roles")
    public RoleMappingResource getUserRole(@RestPath String realm, String userId) {
    	RoleMappingResource result = null;
        try {
        	result = keycloak.realm(realm).users().get(userId).roles();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return result;
    }
    @PUT
    @Path("/{userId}")
    public Response updateUserById(@RestPath String realm, UserRepresentation user) {
    	Response response = null;
        try {
        	if (user.getId() != null) {
        		keycloak.realm(realm).users().get(user.getId()).update(user);
        		response = Response.ok("Susscessfully").build();
        	} else {
        		response = Response.notModified().build();
        	}
        	
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return response;
    }
    
    @PUT
    @Path("/{userId}/reset-password")
    public Response resetUserPassword(@RestPath String realm, String userId, CredentialRepresentation credential) {
    	Response response = null;
        try {
        	if (userId != null && credential != null && credential.getType() != null && credential.getValue() != null) {
        		keycloak.realm(realm).users().get(userId).resetPassword(credential);
        	} else {
        		response = Response.notModified().build();
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return response;
    }
    
    @GET
    @Path("/{userId}/groups")
    public List<GroupRepresentation> getUserGroups(@RestPath String realm, String userId) {
    	List<GroupRepresentation> response = null;
        try {
        	if (userId != null) {
        		response = keycloak.realm(realm).users().get(userId).groups();
        	} 
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return response;
    }
    @GET
    @Path("/{userId}/groups/count")
    public Map<String, Long> getUserGroupCount(@RestPath String realm, String userId) {
    	Map<String, Long> response = null;
        try {
        	if (userId != null) {
        		response = keycloak.realm(realm).users().get(userId).groupsCount("");
        	} 
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return response;
    }
    @PUT
    @Path("/{userId}/groups/{groupId}")
    public Response joinGroup(@RestPath String realm, String userId, String groupId) {
    	Response response = null;
        try {
        	if (userId != null && groupId != null) {
        		keycloak.realm(realm).users().get(userId).joinGroup(groupId);
        		response = Response.ok().build();
        	} 
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return response;
    }
    @DELETE
    @Path("/{userId}/groups/{groupId}")
    public Response leaveGroup(@RestPath String realm, String userId, String groupId) {
    	Response response = null;
        try {
        	if (userId != null && groupId != null) {
        		keycloak.realm(realm).users().get(userId).leaveGroup(groupId);
        		response = Response.ok().build();
        	} 
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return response;
    }
    @DELETE
    @Path("/{userId}")
    public Response deleteUserById(@RestPath String realm, String userId) {
    	Response response = null;
        try {
        	if (userId != null) {
        		response = keycloak.realm(realm).users().delete(userId);
        	} else {
        		response = Response.notModified().build();
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return response;
    }
}
