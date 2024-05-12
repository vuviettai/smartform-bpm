package com.smartform.models;

import java.util.Objects;

import lombok.Data;

@Data
public class SubmissionRef {
	private String formId;
	private String submissionId;
	private int hashCode;
	public SubmissionRef() { 
		super();
	}
	public SubmissionRef(String formId, String submissionId) {
		super();
		this.formId = formId;
		this.submissionId = submissionId;
		this.hashCode = Objects.hash(formId, submissionId);
	}
	@Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SubmissionRef that = (SubmissionRef) o;
        return formId != null && formId.equals(that.formId) && submissionId != null && submissionId.equals(that.submissionId);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
