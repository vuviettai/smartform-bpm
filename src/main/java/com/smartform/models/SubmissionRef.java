package com.smartform.models;

import lombok.Data;

@Data
public class SubmissionRef {
	private String formId;
	private String submissionId;
	public SubmissionRef() { 
		super();
	}
	public SubmissionRef(String formId, String submissionId) {
		super();
		this.formId = formId;
		this.submissionId = submissionId;
	}
	
}
