package com.smartform.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * Data model for formio client
 * Using for upload multiple submission
 * @author vuviettai
 */

@Data
public class Submissions {
	private List<String> access;
	private Date created;
	private List<Map<String, Object>> data;
	private List<String> externalIds;
	
	private String form;
	private Metadata metadata;
	private Date modified;
	private String owner;
	private String[] roles;
	
	public Submission createSubmission(Map<String, Object> data) {
		Submission submission = new Submission();
		submission.setData(data);
		submission.setAccess(access);
		submission.setCreated(created);
		submission.setExternalIds(externalIds);
		submission.setForm(form);
		submission.setMetadata(metadata);
		submission.setModified(modified);
		submission.setOwner(owner);
		submission.setRoles(roles);
		if (data.containsKey(Submission._ID)) {
			submission.set_id(String.valueOf(data.get(Submission._ID)));
		}
		return submission;
	}
	public List<Submission> toSubmissionList() {
		List<Submission> list = new ArrayList<Submission>();
		if (data != null) {
			for (Map<String, Object> item : data) {
				Submission submission = createSubmission(item);
				list.add(submission);
			}
		}
		return list;
	}
	
}
