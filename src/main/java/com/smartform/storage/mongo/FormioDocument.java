package com.smartform.storage.mongo;

import org.bson.Document;

import lombok.Data;

@Data
public class FormioDocument {
	private Document document;
	public FormioDocument(Document document) {
		this.document = document;
	}
}
