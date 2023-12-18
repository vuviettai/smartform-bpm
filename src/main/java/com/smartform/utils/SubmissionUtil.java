package com.smartform.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import com.smartform.models.SubmissionRef;
import com.smartform.rest.client.FormioService;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class SubmissionUtil {
	
	@RestClient 
	FormioService formioService;

//	public SubmissionUtil(FormioService formioService) {
//		super();
//		this.formioService = formioService;
//	}
//	
	public FormioForm getFormByName(String name) {
		FormioForm result = null;
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		params.add("name", name);
		List<FormioForm> forms = formioService.queryForms(params);
		if (forms != null && forms.size() > 0) {
			result = forms.get(0);
		}
		return result;
	}
	
	public List<Submission> querySubmissionsByFormId(String formId, MultivaluedMap<String, String> params) {
		List<Submission> result = null;
		if (formId != null) {
			RestResponse<List<Submission>> response = formioService.getSubmissions(formId, params);
			result = response.getEntity();
		}
		return result;
	}
	
	public List<Submission> getSubmissionsByFormName(String formName, MultivaluedMap<String, String> params) {
		List<Submission> result = null;
		FormioForm form = getFormByName(formName);
		if (form != null) {
			RestResponse<List<Submission>> response = formioService.getSubmissions(form.get_id(), params);
			result = response.getEntity();
		}
		return result;
	}
	
	public List<Submission> storeSubmissions(FormioForm form, List<Submission> submissions) {
		List<Submission> storedSubmissions = new ArrayList<Submission>();
		if (form != null) {
			for(Submission submission : submissions) {
				Submission stored = null;
				if (submission.get_id() != null) {
					stored = formioService.putSubmission(form.get_id(), submission.get_id(), submission);
				} else {
					stored = formioService.createSubmission(form.get_id(), submission);
				}
				storedSubmissions.add(stored);
			}
		}
		return storedSubmissions;
	}
	public List<Submission> deleteSubmissions(FormioForm form, List<Submission> submissions) {
		List<Submission> deletedSubmissions = new ArrayList<Submission>();
		if (form != null) {
			for(Submission submission : submissions) {
				Submission deleted = formioService.deleteSubmission(form.get_id(), submission.get_id());
				deletedSubmissions.add(deleted);
			}
		}
		return deletedSubmissions;
	}
	public static Object getFieldValue(Submission submission, String field) {
		Object result = null;
		if (submission != null) {
			if (Submission._ID.equalsIgnoreCase(field)) {
				return submission.get_id();
			}
			else if(submission.getData() != null) {
				return submission.getData().get(field);
			}
		}
		return result;
	}
	public static Map<String, String> createReferenceMap(Submission submission) {
		return Map.of(Submission.FORM, submission.getForm(), Submission._ID, submission.get_id());
	}
	public static SubmissionRef toSubmissionReference(Object value) {
		if (value instanceof Map) {
			Object formId = ((Map<String, Object>) value).get(Submission.FORM);
			Object submissionId = ((Map<String, Object>) value).get(Submission._ID);
			if (formId instanceof String && submissionId instanceof String) {
				return new SubmissionRef((String)formId, (String)submissionId);
			}
		}
		return null;
	}
	public static Map<String, Submission> groupSubmissionsById(List<Submission> submissions) {
		Map<String, Submission> result = new HashMap<String, Submission>();
		if (submissions != null && submissions.size() > 0) {
			for (Submission submission : submissions) {
				result.put(submission.get_id(), submission);
			}
		}
		return result;
	}
	public static Map<Object, Submission> groupSubmissionsByUniqueField(List<Submission> submissions, String fieldName) {
		Map<Object, Submission> result = new HashMap<Object, Submission>();
		if (submissions != null && submissions.size() > 0) {
			for (Submission submission : submissions) {
				Object ref = SubmissionUtil.getFieldValue(submission, fieldName);
				result.put(ref, submission);
			}
		}
		return result;
	}
	public static Map<String, List<Submission>> groupSubmissionsByReference(List<Submission> submissions, String refField) {
		Map<String, List<Submission>> result = new HashMap<String, List<Submission>>();
		if (submissions != null && submissions.size() > 0) {
			for (Submission submission : submissions) {
				String key = null;
				Object ref = SubmissionUtil.getFieldValue(submission, refField);
				if (ref instanceof Map) {
					Map<String, String> mapRef = (Map<String, String>) ref;
					key = mapRef.get(Submission.FORM);
					key += "#" + mapRef.get(Submission._ID);
				} 
				List<Submission> list = result.get(key);
				if (list == null) {
					list = new ArrayList<Submission>();
					result.put(key, list);
				}
				list.add(submission);
			}
		}
		return result;
	}
	public static Map<Object, List<Submission>> groupSubmissionsByField(List<Submission> submissions, String fieldName) {
		Map<Object, List<Submission>> result = new HashMap<Object, List<Submission>>();
		if (submissions != null && submissions.size() > 0) {
			for (Submission submission : submissions) {
				Object groupId = SubmissionUtil.getFieldValue(submission, fieldName);
				if (groupId instanceof Map) {
					groupId = ((Map<String, Object>)groupId).get(Submission._ID);
				} 
				List<Submission> list = result.get(groupId);
				if (list == null) {
					list = new ArrayList<Submission>();
					result.put(groupId, list);
				}
				list.add(submission);
			}
		}
		return result;
	}
	
}
