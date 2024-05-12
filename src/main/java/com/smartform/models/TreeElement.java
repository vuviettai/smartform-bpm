package com.smartform.models;

public class TreeElement {
	private String name;
	private String code;
	private String type;
	private String description;
	private String parent;
	
	public TreeElement() {
		super();
	}
	public TreeElement(String name, String code, String type) {
		super();
		this.name = name;
		this.code = code;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
