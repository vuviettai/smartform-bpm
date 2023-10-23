package com.smartform.mapping;

import java.util.function.Function;

import com.smartform.domain.EntityBase;

import io.vertx.mutiny.sqlclient.Row;

public class EntityRowTransform implements Function<Row, EntityBase>{

	private final String entityName;
	public EntityRowTransform(String entityName) {
		this.entityName = entityName;
	}
	@Override
	public EntityBase apply(Row row) {
		// TODO Auto-generated method stub
		return null;
	}
	public String getEntityName() {
		return entityName;
	}

}
