package com.smartform.rest.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

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
	
	private ObjectId form;
	private Metadata metadata;
	private Date modified;
	private ObjectId owner;
	private List<ObjectId> roles;
	
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
		Object submissionId = data.get(Submission._ID);
		if (submissionId instanceof ObjectId) {
			submission.set_id((ObjectId)submissionId);
		} else if (submissionId instanceof String) {
			submission.set_id(new ObjectId((String)submissionId));
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
