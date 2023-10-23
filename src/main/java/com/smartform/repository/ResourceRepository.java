package com.smartform.repository;

import static io.quarkus.hibernate.orm.panache.runtime.JpaOperations.INSTANCE;

import java.util.List;
import java.util.UUID;

import com.smartform.domain.EntityBase;
import com.smartform.domain.ResourceEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ResourceRepository implements PanacheRepositoryBase<ResourceEntity, UUID>{
	// put your custom logic here as instance methods
	
	public List<EntityBase> list(String resourceName) {
		return null;
    }
	public List<EntityBase> list(String resourceName, String query, Object... params) {

		return null;
    }
//	public List<ResourceEntity> filterEntities() {
//        return list("order by date DESC");
//    }
   
}
