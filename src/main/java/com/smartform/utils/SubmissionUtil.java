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

	// public SubmissionUtil(FormioService formioService) {
	// super();
	// this.formioService = formioService;
	// }
	//
//	public FormioForm getFormByName(String name) {
//		FormioForm result = null;
//		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
//		params.add("name", name);
//		List<FormioForm> forms = formioService.queryForms(params);
//		if (forms != null && forms.size() > 0) {
//			result = forms.get(0);
//		}
//		return result;
//	}

	public FormioForm getFormById(String formId) {
		FormioForm result = formioService.getForm(formId);
		return result;
	}
	public List<Submission> querySubmissionsByFormId(String formId, MultivaluedMap<String, String> params) {
		List<Submission> result = null;
		if (formId != null) {
			RestResponse<List<Submission>> response = formioService.getSubmissions(formId, params);
			//Load referencces
			result = response.getEntity();
			loadReferenceSubmissions(result);
		}
		return result;
	}
	public Submission getLastSubmission(String formId, List<String> selectFields) {
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		params.putSingle(FormioService.LIMIT, "1");
		params.putSingle(FormioService.SORT, "-created");
		if (selectFields != null) {
			params.putSingle(FormioService.SELECT, String.join(",", selectFields));
		}
		RestResponse<List<Submission>> response = formioService.getSubmissions(formId, params);
		List<Submission> entity = response.getEntity();
		if (entity != null && entity.size() > 0) {
			return entity.get(0);
		}
		return null;
	}
