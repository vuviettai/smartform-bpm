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
	
	public List<Submission> toSubmissionList() {
		List<Submission> list = new ArrayList<Submission>();
		if (data != null) {
			for (Map<String, Object> item : data) {
				Submission submission = new Submission();
				submission.setData(item);
				submission.setAccess(access);
				submission.setCreated(created);
				submission.setExternalIds(externalIds);
				submission.setForm(form);
				submission.setMetadata(metadata);
				submission.setModified(modified);
				submission.setOwner(owner);
				submission.setRoles(roles);
				if (item.containsKey(Submission._ID)) {
					submission.set_id(String.valueOf(item.get(Submission._ID)));
				}
				list.add(submission);
			}
		}
		return list;
	}
	
}
