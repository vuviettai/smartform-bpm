package com.smartform.customize.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonValue;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.InsertManyResult;
import com.smartform.models.Tree;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongodbService {
	@Inject
	MongoClient client;
	
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


