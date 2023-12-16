package com.smartform.customize.vgec;

import java.util.List;

import com.smartform.rest.model.Submission;

import lombok.Data;

@Data
public class BeneficiaryCommission {
	private Submission header;
	private List<Submission> details;
	public BeneficiaryCommission(Submission header, List<Submission> details) {
		super();
		this.header = header;
		this.details = details;
	}
	
}
