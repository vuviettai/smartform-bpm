package com.smartform.resources;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;
import org.jboss.resteasy.reactive.common.util.RestMediaType;

import com.smartform.customize.handler.FormActionHandler;
import com.smartform.customize.handler.SubmissionActionHandler;
import com.smartform.customize.service.MongodbService;
import com.smartform.models.ActionResult;
import com.smartform.models.xlsx.XlsxWorkboolModel;
import com.smartform.rest.client.FormsflowService;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;
import com.smartform.rest.model.Submissions;
import com.smartform.storage.mongo.entity.EntityMapper;
import com.smartform.utils.SubmissionUtil;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

@Path("/form")
public class FormioResource extends AbstractResource {

	public static final String OPEN_FORM_ID = "openFormId";
	public static final String FORM_USER = "formuser";
	public static final String FORM_GROUP = "formgroup";
	public static final String FORM_ROLE = "formrole";
	public static final String TEMPLATE_LAST_CELL = "lastCell";

	@Inject
    EntityMapper entityMapper
    ;
	@RestClient
	@Inject
	FormsflowService formsflowService;
	
	@Inject
	SubmissionActionHandler actionHandler;
	@Inject
	FormActionHandler formActionHandler;
	
	@Inject
	private SubmissionUtil submissionUtil;

