package com.smartform.rest.model;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class FormioForm {
	private List<Access> access;
	private List<FormComponent> components;
	private Date created;
	private String display;
	private String machineName;
	private Date modified;
	private String name;
	private String owner;
	private String path;
	private List<Access> submissionAccess;
	private List<String> tags;
	private String title;
	private String form;
	private String _id;
}
