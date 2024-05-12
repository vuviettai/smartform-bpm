package com.smartform.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

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
	private Map<String, Object> extraParams;
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
	public Submission(Document document) {
		super();
		this._id = document.getObjectId("_id").toString();
		ObjectId ownerId = document.getObjectId("owner");
		this.owner = ownerId != null ? ownerId.toString() : "";
		this.access = document.getList("access", String.class);
		this.created = document.getDate("created");
		this.created = document.getDate("modified");
		this.externalIds = new ArrayList<String>();
		Map<String, Object> metadata = (Map<String, Object>)document.get("metadata");
		this.metadata = new Metadata();
		this.metadata.setHeaders((Map<String, String>) metadata.get("header"));
		List<Object> roles = document.getList("roles", Object.class);
		if (roles != null) {
			this.roles = new String[roles.size()];
			for (int i = 0; i < roles.size(); i ++) {
				this.roles[i] = roles.get(i).toString();
			}
		}
		//this.roles = (String[]).toArray();
		ObjectId form = document.getObjectId("form");
		if (form != null) {
			this.form = form.toString();
		}
		this.data = (Map<String, Object>)document.get("data");
	}
	public void setField(String fieldName, Object value) {
		if (data == null) {
			this.data = new HashMap<String, Object>();
		}
		if (fieldName != null) {
			this.data.put(fieldName, value);
		}
	}
	public Object getExtraValue(String field) {
		return extraParams != null ? extraParams.get(field) : null;
	}
}
