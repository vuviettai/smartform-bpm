package com.smartform.rest.model;

import java.util.List;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class Access {
	private String type;
	private List<ObjectId> roles;
}
