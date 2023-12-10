package com.smartform.rest.model;

import java.util.List;

import lombok.Data;

@Data
public class FormsflowPage {
	private List<Formsflow> forms;
	private Integer limit;
	private Integer pageNo;
	private Integer totalCount;
	
	public void increaseTotalCount(Integer value) {
		if (totalCount == null) {
			this.totalCount = value;
		} else if (value != null) {
			this.totalCount += value;
		}
	}
}
