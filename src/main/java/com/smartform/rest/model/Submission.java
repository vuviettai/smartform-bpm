package com.smartform.rest.model;

import java.util.ArrayList;
import java.util.Date;
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
		this.access = new ArrayList<String>();
		this.created = new Date();
		this.externalIds = new ArrayList<String>();
		this.metadata = new Metadata();
		this.roles = new String[] {};
	}
	public Submission(String form, Map<String, Object> data) {
		this.form = form;
		this.data = data;
		this.access = new ArrayList<String>();
		this.created = new Date();
		this.externalIds = new ArrayList<String>();
		this.metadata = new Metadata();
		this.roles = new String[] {};
	}
}
