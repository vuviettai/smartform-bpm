package com.smartform.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class GenericEntity extends EntityBase {
	private String entityName;
	private Map<String, Object> properties;
	public GenericEntity(String entityName) {
		super();
		this.entityName = entityName;
		this.properties = new HashMap<String, Object>();
	}
	public void addProperty(String property, Object value) {
		properties.put(property, value);
	}
}
