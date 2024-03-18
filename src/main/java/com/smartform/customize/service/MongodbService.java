package com.smartform.customize.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.InsertManyResult;
import com.smartform.models.Tree;
import com.smartform.rest.model.Submission;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class MongodbService {
	@Inject
	MongoClient client;
	public List<Submission> getSubmissions(String formId, MultivaluedMap<String, String> queryParams) {
		List<Submission> results = null;
		MongoCollection<Document> collection = getCollection("formio", "submissions");
		String smartFilter = queryParams.getFirst("smartFilter");
		if (smartFilter != null) {
			smartFilter = new String(Base64.getDecoder().decode(smartFilter));
		}
		try {
			 Document filter = null;
			 if (smartFilter != null) {
				 filter = Document.parse(smartFilter);
				 Object andGroup = filter.get("$and");
				 if (andGroup instanceof List) {
					 ((List<Document>)andGroup).add(new Document("form", new ObjectId(formId)));
				 }
			 } else {	
				 filter = new Document("form", new ObjectId(formId));
			 }
			 FindIterable<Document> docCusor = collection.find(filter);
			 Object limit = queryParams.getFirst("limit");
			 if (limit instanceof Integer) {
				 docCusor = docCusor.limit((Integer)limit);
			 } else if (limit instanceof String) {
				 try {
					 limit = Integer.parseInt((String)limit);
					 docCusor = docCusor.limit((Integer)limit);
				 } catch (Exception e) {}
			 }
			 try (final MongoCursor<Document> cursorIterator = docCusor.cursor()) {
				 results = new ArrayList<Submission>();
			    while (cursorIterator.hasNext()) {
			    	results.add(new Submission(cursorIterator.next()));
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
    public void storeGeoInfo(List<Tree> listTrees) {
        MongoCollection<Document> collection = getCollection("formio", "submissions");
        List<Document> listProvinces = new ArrayList<Document>();
        for(Tree provinceElm : listTrees) {
        	Document document = new Document();
//        	document.append("name", provinceElm.getNode().getName())
//        			.append("code", provinceElm.getNode().getCode())
//        			.append("description", provinceElm.getNode().getDescription())
//        			.append("type", "province");
        	document.append("data", provinceElm);
        	listProvinces.add(document);
        	
        }
        InsertManyResult result = collection.insertMany(listProvinces);
        Map<Integer, BsonValue> insertedIds = result.getInsertedIds();
        
    }
	public MongoCollection<Document> getCollection(String database, String collection) {
		return client.getDatabase(database).getCollection(collection);
	}
	public Document createSubmissionDocument(String formId, String ownerId) {	
		Document document = new Document();
		document.append("form", formId)
			.append("owner", ownerId)
			.append("roles", new ArrayList<String>());
			
		return document;
	}
}


