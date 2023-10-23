package com.smartform.models;

import java.util.HashMap;
import java.util.Map;

public class ResourceData extends HashMap<String, Object>{

	private static final long serialVersionUID = 480616873376636187L;
	private Map<String, Object> lowerCaseKeyData = null;
	public Object getCaseInsensitiveValue(String key) {
		if (lowerCaseKeyData == null) {
			lowerCaseKeyData = new HashMap<String, Object>();
			for(Map.Entry<String, Object> entry : this.entrySet()) {
				lowerCaseKeyData.put(entry.getKey().toLowerCase(), entry.getValue());
			}
		}
		return key != null ? lowerCaseKeyData.get(key.toLowerCase()) : null;
	}
}
