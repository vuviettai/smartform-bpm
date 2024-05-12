package com.smartform.storage.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MongoAdapter {
	public static final String DATABASE_FORMIO = "formio";
	public static final String COLLECTION_FORMS = "forms";
	public static final String COLLECTION_SUBMISSIONS = "submissions";
	public static final String FIELD_ID = "_id";
	public static final String FIELD_FORM = "form";
	
	@Inject
	MongoClient client;
	
	public Document getFormById(String formId) {
		Document result = null;
		MongoCollection<Document> collection = getCollectionForms();
		Bson filter = createIdFilter(formId);
		result = getFirstElement(collection, filter);
		return result;
	}
	
	public Document getFormByName(String formName) {
		Document result = null;
		MongoCollection<Document> collection = getCollectionForms();
		Bson filter = Filters.eq("name", formName);
		result = getFirstElement(collection, filter);
		return result;
	}
	
	public List<Document> queryFormSubmissions(String formId, Bson filter) {
		if (formId == null) return null;
		
		Bson finalFilter = Filters.eq("form", formId);
		if (filter != null) {
			finalFilter = Filters.and(finalFilter, filter);
		}
		List<Document> result = findSubmissions(filter);
		return result;
		
	}
	public List<Document> queryFormSubmissionsByFormName(String formName, Bson filter) {
		if (formName == null || formName == null) return null;
		Document form = getFormByName(formName);
		if (form == null) return null;
		Bson finalFilter = Filters.eq("form", form.getString(FIELD_ID));
		if (filter != null) {
			finalFilter = Filters.and(finalFilter, filter);
		}
		List<Document> result = findSubmissions(filter);
		return result;
	}
	private List<Document> findSubmissions(Bson filter) {
		if (filter == null) return null;
		MongoCollection<Document> collection = getCollectionSubmissions();
		List<Document> result = new ArrayList<Document>();
		FindIterable<Document> documents = collection.find(filter);
		MongoCursor<Document> cursor = documents.iterator();
		while (cursor.hasNext()) {
			result.add(cursor.next());
		}
		return result;
	}
	private Document getFirstElement(MongoCollection<Document> collection, Bson filter) {
		if (collection == null || filter == null) return null;
		Document result = null;
		FindIterable<Document> documents = collection.find(filter);
		MongoCursor<Document> cursor = documents.iterator();
		while (cursor.hasNext()) {
			if (result == null) {
				result = cursor.next();
				break;
			}
		}
		return result;
	}
	private Bson createIdFilter(String id) {
		return Filters.eq("_id", new ObjectId(id));
	}
	public MongoCollection<Document> getCollectionForms() {
		MongoCollection<Document> collection = getCollection(DATABASE_FORMIO, COLLECTION_FORMS);
		return collection;
	} 
	
	public MongoCollection<Document> getCollectionSubmissions() {
		MongoCollection<Document> collection = getCollection(DATABASE_FORMIO, COLLECTION_SUBMISSIONS);
		return collection;
	} 
	private MongoCollection<Document> getCollection(String database, String collection) {
		return client.getDatabase(database).getCollection(collection);
	}
}
