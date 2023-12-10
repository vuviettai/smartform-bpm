package com.smartform.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

import com.smartform.rest.client.FormioService;
import com.smartform.rest.client.FormsflowService;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Formsflow;
import com.smartform.rest.model.Submission;
import com.smartform.rest.model.Submissions;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

@Path("/form")
public class FormioResource extends AbstractResource {
	public static final String REFERENCE_FORM	=	"form";
	public static final String REFERENCE_ID		=	"_id";
	
	@RestClient 
    FormioService formioService;
	
	@RestClient 
    FormsflowService formsflowService;
	
	@Path("/{formId}")
	@GET
	public FormioForm getForm(@RestPath String formId) {
		FormioForm formioForm = null;
		try {
			formioForm = formioService.getForm(formId);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		return formioForm;
	}
	@Path("/{formId}/submission")
	@GET
	public List<Submission> getSubmissions(@RestPath String formId, @Context UriInfo uriInfo) {
		List<Submission> submissions = null;
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			//Map<String, String> filters = new HashMap<String, >();
			submissions = formioService.getSubmissions(formId, queryParams);
			//Todo: Improve performance
			if (submissions != null) {
				for(Submission submission : submissions) {
					if (submission.getData() != null) {
						loadReferenceSubmissions((Map<String, Object>)submission.getData());
					}
				}
			}
			
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return submissions;
	}
	@Path("/{formId}/submission")
	@POST
	public Submission createSubmission(@RestPath String formId, Submission submission) {
		Submission createdSubmission = null;
		//Formsflow formsflow = null;
		try {
			//formsflow = formsflowService.getById(formId);
			createdSubmission = formioService.createSubmission(formId, submission);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return createdSubmission;
	}
	@Path("/{formId}/submission/upload")
	@POST
	public List<Submission> uploadSubmissions(@RestPath String formId, Submissions submissions) {
		List<Submission> uploadedSubmissions = new ArrayList<Submission>();
		if (submissions != null) {
			List<Submission> payload = submissions.toSubmissionList();
			for(Submission submission : payload) {
				try {
					Submission createdSubmission = formioService.createSubmission(formId, submission);
					uploadedSubmissions.add(createdSubmission);
				} catch (WebApplicationException e) {
					e.printStackTrace();
				}
			}
		}
		return uploadedSubmissions;
	}
	@Path("/{formId}/submission/{submissionId}")
	@GET
	public Submission getSubmission(@RestPath String formId, @RestPath String submissionId) {
		Submission submission = null;
		try {
			submission = formioService.getSubmission(formId, submissionId);
			//Load reference;
			Map<String, Object> data = submission.getData();
			if (data != null) {
				loadReferenceSubmissions(data);
			}
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return submission;
	}
	
	@Path("/{formId}/submission/{submissionId}")
	@PUT
	public Submission putSubmission(@RestPath String formId, @RestPath String submissionId, Submission submission) {
		Submission updated = null;
		try {
			updated = formioService.putSubmission(formId, submissionId, submission);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return updated;
	}
	@Path("/{formId}/submission/{submissionId}")
	@DELETE
	public Submission deleteSubmission(@RestPath String formId, @RestPath String submissionId) {
		Submission deleted = null;
		try {
			deleted = formioService.deleteSubmission(formId, submissionId);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		
		return deleted;
	}
	//Load reference submission fields
	private void loadReferenceSubmissions(Map<String, Object> data) {
		Map<String, Submission> mapRefSubmissions = new HashMap<String, Submission>();
		for(Map.Entry<String, Object> entry : data.entrySet()) {
			if (entry.getValue() instanceof Map) {
				Map<String, String> ref = (Map<String, String>)entry.getValue();
				Submission refSubmission = getReferenceSubmission(ref);
				if (refSubmission != null) {
					String key = ref.get(REFERENCE_FORM) + "#" + ref.get(REFERENCE_ID);
					mapRefSubmissions.put(entry.getKey(), refSubmission);
				}
			} else if (entry.getValue() instanceof List) {
				for(Object element : (List) entry.getValue()) {
					if (element instanceof Map) {
						loadReferenceSubmissions((Map<String, Object>)element);
					}
				}
				
			}
		}
		data.putAll(mapRefSubmissions);
	}
	private Submission getReferenceSubmission(Map<String, String> ref) {
		String formId = ref.get(REFERENCE_FORM);
		String submissionId = ref.get(REFERENCE_ID);
		Submission refSubmission = null;
		if (formId != null && submissionId != null) {
			refSubmission = getSubmission(formId, submissionId);
		}
		return refSubmission;
	}
}
