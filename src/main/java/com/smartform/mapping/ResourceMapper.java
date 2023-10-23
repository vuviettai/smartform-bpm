package com.smartform.mapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.smartform.domain.EntityBase;
import com.smartform.domain.GenericEntity;
import com.smartform.domain.Service;
import com.smartform.domain.dto.ResourceDto;
import com.smartform.domain.dto.ServiceDto;
import com.smartform.utils.ClassUtils;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.vertx.mutiny.sqlclient.Row;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@ApplicationScoped
public class ResourceMapper {
	 
	private Map<String, Class<?>> entityClasses = null;
	@Context 
	private SecurityContext securityContext;
    public ResourceDto toResource(PanacheEntity entity) {
    	ResourceDto resourceDto = new ResourceDto();
    	return resourceDto;
    }

    /**
     * Maps {@link ServiceDto} to {@link Service} entity
     *
     * @param ServiceDto to be mapped
     * @return mapped entity
     */
    public EntityBase fromResource(ResourceDto resourceDto) {
    	EntityBase entity = null;
    	Class<?> entityClass = resourceDto.getName() != null ? ClassUtils.getEntityClass(resourceDto.getEntityClassName()) : null;
    	if (entityClass != null) {
    		Constructor[] constructors = entityClass.getDeclaredConstructors();
    		if (constructors.length > 0) {
    			try {
					entity = (EntityBase)constructors[0].newInstance();
					entity.setDefault(securityContext);
					setValues(resourceDto, entityClass, entity);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	} else {
    		GenericEntity genericEntity = new GenericEntity(resourceDto.getName());
    		genericEntity.setDefault(securityContext);
    		genericEntity.setProperties(resourceDto.getData());
    	}
    	return entity;
    }
    private void setValues(ResourceDto resourceDto, Class<?> entityClass, EntityBase entity) {
    	Method[] desMethods = entityClass.getMethods();
		for (Method method : desMethods) {
			if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
				String name = method.getName().substring(3).toLowerCase();
				Object value = resourceDto.getCaseInsensitiveValue(name, method.getParameters()[0].getType());
				if (value != null) {
					try {
						method.invoke(entity, value);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
    }
}
