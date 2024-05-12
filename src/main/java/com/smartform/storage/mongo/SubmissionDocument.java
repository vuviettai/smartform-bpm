package com.smartform.storage.mongo;

import org.bson.Document;

import lombok.Data;

@Data
public class SubmissionDocument {
	private Document document;
	public SubmissionDocument(Document document) {
		this.document = document;
	}
}