//	public List<Submission> getSubmissionsByFormName(String formName, MultivaluedMap<String, String> params) {
//		List<Submission> result = null;
//		FormioForm form = getFormByName(formName);
//		if (form != null) {
//			RestResponse<List<Submission>> response = formioService.getSubmissions(form.get_id(), params);
//			result = response.getEntity();
//		}
//		return result;
//	}
	public Submission getSubmissionByRef(Map<String, Object> ref) {
		if (ref == null) return null;
		String formId = (String)ref.get(Submission.FORM);
		String submissionId = (String) ref.get(Submission._ID);
		if (formId == null || submissionId == null) return null;
		Submission submission = formioService.getSubmission(formId, submissionId);
		return submission;
	}
	public Submission getSubmissionById(String formId, String submissionId) {
		if (formId == null || submissionId == null) return null;
		Submission submission = formioService.getSubmission(formId, submissionId);
		return submission;
	}
	public List<Submission> getSubmissionByIds(String formId, List<String> submissionIds) {
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		for (String id : submissionIds) {
			params.add("_id__in", id);
		}
		RestResponse<List<Submission>> response = formioService.getSubmissions(formId, params);
		return response.getEntity();
	}
	/*
	 * 2023-12-16 load reference by each field for all submissions.
	 * This approach gives better performance
	 */
	// Load reference submission fields
	public void loadReferenceSubmissions(List<Submission> submissions) {
		// Store all submission ids for each form
		Map<String, List<String>> mapRefSubmissionIds = new HashMap<String, List<String>>();
		// Map fieldName with formId
		Map<String, String> mapFormFields = new HashMap<String, String>();
		for (Submission submission : submissions) {
			if (submission.getData() == null)
				continue;
			for (Map.Entry<String, Object> entry : submission.getData().entrySet()) {
				List<SubmissionRef> refs = SubmissionUtil.toSubmissionReference(entry.getValue());
				if (refs != null && refs.size() > 0) {
					for (SubmissionRef ref : refs) {
						List<String> list = mapRefSubmissionIds.getOrDefault(ref.getFormId(), new ArrayList<String>());
						mapRefSubmissionIds.put(ref.getFormId(), list);
						list.add(ref.getSubmissionId());
						mapFormFields.put(entry.getKey(), ref.getFormId());
					}
				}
			}
		}
		Map<SubmissionRef, Submission> mapReferenceSubmissions = new HashMap<SubmissionRef, Submission>();
		// Todo: Send references to client for better performance
		for (Map.Entry<String, List<String>> entry : mapRefSubmissionIds.entrySet()) {
			MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
			params.put(Submission._ID + "__in", entry.getValue());
			params.putSingle("limit", String.valueOf(entry.getValue().size()));
			//Get submission by each form
			List<Submission> listReferences = querySubmissionsByFormId(entry.getKey(), params);
			for (Submission submission : listReferences) {
				mapReferenceSubmissions.put(new SubmissionRef(entry.getKey(), submission.get_id()), submission);
			}
			
		}
		if (mapReferenceSubmissions.size() > 0) {
			for (Submission submission : submissions) {
				if (submission.getData() == null)
					continue;
				setReferenceSubmission(submission.getData(), mapReferenceSubmissions);
			}
		}
	}
	/*
	 * Set reference submission to the value with pair (form, _id)
	 * useSubmission: true - use full submission for reference value, false - use data field only
	 * 
	 */
	private void setReferenceSubmission(Map<String, Object> data, Map<SubmissionRef, Submission> mapReferenceSubmissions) {
		Map<String, Object> refSubmissions = new HashMap<String, Object>();
		for(Map.Entry<String, Object> entry : data.entrySet()) {
			if (entry.getValue() instanceof List) {
				for(Object itemValue : (List)entry.getValue()) {
					if (itemValue instanceof Map) {
						setReferenceSubmission((Map<String, Object>)itemValue, mapReferenceSubmissions);
					}
				}
			} else if (entry.getValue() instanceof Map) {
				Map<String, Object> valueMap = (Map<String, Object>)entry.getValue();
				String formId = (String)valueMap.get(Submission.FORM);
				String submissionId = (String)valueMap.get(Submission._ID);
				if (formId != null && submissionId != null) {
					Submission refSubmission = mapReferenceSubmissions.get(new SubmissionRef(formId, submissionId));
					if (refSubmission != null) {
						refSubmissions.put(entry.getKey(), refSubmission);
					}
				} else {
					setReferenceSubmission(valueMap, mapReferenceSubmissions);
				}
			}
		}
		if (refSubmissions.size() > 0) {
			data.putAll(refSubmissions);
		}
	}
	public List<Submission> storeSubmissions(FormioForm form, List<Submission> submissions) {
		List<Submission> storedSubmissions = new ArrayList<Submission>();
		if (form != null) {
			for (Submission submission : submissions) {
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
			for (Submission submission : submissions) {
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
			} else if (submission.getData() != null) {
				return submission.getData().get(field);
			}
		}
		return result;
	}
	public static void setDataValue(Submission submission, String field, Object value) {
		if (submission != null) {
			Map<String, Object> data = submission.getData();
			if (data == null) {
				data = new HashMap<String, Object>();
				submission.setData(data);
			}
			data.put(field, value);
		}
	}

	public static Map<String, String> createReferenceMap(Submission submission) {
		return Map.of(Submission.FORM, submission.getForm(), Submission._ID, submission.get_id());
	}

	public static List<SubmissionRef> toSubmissionReference(Object value) {
		List<SubmissionRef> listRefs = new ArrayList<SubmissionRef>();
		if (value instanceof Map) {
			Object formId = ((Map<String, Object>) value).get(Submission.FORM);
			Object submissionId = ((Map<String, Object>) value).get(Submission._ID);
			if (formId instanceof String && submissionId instanceof String) {
				listRefs.add(new SubmissionRef((String) formId, (String) submissionId));
			}
		} else if (value instanceof List) {
			for (Object itemValue : (List)value) {
				if (itemValue instanceof Map) {
					for(Object itemFieldValue : ((Map<String, Object>)itemValue).values()) {
						List<SubmissionRef> itemRefs = toSubmissionReference(itemFieldValue);
						listRefs.addAll(itemRefs);
					}
				}
			}
		}
		return listRefs;
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

	public static Map<Object, Submission> groupSubmissionsByUniqueField(List<Submission> submissions,
			String fieldName) {
		Map<Object, Submission> result = new HashMap<Object, Submission>();
		if (submissions != null && submissions.size() > 0) {
			for (Submission submission : submissions) {
				Object ref = SubmissionUtil.getFieldValue(submission, fieldName);
				result.put(ref, submission);
			}
		}
		return result;
	}

	public static Map<String, List<Submission>> groupSubmissionsByReference(List<Submission> submissions,
			String refField) {
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

	public static Map<Object, List<Submission>> groupSubmissionsByField(List<Submission> submissions,
			String fieldName) {
		Map<Object, List<Submission>> result = new HashMap<Object, List<Submission>>();
		if (submissions != null && submissions.size() > 0) {
			for (Submission submission : submissions) {
				Object groupId = SubmissionUtil.getFieldValue(submission, fieldName);
				if (groupId instanceof Map) {
					groupId = ((Map<String, Object>) groupId).get(Submission._ID);
				} else if (groupId instanceof Submission) {
					groupId = ((Submission) groupId).get_id();
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
