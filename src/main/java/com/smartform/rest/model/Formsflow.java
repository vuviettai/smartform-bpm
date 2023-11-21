package com.smartform.rest.model;

import java.util.Date;

import lombok.Data;

@Data
public class Formsflow {
	private String formId;
	private String formName;
	private String formType;
	private Integer id;
	private String modified;
	private String processKey;
}
