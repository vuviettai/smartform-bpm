package com.smartform.resources;

import java.util.List;
import java.util.Map;

import com.smartform.domain.EntityBase;
import com.smartform.domain.dto.ResourceDto;
import com.smartform.mapping.ResourceMapper;
import com.smartform.models.RequestParams;
import com.smartform.repository.ResourceRepository;
import com.smartform.services.ResourceService;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/resources")
@Produces("application/json")
@Consumes("application/json")
@Authenticated
public class GenericResource extends AbstractResource {
	@Inject
    ResourceRepository resourceRespository;
    @Inject
    ResourceMapper resourceMapper;
    @Inject
    ResourceService resourceService;
    @GET
    @Path("/{entityName}")
    public Response findEntities(@PathParam("entityName") String entity, @BeanParam RequestParams params) {
    	List<Map<String,Object>>listEntities = resourceService.find(entity, params);
        return Response.ok(listEntities).build();
//                .map(b -> Response.ok(serviceMapper.toResource(b)))
//                .orElseGet(() -> Response.status(NOT_FOUND))
//                .build();
    }

    @POST
    public Response create(ResourceDto resourceDto) {
    	EntityBase entity = resourceMapper.fromResource(resourceDto);
    	if (entity != null) {
    		entity = resourceService.persist(entity);
    	} else {
    		entity = resourceService.persist(resourceDto);
    	}
        return Response.ok(entity).build();
    }

}
