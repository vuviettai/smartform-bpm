package com.smartform.storage.mongo.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.smartform.rest.model.Metadata;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@MongoEntity(collection="submissions")
public class Submission extends BaseFormioEntity {
	private ObjectId form;
	private List<ObjectId> roles;
	private Metadata metadata;
	private HashMap<String, Object> data;
	private List<String> externalIds;
	private List<String> access;
	private Map<String, Object> extraParams;
	// entity methods
    public static List<Submission> findByFormId(String formId, Document document) {
    	if (formId == null) return null;
    	Document filter = new Document("form", new ObjectId(formId)); 
    	if (document != null) {
    		filter.putAll(document);
    	}
        return find(filter).list();
    }
    public static List<Submission> findByFormId(ObjectId formId, Document document) {
    	if (formId == null) return null;
    	Document filter = new Document("form", formId); 
    	if (document != null) {
    		filter.putAll(document);
    	}
        return find(filter).list();
    }
    public static boolean delete(String submissionId) {
    	if (submissionId == null) return false;
        return deleteById(new ObjectId(submissionId));
    }
    public static long deleteByIds(List<String> submissionIds) {
    	long counter = 0;
    	if (submissionIds != null) {
    		counter  = delete("_id in ?1", submissionIds);
    	}
        return counter;
    }
}
