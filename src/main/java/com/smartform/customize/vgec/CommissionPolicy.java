package com.smartform.customize.vgec;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;
import com.smartform.utils.SubmissionUtil;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

public class CommissionPolicy {
	public static final String FORM_BENEFICIARY 				= "form_tvv";
	public static final String FORM_CONTRACT 					= "form_contract";
	public static final String FORM_NOPTIEN 					= "form_noptien";
	public static final String FORM_COMMISSION 					= "form_commission";
	public static final String FORM_COMMISSION_TRAN 			= "form_commissiontran";
	public static final String COMMISSION_PERIOD 				= "period";
	public static final String COMMISSION						= "commission";
	public static final String COMMISSION_LEVELS 				= "commissionLevels";
	public static final String COMMISSION_TRANS 				= "commissionTrans";
	public static final String COMMISSION_MIN_CONTRACT_AMOUNT 	= "minContractAmount";
	public static final String COMMISSION_MAX_CONTRACT_AMOUNT 	= "maxContractAmount";
	public static final String COMMISSION_VALUE 				= "commission";
	public static final String COMMISSION_POLICY 				= "policy";
	public static final String COMMISSION_PERIOD_UNIT			= "periodUnit";
	public static final String COMMISSION_PERIOD_YEARLY 		= "yearly";
	public static final String COMMISSION_PERIOD_QUARTERLY 		= "quarterly";
	public static final String COMMISSION_PERIOD_MONTHLY 		= "monthly";
	public static final String COMMISSION_PERIOD_WEEKLY 		= "weekly";
	public static final String COMMISSION_PERIOD_DAILY 			= "daily";
	public static final String COMMISSION_BENEFICIARY 			= "beneficiary";
	public static final String COMMISSION_CONTRACT 				= "contract";
	public static final String COMMISSION_TVV 					= "tuvanvien";
	public static final String COMMISSION_TRAN_STATUS			= "status";
	public static final String COMMISSION_TRAN_STATUS_TEMPORARY	= "temporary";
	public static final String PATTERN_YEAR_MONTH				= "yyyy-MM";
	public static final String PATTERN_DATE						= "yyyy-MM-dd";

	private Submission submission;
	private Map<String, Object> params;

	public CommissionPolicy(Submission submission, Map<String, Object> params) {
		super();
		this.submission = submission;
		this.params = params;
	}

	public Submission getSubmission() {
		return submission;
	}

