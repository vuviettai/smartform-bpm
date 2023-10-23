package com.smartform.mapping;

import java.util.function.Function;

import org.apache.commons.text.CaseUtils;

import com.smartform.domain.GenericEntity;

import io.vertx.mutiny.sqlclient.Row;

public class GenericEntityTransform implements Function<Row, GenericEntity>{
	private final String entityName;
	public GenericEntityTransform(String entityName) {
		this.entityName = entityName;
	}
	@Override
	public GenericEntity apply(Row row) {
		// TODO Auto-generated method stub
		GenericEntity entity = new GenericEntity(entityName);
		int index = 0;
		while(true) {
			String columnName = row.getColumnName(index);
			if (columnName == null) break;
			String propertyName = CaseUtils.toCamelCase(columnName, false, new char[]{'_'}); // returns "camelCase"
			Object value = row.getValue(index++);
			if (value != null) {
				entity.addProperty(propertyName, value);
			}
			
		}
		
		return entity;
	}
	public String getEntityName() {
		return entityName;
	}

}
