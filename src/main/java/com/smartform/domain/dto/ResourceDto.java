package com.smartform.domain.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.smartform.models.Metadata;
import com.smartform.models.ResourceData;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
/**
 * Data transfer object for generic resource
 *
 * @author vuviettai
 */
@Data
@RegisterForReflection
public class ResourceDto {
	private UUID id;
	
    private String name;
    private Metadata metadata;
    private ResourceData data;
	
    public Object getFieldValue(String name) {
    	return data != null ? data.get(name) : null;
    }
    public Object getCaseInsensitiveValue(String name, Class<?> valueTpe) {
    	Object value = data != null ? data.getCaseInsensitiveValue(name) : null;
    	if (value == null) return value;
    	if (valueTpe.isInstance(value)) return value;
    	return null;
    }
    public String getEntityClassName() {
    	if (name == null) return null;
    	return name.replaceAll("[-_]", "");
    }
}
