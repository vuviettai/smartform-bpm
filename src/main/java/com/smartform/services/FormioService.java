package com.smartform.services;

import java.util.List;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.types.ObjectId;

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
	@Inject
	MongoAdapter mongoAdapter;
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
	
	public Stream<Submission> getSubmissionAsStream(String formId,
			MultivaluedMap<String, String> queryParams) {
		List<Submission> formExtensions = getFormExtensions(formId);
		Stream<Submission> response = null;
		if (formExtensions != null && formExtensions.size() > 0) {
			Submission extension = formExtensions.get(0);
//			SubmissionUtil.getFieldValue(extension, formId);
//			response = formioClient.getSubmissions(formId, queryParams);
		} else {
			Document filter = new Document("form", new ObjectId(formId));
//			if (queryParams != null) {
//				filter.putAll(queryParams);
//			}
			response = Submission.stream(filter);
		}
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
			result = Submission.findByFormId(formExtension.get_id(), new Document("sourceForm.formId", formId));
		}
		return result;
	}
}
