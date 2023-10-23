package com.smartform.mapping;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.CaseUtils;

import io.vertx.mutiny.sqlclient.Row;

public class PropertiesTransform {
	public static Map<String, Object> from(Row row) {
		Map<String, Object> properties = new HashMap<String, Object>();
		int index = 0;
		while(true) {
			String columnName = row.getColumnName(index);
			if (columnName == null) break;
			String propertyName = CaseUtils.toCamelCase(columnName, false, new char[]{'_'}); // returns "camelCase"
			Object value = row.getValue(index++);
			if (value != null) {
				properties.put(propertyName, value);
			}
			
		}
		return properties;
	}
}
