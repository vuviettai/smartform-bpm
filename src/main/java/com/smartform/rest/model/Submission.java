package com.smartform.rest.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * Data model for formio client
 *
 * @author vuviettai
 */

@Data
public class Submission {
	private List<String> access;
	private Date created;
	private Map<String, Object> data;
	private List<String> externalIds;
	
	private String form;
	private Metadata metadata;
	private Date modified;
	private String owner;
	private String[] roles;
	private String _id;
}
