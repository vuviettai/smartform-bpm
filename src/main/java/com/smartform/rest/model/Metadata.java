package com.smartform.rest.model;

import java.util.Map;

import lombok.Data;

@Data
public class Metadata {
	private String browserName;
	private Map<String, String> headers;
	private Integer offset;
	private Boolean onLine;
	private String origin;
	private String pathName;
	private String referrer;
	private String timezone;
	private String userAgent;
}