	public void setSubmission(Submission submission) {
		this.submission = submission;
	}
	public String getPolicyPeriod() {
		String period = (String)params.get(COMMISSION_PERIOD);
		ZonedDateTime inputPeriod = parsePeriod(period);
		//Period using component Formio's Day has format "MM/dd/YYYY";
		String periodUnit = (String)SubmissionUtil.getFieldValue(submission, "periodUnit");
		if(CommissionPolicy.COMMISSION_PERIOD_YEARLY.equalsIgnoreCase(periodUnit)) {
			YearMonth yearValue = null;
			if (inputPeriod != null) {
				yearValue = YearMonth.from(inputPeriod);
			} else {
				yearValue = YearMonth.now().minusYears(1);
			}
			
			period = String.valueOf(yearValue.getYear());
		}
		else if(CommissionPolicy.COMMISSION_PERIOD_QUARTERLY.equalsIgnoreCase(periodUnit)) {
			YearMonth yearMonth = null;
			if (inputPeriod != null) {
				yearMonth = YearMonth.from(inputPeriod);
			} else {
				yearMonth = YearMonth.now().minusMonths(3);
			}
			period = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-QQ"));
		}
		else if(CommissionPolicy.COMMISSION_PERIOD_MONTHLY.equalsIgnoreCase(periodUnit)) {
			YearMonth yearMonth = null;
			if (inputPeriod != null) {
				yearMonth = YearMonth.from(inputPeriod);
			} else {
				yearMonth = YearMonth.now().minusMonths(1);
			}
			period = yearMonth.format(DateTimeFormatter.ofPattern(PATTERN_YEAR_MONTH));
		}
		else if(CommissionPolicy.COMMISSION_PERIOD_WEEKLY.equalsIgnoreCase(periodUnit)) {
			Calendar calendar = Calendar.getInstance();
			
			//period = lastMonth.format(DateTimeFormatter.ofPattern("YYYY-MM-W"));
		}
		else if(CommissionPolicy.COMMISSION_PERIOD_DAILY.equalsIgnoreCase(periodUnit)) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_DATE);
			period = simpleDateFormat.format(calendar.getTime());
		}
		
		return period;
	}
	public MultivaluedMap<String, String> prepareParamsContract(String period) {
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		String periodUnit = (String)SubmissionUtil.getFieldValue(submission, "periodUnit");
		String field = "data.contractDate";
		if(CommissionPolicy.COMMISSION_PERIOD_YEARLY.equalsIgnoreCase(periodUnit)) {
			try {
				int year = Integer.parseInt(period);
				params.add(field + "__gte", String.valueOf(year));
				params.add(field + "__lt", String.valueOf(year + 1));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		else if(CommissionPolicy.COMMISSION_PERIOD_QUARTERLY.equalsIgnoreCase(periodUnit)) {
			
			//period = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-QQ"));
		}
		else if(CommissionPolicy.COMMISSION_PERIOD_MONTHLY.equalsIgnoreCase(periodUnit)) {
			String[] parts = period.split("-");
			try {
				ZoneId zoneId = ZoneId.of("GMT+0");
				int year = Integer.parseInt(parts[0]);
				int month = Integer.parseInt(parts[1]);
				ZonedDateTime zdt = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, zoneId);
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern(PATTERN_DATE);
				params.add(field + "__gte", zdt.format(dtf));
				zdt = zdt.plusMonths(1);
				params.add(field + "__lt", zdt.format(dtf));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		else if(CommissionPolicy.COMMISSION_PERIOD_WEEKLY.equalsIgnoreCase(periodUnit)) {
			Calendar calendar = Calendar.getInstance();
			
			//period = lastMonth.format(DateTimeFormatter.ofPattern("YYYY-MM-W"));
		}
		else if(CommissionPolicy.COMMISSION_PERIOD_DAILY.equalsIgnoreCase(periodUnit)) {
			
		}
		return params;
	}
	private ZonedDateTime parsePeriod(String period) {
		ZonedDateTime zdt = null;
		try {
//			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//			zdt = ZonedDateTime.parse( period , dtf );
			String[] parts = period.split("/");
			if (parts.length == 3) {
				int month = Integer.parseInt(parts[0]);
				int day = Integer.parseInt(parts[1]);
				if (day <= 0) {
					day = 1;
				}
				int year = Integer.parseInt(parts[2]);
				zdt = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.of("GMT+0"));
			}
		} catch (DateTimeParseException e) {
			// TODO Auto-generated catch block
		}
		return zdt;
	}
	public String getFormBeneficiary() {
		String formName = params != null ? (String) params.get(FORM_BENEFICIARY) : null;
		return formName != null ? formName : FORM_BENEFICIARY;
	}

	public String getFormContract() {
		String formName = params != null ? (String) params.get(FORM_CONTRACT) : null;
		return formName != null ? formName : FORM_CONTRACT;
	}

	public String getFormNoptien() {
		String formName = params != null ? (String) params.get(FORM_NOPTIEN) : null;
		return formName != null ? formName : FORM_NOPTIEN;
	}

	public String getFormCommission() {
		String formName = params != null ? (String) params.get(FORM_COMMISSION) : null;
		return formName != null ? formName : FORM_COMMISSION;
	}

	public String getFormCommissionTran() {
		String formName = params != null ? (String) params.get(FORM_COMMISSION_TRAN) : null;
		return formName != null ? formName : FORM_COMMISSION_TRAN;
	}

	public BeneficiaryCommission createCommission(FormioForm formCommission, FormioForm formCommissionTran, String beneficiaryFormId, String beneficiaryId, String period, 
			List<Submission> contracts ) {
		if (contracts == null || contracts.size() == 0) return null;
		Submission headerCommission = null;
		List<Submission> detailCommissions = null;
		Float commissionPerContract = null;
		int contractCount = contracts.size();
		Number value = null;
		Object levels = SubmissionUtil.getFieldValue(this.submission, COMMISSION_LEVELS);
		if (levels instanceof List) {
			List<Map<String, Object>> commissionLevels = (List<Map<String, Object>>) levels;
			for (Map<String, Object> level : commissionLevels) {
				Object minContractAmount = level.get(COMMISSION_MIN_CONTRACT_AMOUNT);
				Object maxContractAmount = level.get(COMMISSION_MAX_CONTRACT_AMOUNT);
				
				if (minContractAmount instanceof Number 
						&& maxContractAmount instanceof Number
						&& (Integer)minContractAmount <= contractCount
						&& (Integer)maxContractAmount > contractCount) {
					Object commissionValue = level.get(COMMISSION_VALUE);
					if (commissionValue instanceof Number) {
						commissionPerContract = ((Number) commissionValue).floatValue();
						value = commissionPerContract * contractCount;
					}
					break;
				}
			}
		}
		if (value != null) {
			Map<String, String> policyRef = Map.of(Submission.FORM, submission.getForm(), Submission._ID, submission.get_id());
			Map<String, String> beneficiaryRef = Map.of(Submission.FORM, beneficiaryFormId, Submission._ID, beneficiaryId);
			Map<String, Object> data = new HashMap<String, Object>();
			StringJoiner stringJoiner = new StringJoiner(", ");
			Utils.getContractNames(contracts).forEach(stringJoiner::add);
			String commaSeparatedString = stringJoiner.toString();
			data.put(COMMISSION_POLICY, policyRef);
			data.put("group", "tuvanvien");
			data.put(COMMISSION_BENEFICIARY, beneficiaryRef);
			data.put("contractCount", contractCount);
			data.put("totalAmount", value);
			data.put("description", commaSeparatedString);
			data.put("period",period);
			headerCommission = new Submission(formCommission.get_id(), data);
			detailCommissions = createCommissionTrans(formCommissionTran, policyRef, beneficiaryRef, period, contracts, commissionPerContract);
		}
		return new BeneficiaryCommission(headerCommission, detailCommissions);
	}
	List<Submission> createCommissionTrans(FormioForm form, Map<String, String> policyRef, Map<String, String> beneficiaryRef, String period, 
			List<Submission> contracts, Float commissionPerContract) {
		List<Submission> commissionTrans = new ArrayList<Submission>();
		Object comTranvalue = SubmissionUtil.getFieldValue(this.submission, COMMISSION_TRANS);
		List<Map<String, Object>> commissionRounds = null;
		if (comTranvalue instanceof List) {
			commissionRounds = (List<Map<String, Object>>)comTranvalue;
		}
		if (commissionRounds != null) {
			/*
			 * Mỗi hợp đồng sinh ra số giao dịch bằng số lần được cấu hình,
			 * Lần cuối là số tiền còn 
			 */
			for (Submission contract : contracts) {
				Map<String, String> contractRef = SubmissionUtil.createReferenceMap(contract);
				Float remainValue = commissionPerContract;
				Map<String, Object> remainData = null;
				for(Map<String, Object> round : commissionRounds) {
					Object objValue = round.get("value");
					Float value = (objValue instanceof Number) ? ((Number) objValue).floatValue() : null ;
					if (value != null) {
						remainValue -= value;
						Map<String, Object> data = new HashMap<String, Object>(round);
						data.put(COMMISSION_POLICY, policyRef);
						data.put(COMMISSION_BENEFICIARY, beneficiaryRef);
						data.put(COMMISSION_CONTRACT, contractRef);
						data.put(COMMISSION_TRAN_STATUS, "temporary");
						data.put("period",period);		
						Submission comTran = new Submission(form.get_id(), data);
						commissionTrans.add(comTran);
					} else {
						remainData = round;
					}
				}
				if (remainValue > 0 && remainData != null ) {
					Map<String, Object> data = new HashMap<String, Object>(remainData);
					data.put(COMMISSION_POLICY, policyRef);
					data.put(COMMISSION_BENEFICIARY, beneficiaryRef);
					data.put(COMMISSION_CONTRACT, contractRef);
					data.put(COMMISSION_TRAN_STATUS, "temporary");		
					data.put("period",period);					
					data.put("value", remainValue);
					Submission comTran = new Submission(form.get_id(), data);
					commissionTrans.add(comTran);
				}
			}
		}
		return commissionTrans;
	}
	public static Submission mergeCommission(Submission dest, Submission update) {
		if (dest == null) return update;
		dest.setModified(new Date());
		dest.getData().putAll(update.getData());
		return dest;
	}
}
