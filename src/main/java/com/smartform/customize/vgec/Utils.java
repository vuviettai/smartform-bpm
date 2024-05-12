package com.smartform.customize.vgec;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.Sumif;

import com.smartform.rest.model.Submission;
import com.smartform.utils.SubmissionUtil;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public class Utils {
	public static MultivaluedMap<String, String> prepareParamsLastMonthContract() {
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		YearMonth lastMonth = YearMonth.now().minusMonths(1);
		String firstDate = lastMonth.atDay(1).format(DateTimeFormatter.ISO_DATE);
		String endDate = lastMonth.atEndOfMonth().format(DateTimeFormatter.ISO_DATE);
		params.putSingle("contractDate__ge",firstDate);
		params.putSingle("contractDate__le",endDate);
		return params;
	}
	
	public static MultivaluedMap<String, String> prepareParamsNoptienByContracts(List<String> contractIds) {
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		if (contractIds != null && contractIds.size() > 0) {
			params.addAll("contract__in", contractIds);
//			StringJoiner stringJoiner = new StringJoiner(",");
//			contractIds.forEach(stringJoiner::add);
//			String commaSeparatedString = stringJoiner.toString();
//			params.putSingle("contract__in", commaSeparatedString);
		}
		return params;
	}
	public static List<String> getContractIds(List<Submission> contracts) {
		List<String> result = new ArrayList<String>();
		if (contracts != null) {
			for (Submission submission : contracts) {
				result.add(submission.getId());
			}
		}
		return result;
	}
	
	public static List<String> getContractNames(List<Submission> contracts) {
		List<String> result = new ArrayList<String>();
		if (contracts != null) {
			for (Submission submission : contracts) {
				String name = (String)SubmissionUtil.getFieldValue(submission, "contractNumber");
				result.add(name);
			}
		}
		return result;
	}

//	public static Map<String, List<Submission>> groupContractsByTVV(List<Submission> contracts) {
//		Map<String, List<Submission>> result = new HashMap<String, List<Submission>>();
//		if (contracts != null && contracts.size() > 0) {
//			for (Submission contract : contracts) {
//				Object tvv = SubmissionUtil.getFieldValue(contract, "tuvanvien");
//				String tvvId = null;
//				if (tvv instanceof Map) {
//					tvvId = ((Map<String, String>)tvv).get(Submission._ID);
//				} else if (tvv instanceof String) {
//					tvvId = (String)tvv;
//				}
//				List<Submission> list = result.get(tvvId);
//				if (list == null) {
//					list = new ArrayList<Submission>();
//					result.put(tvvId, list);
//				}
//				list.add(contract);
//			}
//		}
//		return result;
//	}
}
