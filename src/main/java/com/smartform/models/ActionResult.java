package com.smartform.models;

import java.util.Map;

import lombok.Data;

@Data
public class ActionResult {
	private String formId;
	private String submissionId;
	private String name;
	private Map<String, Object> params;
}