	@Inject
	private MongodbService mongodbService;
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
	@GET
	public List<FormioForm> findForm(@Context UriInfo uriInfo) {
		List<FormioForm> response = null;
		MultivaluedMap<String, String> params = uriInfo.getPathParameters();
		try {
			Stream<com.smartform.storage.mongo.entity.FormioForm> entities = formioService.queryFormAsStream(params);
			response = entities.map(entity -> entityMapper.toModel(entity)).collect(Collectors.toList());
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
			com.smartform.storage.mongo.entity.FormioForm entity = formioService.getFormById(formId);
			if (entity != null) {
				formioForm = entityMapper.toModel(entity);
			}
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
		try {
			MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>(uriInfo.getQueryParameters()); //parseQueryParams(formId, uriInfo);
			injectQueryParams(formId, queryParams);
			// Map<String, String> filters = new HashMap<String, >();
			List<String> refFields = queryParams.remove("refField");
			List<String> refIds = queryParams.remove("refId");
			if (refFields != null && refFields.size() > 0 && refIds != null && refIds.size() > 0) {
				queryParams.add(refFields.get(0), refIds.get(0));
			}
			
//			submissions = mongodbService.getSubmissions(formId, queryParams);
//			builder = ResponseBuilder.ok(submissions, MediaType.APPLICATION_JSON);
//			List<com.smartform.storage.mongo.entity.Submission> entities = formioService.getSubmissionAsStream(formId, queryParams).collect(Collectors.toList());
			submissions = formioService.getSubmissionAsStream(formId, queryParams).map(entity -> entityMapper.toModel(entity)).collect(Collectors.toList());
			// builder = createResponseBuilder(clientResponse);
			
			if (submissions != null) {
				submissionUtil.loadReferenceSubmissions(submissions);
				// for(Submission submission : submissions) {
				// if (submission.getData() != null) {
				// loadReferenceSubmissions((Map<String, Object>)submission.getData());
				// }
				// }
				builder = ResponseBuilder.ok(submissions, MediaType.APPLICATION_JSON);	
			} else {
				builder = ResponseBuilder.<List<Submission>>noContent();
			}
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}
		

		// for(Map.Entry<String, List<String>> header :
		// clientResponse.getStringHeaders().entrySet()) {
		// builder = builder.header(header.getKey(), header.getValue());
		// }
		// builder = builder.type(MediaType.APPLICATION_JSON);
		return builder.build();
	}
	@GET
	@Path("/{formId}/submission/filter")
	@Consumes({ RestMediaType.APPLICATION_JSON })
	@Produces({ RestMediaType.APPLICATION_JSON })
	public RestResponse<List<Submission>> filterSubmissions(@RestPath String formId, @Context UriInfo uriInfo) {
		List<Submission> submissions = null;
		ResponseBuilder<List<Submission>> builder = null;
		try {
			MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>(uriInfo.getQueryParameters()); //parseQueryParams(formId, uriInfo);
			injectQueryParams(formId, queryParams);
			List<String> refFields = queryParams.remove("refField");
			List<String> refIds = queryParams.remove("refId");
			if (refFields != null && refFields.size() > 0 && refIds != null && refIds.size() > 0) {
				queryParams.add(refFields.get(0), refIds.get(0));
			}
			submissions = mongodbService.getSubmissions(formId, queryParams);
			if (submissions == null || submissions.isEmpty()) {
				builder = ResponseBuilder.noContent();
			} else {
				builder = ResponseBuilder.ok(submissions, MediaType.APPLICATION_JSON);
			}
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
		return builder.build();
	}
	@Path("/{formId}/submission")
	@POST
	public Submission createSubmission(@RestPath String formId, Submission submission) {
		Submission createdSubmission = null;
		// Formsflow formsflow = null;
		try {
			injectPartnerGroup(submission);
			createdSubmission = formioService.createSubmission(formId, submission);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}

		return createdSubmission;
	}
	
	@Path("/{formId}/submission/{customAction}")
	@POST
	public Submission createSubmission(@RestPath String formId, @RestPath String customAction, Submission submission) {
		Submission createdSubmission = null;
		// Formsflow formsflow = null;
		try {
			// formsflow = formsflowService.getById(formId);
			if(customAction != null) {
				actionHandler.prepareSubmission(formId, submission, customAction);
			} 
			injectPartnerGroup(submission);
			createdSubmission = formioService.createSubmission(formId, submission);
			if(customAction != null) {
				createdSubmission.setExtraParams(submission.getExtraParams());
				submissionUtil.loadReferenceSubmissions(Arrays.asList(createdSubmission));
				actionHandler.onSubmissionCreated(formId, createdSubmission, customAction);
			} 
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}

		return createdSubmission;
	}
	@Path("/{formId}/formAction")
	@POST
	public ActionResult callFormAction(@RestPath String formId,	Map<String, Object> params) {
		ActionResult result = new ActionResult();
		result.setName("formAction");
		result.setFormId(formId);
		result.setParams(params);
		ActionResult handleResult = formActionHandler.handleAction(formId, params);
		return result;
	}
	@Path("/{formId}/submission/upload")
	@POST
	public List<Submission> uploadSubmissions(@RestPath String formId, Submissions submissions) {
		List<Submission> uploadedSubmissions = new ArrayList<Submission>();
		if (submissions != null) {
			List<Submission> payload = submissions.toSubmissionList();
			injectPartnerGroup(payload);
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
			formioService.deleteSubmissions(submissionIds);
//			for (String submissionId : submissionIds) {
//				try {
//					Submission deletedSubmission = formioService.deleteSubmission(submissionId);
//				} catch (WebApplicationException e) {
//					e.printStackTrace();
//					return ResponseBuilder.serverError().build();
//				}
//			}
		}
		return ResponseBuilder.accepted().build();
	}

	@Path("/{formId}/submission/{submissionId}")
	@GET
	public Submission getSubmission(@RestPath String formId, @RestPath String submissionId) {
		Submission submission = null;
		try {
			submission = formioService.getSubmissionModel(formId, submissionId);
			if (submission != null) {
				// Load reference;
				Optional<String> partnerGroup = getPartnerGroupName(identity);
				if (partnerGroup.isPresent() && hasPartnerField(formId)) {
					if(submission.getData() != null 
							&& partnerGroup.get().equals(submission.getData().get(FORMIO_COMPONENT_PARTNER))) {
						submissionUtil.loadReferenceSubmissions(Arrays.asList(submission));
					} else {
						submission = null;
					}
				} else {
					submissionUtil.loadReferenceSubmissions(Arrays.asList(submission));
				}
				
			}
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}

		return submission;
	}
	
	@Path("/{formId}/submission/{submissionId}/xlsxTemplate")
	@GET
	public List<XlsxWorkboolModel> getXlsxTemplate(@RestPath String formId, @RestPath String submissionId, 
			@QueryParam("templateField") String templateField) {
		List<XlsxWorkboolModel> xlsxModels = new ArrayList<XlsxWorkboolModel>();
		Submission submission = null;
		try {
			submission = formioService.getSubmissionModel(formId, submissionId);
			if (submission != null) {
				// Load reference;
				if (templateField == null) {
					templateField = "templateFile";
				}
				Object templateFiles = SubmissionUtil.getFieldValue(submission, templateField);
				Object lastCell = SubmissionUtil.getFieldValue(submission, TEMPLATE_LAST_CELL);
				if (templateFiles instanceof List) {
					for (Object tplFile : (List)templateFiles) {
						Map<String, Object> mapTplProps = (HashMap<String, Object>) tplFile;
						String type = (String) mapTplProps.get("type");
						String storage = (String) mapTplProps.get("storage");
						String url = (String)mapTplProps.get("url");
						if ("base64".equals(storage) && url != null && type != null) {
							String base64Encoded = url.substring("data".length() + type.length() + storage.length() + 3);
							Base64.Decoder decoder = Base64.getDecoder();
							try {
								ByteArrayInputStream bais = new ByteArrayInputStream(decoder.decode(base64Encoded));
								Workbook workbook = new XSSFWorkbook(bais);
								XlsxWorkboolModel workbookModel = new XlsxWorkboolModel();
								workbookModel.parse(workbook, lastCell);
								workbookModel.setStorage(storage);
								workbookModel.setType(type);
								xlsxModels.add(workbookModel);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
					}
				}
			}
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}

		return xlsxModels;
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
	
	@Path("/{formId}/submission/{submissionId}/{customAction}")
	@PUT
	public Submission putSubmission(@RestPath String formId, @RestPath String submissionId, @RestPath String customAction, Submission submission) {
		Submission updated = null;
		try {
			actionHandler.prepareSubmission(formId, submission, customAction);
			updated = formioService.putSubmission(formId, submissionId, submission);
			updated.setExtraParams(submission.getExtraParams());
			actionHandler.onSubmissionUpdated(formId, updated, customAction);
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}

		return updated;
	}

	@Path("/{formId}/submission/{submissionId}")
	@DELETE
	public Submission deleteSubmission(@RestPath String formId, @RestPath String submissionId) {
		Submission result = null;
		boolean deleted = false;
		try {
			Optional<String> partnerGroup = getPartnerGroupName(identity);
			if (partnerGroup.isEmpty()) {
				result = formioService.getSubmissionModel(formId, submissionId);
				if (result != null) {
					deleted = formioService.deleteSubmission(submissionId);
				}
			} else {
				result = getSubmission(formId, submissionId);
				if (result != null && result.getData() != null 
						&& partnerGroup.get().equals(result.getData().get(KEYCLOAK_GROUP_PARTNER))) {
					deleted = formioService.deleteSubmission(submissionId);
				}
			}
		} catch (WebApplicationException e) {
			e.printStackTrace();
		}

		return result;
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
		ActionResult handleResult = actionHandler.handleAction(formId, submissionId, params);
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
		Submission submission = formioService.getSubmissionModel(formId, submissionId);
		if (submission != null && openFormId != null) {
			try { 
				Optional<String> partnerGroup = getPartnerGroupName(identity);
				if (partnerGroup.isPresent() && submission.getData() != null) {
					submission.getData().put(KEYCLOAK_GROUP_PARTNER, partnerGroup.get());
				}
				submission.setForm(null);
				submission.set_id(null);
				Submission createdSubmission = formioService.createSubmission(openFormId, submission);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
