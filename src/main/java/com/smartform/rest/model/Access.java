package com.smartform.rest.model;

import java.util.List;

import lombok.Data;

@Data
public class Access {
	private String type;
	private List<String> roles;
}
