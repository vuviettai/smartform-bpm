package com.smartform.resources;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.stream.Collectors;

import com.smartform.domain.Service;
import com.smartform.domain.User;
import com.smartform.domain.dto.ServiceDto;
import com.smartform.domain.dto.UserDto;
import com.smartform.mapping.UserMapper;
import com.smartform.repository.UserRepository;

import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

/**
 * Endpoint for {@link Service}-related functionality
 *
 * @author serhiy
 */
@Path("/users")
@Produces("application/json")
@Consumes("application/json")
@ApplicationScoped
@Authenticated
public class UserResource {
    @Inject
    UserRepository userRepository;
    @Inject
    UserMapper userMapper;

    @GET
    @Path("/{code}")
    public Response find(@PathParam("code") String code) {
        return userRepository.findByCode(code)
                .map(b -> Response.ok(userMapper.toResource(b)))
                .orElseGet(() -> Response.status(NOT_FOUND))
                .build();
    }

    @GET
    public Response findAll() {
        return Response
                .ok(userRepository.findAll().stream().map(b -> userMapper.toResource(b)).collect(Collectors.toList()))
                .build();
    }

    @POST
    public Response create(UserDto userDto) {
        User user = userMapper.fromResource(userDto);
        userRepository.persist(user);
        return Response.ok(user).build();
    }

    @DELETE
    @Path("/{code}")
    public Response delete(@PathParam("code") String code) {
        return userRepository.findByCode(code)
                .map(b -> {
                    userRepository.delete(b);
                    return Response.ok();
                })
                .orElseGet(() -> Response.status(NOT_FOUND))
                .build();
    }
}