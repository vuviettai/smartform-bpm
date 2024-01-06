package com.smartform.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import com.smartform.rest.client.FormioService;
import com.smartform.rest.model.Submission;
import com.smartform.rest.model.Submissions;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@Path("/master")
public class MasterResource extends AbstractResource{

	@RestClient
	FormioService formioService;
	
	@Path("/{formId}/convertGeoInfoVn")
	@PUT
	public List<Submission> uploadSubmissions(@RestPath String formId, String destForm) {
		List<Submission> uploadedSubmissions = new ArrayList<Submission>();
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		params.putSingle("limit", String.valueOf(Integer.MAX_VALUE));
		//Delete old data
		RestResponse<List<Submission>> res = formioService.getSubmissions(destForm, params);
		List<Submission> submissions = res.getEntity();
		if (submissions != null && submissions.size() > 0) {
			for(Submission submission : submissions) {
				formioService.deleteSubmission(destForm, submission.get_id());
			}
		}
		res = formioService.getSubmissions(formId, params);
		submissions = res.getEntity();
		if (submissions != null && submissions.size() > 0) {
			Map<String, Submission> mapProvinces = new LinkedHashMap<String, Submission>();
			Map<String, Submission> mapDistricts = new LinkedHashMap<String, Submission>();
			//Map between district with it's parent
			Map<Submission, Submission> mapDistrict = new LinkedHashMap<Submission, Submission>();
			//Map between ward with it's parent
			Map<Submission, Submission> mapWards = new LinkedHashMap<Submission, Submission>();
			//Store Province
			for(Submission item : submissions) {
				Map<String, Object> itemData = item.getData();
				String province = (String)itemData.get("province");
				String provinceCode = (String)itemData.get("provinceCode");
				String district = (String) itemData.get("district");
				String districtCode = (String) itemData.get("districtCode");
				String ward = (String)itemData.get("ward");
				String wardCode = (String)itemData.get("wardCode");
				String districtKey = "province" + "#" + district;
				Submission provinceSub = mapProvinces.get(province);
				if (provinceSub == null) {
					provinceSub = createSubmission(item, destForm, province, provinceCode, "province", null);
					provinceSub = formioService.createSubmission(destForm, provinceSub);
					mapProvinces.put(province, provinceSub);
				}
				Submission districtSub = mapDistricts.get(districtKey);
				if (districtSub == null) {
					districtSub = createSubmission(item, destForm, district, districtCode, "district", provinceSub);
					districtSub = formioService.createSubmission(destForm, provinceSub);
					mapDistricts.put(districtKey, districtSub);
				}
				Submission wardSub = createSubmission(item, destForm, ward, wardCode, "ward", districtSub);
				Submission createdWardSubmission = formioService.createSubmission(destForm, wardSub);
			}
			for(Map.Entry<String, Submission> entry : mapProvinces.entrySet()) {
				Submission createdSubmission = formioService.createSubmission(destForm, entry.getValue());
				entry.getValue().set_id(createdSubmission.get_id());
			}
			//Store district
			
//			//Store wards
//			List<Submission> payload = submissions.toSubmissionList();
//			for (Submission submission : payload) {
//				try {
//					Submission createdSubmission = formioService.createSubmission(formId, submission);
//					uploadedSubmissions.add(createdSubmission);
//				} catch (WebApplicationException e) {
//					e.printStackTrace();
//				}
//			}
		}
		return uploadedSubmissions;
	}
	private Submission createSubmission(Submission item, String form, String name, String code, String type, Submission parent) {
		Submission submission = new Submission();
		Map<String, Object> provinceData = new HashMap<String, Object>();
		provinceData.put("name", name);
		provinceData.put("code", code);
		provinceData.put("type", type);
		submission.setData(provinceData);
		submission.setAccess(item.getAccess());
		submission.setCreated(new Date());
		submission.setExternalIds(new ArrayList<String>());
		submission.setForm(form);
		submission.setMetadata(item.getMetadata());
		submission.setOwner(item.getOwner());
		submission.setRoles(item.getRoles());
		if (parent != null) {
			Map<String, Object> parentData = Map.of("formId", form, "submissionId", parent.get_id());
			submission.getData().put("parent", parentData);
		}
		return submission;
	}
}
