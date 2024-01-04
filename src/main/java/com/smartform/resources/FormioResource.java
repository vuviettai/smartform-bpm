package com.smartform.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;
import org.jboss.resteasy.reactive.common.util.RestMediaType;

import com.smartform.customize.vgec.CommissionPolicy;
import com.smartform.customize.vgec.CommissionService;
import com.smartform.models.ActionResult;
import com.smartform.rest.client.FormioService;
import com.smartform.rest.client.FormsflowService;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;
import com.smartform.rest.model.Submissions;
import com.smartform.utils.SubmissionUtil;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/form")
public class FormioResource extends AbstractResource {

	public static final String ACTION = "action";
	public static final String OPEN_FORM_ID = "openformid";
	public static final String FORM_USER = "formuser";
	public static final String FORM_GROUP = "formgroup";
	public static final String FORM_ROLE = "formrole";

	@RestClient
	FormioService formioService;

	@RestClient
	FormsflowService formsflowService;

	@Inject
	CommissionService commissionService;

	@Inject
	private SubmissionUtil submissionUtil;

	// private FormioForm formUser;
	// private FormioForm formGroup;
	// private FormioForm formRole;

	// @PostConstruct
	// private void initUsersForms() {
	// MultivaluedMap<String, String> params = new MultivaluedHashMap<String,
	// String>();
	// params.put("name__in", Arrays.asList(FORM_GROUP, FORM_ROLE, FORM_USER));
	// List<FormioForm> response = formioService.queryForms(params);
	// if (response != null && response.size() > 0) {
	// for (FormioForm form : response) {
	// if (FORM_GROUP.equalsIgnoreCase(form.getName())) {
	// formGroup = form;
	// } else if (FORM_ROLE.equalsIgnoreCase(form.getName())) {
	// formRole = form;
	// } else if (FORM_USER.equalsIgnoreCase(form.getName())) {
	// formUser = form;
	// }
	// }
	// }
	// }
	@Path("")
	@GET
	public List<FormioForm> findForm(@Context UriInfo uriInfo) {
		List<FormioForm> response = null;
		MultivaluedMap<String, String> params = uriInfo.getPathParameters();
		try {
			response = formioService.queryForms(params);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		return response;
	}
	
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

	@GET
	@Path("/{formId}/submission")
	@Consumes({ RestMediaType.APPLICATION_JSON })
	@Produces({ RestMediaType.APPLICATION_JSON })
	public RestResponse<List<Submission>> getSubmissions(@RestPath String formId, @Context UriInfo uriInfo) {
		List<Submission> submissions = null;
		ResponseBuilder<List<Submission>> builder = null;
		RestResponse<List<Submission>> clientResponse = null;
		try {
			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); //parseQueryParams(formId, uriInfo);
			// Map<String, String> filters = new HashMap<String, >();

			clientResponse = formioService.getSubmissions(formId, queryParams);
			submissions = clientResponse.getEntity();
			if (submissions != null) {
				submissionUtil.loadReferenceSubmissions(submissions);
				// for(Submission submission : submissions) {
				// if (submission.getData() != null) {
				// loadReferenceSubmissions((Map<String, Object>)submission.getData());
				// }
				// }
			}
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		builder = createResponseBuilder(clientResponse);

		// for(Map.Entry<String, List<String>> header :
		// clientResponse.getStringHeaders().entrySet()) {
		// builder = builder.header(header.getKey(), header.getValue());
		// }
		// builder = builder.type(MediaType.APPLICATION_JSON);
		return builder.build();
	}

	@Path("/{formId}/submission")
	@POST
	public Submission createSubmission(@RestPath String formId, Submission submission) {
		Submission createdSubmission = null;
		// Formsflow formsflow = null;
		try {
			// formsflow = formsflowService.getById(formId);
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
			for (Submission submission : payload) {
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
	
	@Path("/{formId}/submission/delete")
	@DELETE
	public RestResponse<Object> deleteSubmissions(@RestPath String formId, List<String> submissionIds) {
		if (submissionIds != null) {
			for (String submissionId : submissionIds) {
				try {
					Submission deletedSubmission = formioService.deleteSubmission(formId, submissionId);
				} catch (WebApplicationException e) {
					e.printStackTrace();
					return ResponseBuilder.serverError().build();
				}
			}
		}
		return ResponseBuilder.accepted().build();
	}

	@Path("/{formId}/submission/{submissionId}")
	@GET
	public Submission getSubmission(@RestPath String formId, @RestPath String submissionId) {
		Submission submission = null;
		try {
			submission = formioService.getSubmission(formId, submissionId);
			if (submission != null) {
				// Load reference;
				submissionUtil.loadReferenceSubmissions(Arrays.asList(submission));
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

	@Path("/{formId}/submission/{submissionId}/submissionAction")
	@POST
	public ActionResult callSubmissionAction(@RestPath String formId, @RestPath String submissionId,
			Map<String, Object> params) {
		ActionResult result = new ActionResult();
		result.setName("submissionAction");
		result.setFormId(formId);
		result.setSubmissionId(submissionId);
		result.setParams(params);
		String action = (String) params.get(ACTION);
		if (CommissionService.ACTION_CALCULATE.equalsIgnoreCase(action)) {
			try {
				MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
				queryParams.add("name", CommissionPolicy.FORM_DETAIL);
				List<Submission> detailCommissions = null;
				List<FormioForm> detailForms = formioService.queryForms(queryParams);
				if (detailForms != null && detailForms.size() >= 1) {
					RestResponse<List<Submission>> response = formioService.getSubmissions(detailForms.get(0).get_id(),
							null);
					detailCommissions = response.getEntity();
				}
				if (detailCommissions != null && detailCommissions.size() > 0) {
					// Load reference;
					submissionUtil.loadReferenceSubmissions(detailCommissions);
					Submission headerSubmission = (Submission) SubmissionUtil.getFieldValue(detailCommissions.get(0),
							"policy");
					CommissionPolicy commissionPolicy = new CommissionPolicy(headerSubmission, detailCommissions,
							params);
					result = commissionService.calculateCommision(commissionPolicy);
				}
			} catch (WebApplicationException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * Create other submission base on current submission
	 */
	@Path("/{formId}/submission/{submissionId}/submissionCreate")
	@POST
	public ActionResult createOtherSubmission(@RestPath String formId, @RestPath String submissionId,
			Map<String, Object> params) {
		ActionResult result = new ActionResult();
		result.setName("createSubmission");
		result.setFormId(formId);
		result.setSubmissionId(submissionId);
		result.setParams(params);
		String openFormId = (String) params.get(OPEN_FORM_ID);
		if (openFormId == null) {
			openFormId = formId;
		}
		Submission submission = formioService.getSubmission(formId, submissionId);
		if (submission != null) {
			Submission createdSubmission = formioService.createSubmission(openFormId, submission);
			result.setSubmissionId(createdSubmission.get_id());
		}
		return result;
	}

	// private void loadReferenceSubmissions(Map<String, Object> data) {
	// Map<String, Submission> mapRefSubmissions = new HashMap<String,
	// Submission>();
	// for(Map.Entry<String, Object> entry : data.entrySet()) {
	// if (entry.getValue() instanceof Map) {
	// Map<String, String> ref = (Map<String, String>)entry.getValue();
	// Submission refSubmission = getReferenceSubmission(ref);
	// if (refSubmission != null) {
	// String key = ref.get(Submission.FORM) + "#" + ref.get(Submission._ID);
	// mapRefSubmissions.put(entry.getKey(), refSubmission);
	// }
	// } else if (entry.getValue() instanceof List) {
	// for(Object element : (List) entry.getValue()) {
	// if (element instanceof Map) {
	// loadReferenceSubmissions((Map<String, Object>)element);
	// }
	// }
	//
	// }
	// }
	// data.putAll(mapRefSubmissions);
	// }

	// private Submission getReferenceSubmission(Map<String, String> ref) {
	// String formId = ref.get(Submission.FORM);
	// String submissionId = ref.get(Submission._ID);
	// Submission refSubmission = null;
	// if (formId != null && submissionId != null) {
	// refSubmission = getSubmission(formId, submissionId);
	// }
	// return refSubmission;
	// }
}
