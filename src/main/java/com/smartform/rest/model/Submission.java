package com.smartform.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

/**
 * Data model for formio client
 *
 * @author vuviettai
 */

@Data
public class Submission {
	public static final String SUBMISSION_ID 	= "submissionId";
	public static final String FORM				= "form";
	public static final String FORM_ID 			= "formId";
	public static final String _ID 				= "_id";
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
	public Submission() {
		super();
		this.access = new ArrayList<String>();
		this.created = new Date();
		this.externalIds = new ArrayList<String>();
		this.metadata = new Metadata();
		this.roles = new String[] {};
	}
	public Submission(String form) {
		super();
		this.access = new ArrayList<String>();
		this.created = new Date();
		this.externalIds = new ArrayList<String>();
		this.metadata = new Metadata();
		this.roles = new String[] {};
		this.form = form;
		this.data = new HashMap<String, Object>();
	}
	public Submission(String form, Map<String, Object> data) {
		super();
		this.form = form;
		this.data = data;
		this.access = new ArrayList<String>();
		this.created = new Date();
		this.externalIds = new ArrayList<String>();
		this.metadata = new Metadata();
		this.roles = new String[] {};
	}
	public void setField(String fieldName, Object value) {
		if (data == null) {
			this.data = new HashMap<String, Object>();
		}
		if (fieldName != null) {
			this.data.put(fieldName, value);
		}
	}
}
