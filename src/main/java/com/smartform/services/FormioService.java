package com.smartform.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.appbike.jdbc.pg.PostgresQuery;
import com.smartform.storage.mongo.MongoAdapter;
import com.smartform.storage.mongo.entity.EntityMapper;
import com.smartform.storage.mongo.entity.FormioForm;
import com.smartform.storage.mongo.entity.Submission;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class FormioService {
	
	public static final String FORMIO_EXTENSION = "formExtension";
	public static final String FORMIO_DATA_DATABASE = "database";
	public static final String FORMIO_DATA_COLLECTION = "collection";
	public static final String FORMIO_DATA_DATABASE_POSTGRES = "postgres";
	
	@Inject
	MongoAdapter mongoAdapter;
	@Inject
	PostgresQuery pgQuery;
	@Inject
	EntityMapper entityMapper;
//	@RestClient 
//	@Inject
//    FormioClient formioClient;
	
	@CacheResult(cacheName = "formIdToForm") 
	public FormioForm getFormById(String formId) {
		FormioForm form =  FormioForm.findById(new ObjectId(formId));
//		//FormioForm formioForm = formioClient.getForm(formId);
//		Document formDocument = mongoAdapter.getFormById(formId);
//		FormioDocument formioForm = new FormioDocument(formDocument);
		return form;
    }
	public com.smartform.rest.model.FormioForm getFormModelById(String formId) {
		FormioForm form =  FormioForm.findById(new ObjectId(formId));
		return form != null ? entityMapper.toModel(form) : null;
    }
	
	//@CacheResult(cacheName = "nameToForm") 
	public FormioForm getFormByName(String formName) {
		FormioForm result = null;
		result = FormioForm.findByName(formName);
//		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
//		params.add("name__regex", "/^" + formName + "$/i");
//		Document form = mongoAdapter.getFormByName(formName);
//		if (form != null) {
//			result = new FormioDocument(form);
//		}
		return result;
	}
	
	//@CacheResult(cacheName = "paramsToForms") 
	public List<FormioForm> queryForms(MultivaluedMap<String, String> queryParams) {
		return FormioForm.find(new Document(queryParams)).list();
	}

	public Stream<FormioForm> queryFormAsStream(MultivaluedMap<String, String> queryParams) {
		return FormioForm.stream(new Document(queryParams));
	}
	
	public List<com.smartform.rest.model.Submission> getSubmissions(String formId,
			MultivaluedMap<String, String> queryParams) {
		List<Submission> formExtensions = getFormExtensions(formId);
		List<com.smartform.rest.model.Submission> response = null;
		if (formExtensions != null && formExtensions.size() > 0) {
			Submission extension = formExtensions.get(0);
			Object extendDb = extension.getData().get(FORMIO_DATA_DATABASE);
			Object collection = extension.getData().get(FORMIO_DATA_COLLECTION);
			if (FORMIO_DATA_DATABASE_POSTGRES.equals(extendDb) && collection instanceof String) {
				//Get data from postgres db
				List<Map<String, Object>> listData = pgQuery.queryCollection((String)collection, queryParams);
				response = listData.stream().map(data -> new com.smartform.rest.model.Submission(formId, data)).collect(Collectors.toList());
			}
		} else {
			Document filter = new Document("form", new ObjectId(formId));
//			if (queryParams != null) {
//				filter.putAll(queryParams);
//			}
			Stream<com.smartform.rest.model.Submission> submissionStream = Submission.stream(filter).map(entity -> entityMapper.toModel((Submission) entity));
			response = submissionStream.collect(Collectors.toList());
		}
		return response;
	}
	
	public Stream<Submission> getSubmissionDocuments(String formId,
			MultivaluedMap<String, String> queryParams) {
		List<Submission> formExtensions = getFormExtensions(formId);
		Stream<Submission> response = null;
		Document filter = new Document("form", new ObjectId(formId));
		if (queryParams != null) {
			filter.putAll(queryParams);
		}
		response = Submission.stream(filter);
		return response;
	}
	
	//@CacheInvalidate(cacheName = "formidParamsToSubmissions")
	//@CacheInvalidateAll(cacheName = "paramsToForms")
	public com.smartform.rest.model.Submission createSubmission(String formId, com.smartform.rest.model.Submission submission) {
		com.smartform.rest.model.Submission result = null;
		try {
			Submission entity = entityMapper.fromModel(submission);
			entity.setForm(new ObjectId(formId));
			entity.persist();
			result = entityMapper.toModel(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
//	
//	//@CacheResult(cacheName = "formIdSubmissionIdToSubmission") 
	public Submission getSubmission(String formId, String submissionId) {
		Submission entity = Submission.findById(new ObjectId(submissionId));
		//return formioClient.getSubmission(formId, submissionId);
		return entity;
	}
	
	public com.smartform.rest.model.Submission getSubmissionModel(String formId, String submissionId) {
		Submission entity = Submission.findById(new ObjectId(submissionId));
		return entity != null ? entityMapper.toModel(entity) : null;
	}
	//@CacheInvalidate(cacheName = "formIdSubmissionIdToSubmission")
	public com.smartform.rest.model.Submission putSubmission(@CacheKey String formId, @CacheKey String submissionId, com.smartform.rest.model.Submission submission) {
		com.smartform.rest.model.Submission result = null;
		try {
			Submission entity = entityMapper.fromModel(submission);
			entity.set_id(new ObjectId(submissionId));
			entity.setForm(new ObjectId(formId));
			entity.persistOrUpdate();
			result = entityMapper.toModel(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//@CacheInvalidate(cacheName = "formIdSubmissionIdToSubmission")
	//@CacheInvalidateAll(cacheName = "paramsToForms")
	public boolean deleteSubmission(@CacheKey String submissionId) {
		boolean result = false;
		try {
			result = Submission.delete(submissionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public long deleteSubmissions(List<String> submissionIds) {
		long result = 0;
		try {
			result = Submission.deleteByIds(submissionIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//Get formExtension
	//@CacheResult(cacheName = "formExtension") 
	public List<Submission> getFormExtensions(String formId) {
		List<Submission> result = null;
		FormioForm formExtension = FormioForm.findByName(FORMIO_EXTENSION);
		if (formExtension != null) {
			result = Submission.findByFormId(formExtension.get_id(), new Document("data.sourceForm.formId", formId));
		}
		return result;
	}
}
