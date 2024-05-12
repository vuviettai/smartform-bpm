package com.smartform.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

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
	
	private ObjectId form;
	private Metadata metadata;
	private Date modified;
	private ObjectId owner;
	private List<ObjectId> roles;
	private ObjectId _id;
	private Map<String, Object> extraParams;
	public Submission() {
		super();
		this.access = new ArrayList<String>();
		this.created = new Date();
		this.externalIds = new ArrayList<String>();
		this.metadata = new Metadata();
		this.roles = new ArrayList<ObjectId>();
	}
	public Submission(String form) {
		super();
		this.access = new ArrayList<String>();
		this.created = new Date();
		this.externalIds = new ArrayList<String>();
		this.metadata = new Metadata();
		this.roles = new ArrayList<ObjectId>();
		this.form = new ObjectId(form);
		this.data = new HashMap<String, Object>();
	}
	public Submission(String form, Map<String, Object> data) {
		super();
		this.form = new ObjectId(form);
		this.data = data;
		this.access = new ArrayList<String>();
		this.created = new Date();
		this.externalIds = new ArrayList<String>();
		this.metadata = new Metadata();
		this.roles =  new ArrayList<ObjectId>();
	}
	public Submission(Document document) {
		super();
		this._id = document.getObjectId("_id");
		ObjectId ownerId = document.getObjectId("owner");
		this.owner = ownerId;
		this.access = document.getList("access", String.class);
		this.created = document.getDate("created");
		this.created = document.getDate("modified");
		this.externalIds = new ArrayList<String>();
		Map<String, Object> metadata = (Map<String, Object>)document.get("metadata");
		this.metadata = new Metadata();
		this.metadata.setHeaders((Map<String, String>) metadata.get("header"));
		this.roles = document.getList("roles", ObjectId.class);
		//this.roles = (String[]).toArray();
		this.form = document.getObjectId("form");
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
	public String getId() {
		return _id.toString();
	}
	public String getFormId() {
		return form.toString();
	}
}
