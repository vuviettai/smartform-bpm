package com.smartform.rest.model;

import java.util.List;

import lombok.Data;

@Data
public class FormsflowPage {
	private List<Formsflow> forms;
	private Integer limit;
	private Integer pageNo;
	private Integer totalCount;
}
