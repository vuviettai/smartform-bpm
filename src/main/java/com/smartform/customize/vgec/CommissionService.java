package com.smartform.customize.vgec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.smartform.models.ActionResult;
import com.smartform.rest.model.FormioForm;
import com.smartform.rest.model.Submission;
import com.smartform.utils.SubmissionUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

@ApplicationScoped
public class CommissionService {
	public static final String ACTION_CALCULATE = "calculateCommission";
    
	@Inject
	private SubmissionUtil submissionUtil;
	
//	public CommissionService(FormioService formioService) {
//		super();
//		this.submissionUtil = new SubmissionUtil(formioService);
//	}

	public ActionResult calculateCommision(CommissionPolicy commissionPolicy) {
		ActionResult actionResult = new ActionResult();
		//1. Get all TVV applied current policy
		// MultivaluedMap<String, String> queryParams = null;
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		//List<Submission> tvvs = submissionUtil.getSubmissionsByFormName(commissionPolicy.getFormTVV(), params);
		
//		//3. Get so lan nop tien theo cac hop dong de sinh ra so tien se chuyen cho TVV
//		params = Utils.prepareParamsNoptienByContracts(contractIds);
//		List<Submission> noptiens = submissionUtil.getSubmissionsByFormName(commissionPolicy.getFormNoptien(), params);
		
		//4. Sinh ra so tien hoa hong cho TVV theo ket qua cua thang truoc
//		FormioForm formBeneficiary = submissionUtil.getFormByName(commissionPolicy.getFormBeneficiary());
//		FormioForm formCommission = submissionUtil.getFormByName(commissionPolicy.getFormCommission());
//		FormioForm formCommissionTran = submissionUtil.getFormByName(commissionPolicy.getFormCommissionTran());
		FormioForm formBeneficiary = submissionUtil.getFormById(commissionPolicy.getFormBeneficiary());
		FormioForm formCommission = submissionUtil.getFormById(commissionPolicy.getFormCommission());
		FormioForm formCommissionTran = submissionUtil.getFormById(commissionPolicy.getFormCommissionTran());
		if (formCommission != null && formCommissionTran != null && formBeneficiary != null) {
			String policyPeriod = commissionPolicy.getPolicyPeriod();
			//2. Get all Contracts theo TVV trong thang truoc de xac dinh muc Hoa Hong cho TVV
			params = commissionPolicy.prepareParamsContract(policyPeriod);
			//List<Submission> contracts = submissionUtil.getSubmissionsByFormName(commissionPolicy.getFormContract(), params);
			List<Submission> contracts = submissionUtil.querySubmissionsByFormId(commissionPolicy.getFormContract(), params);
			if (contracts != null && contracts.size() > 0) {
				submissionUtil.loadReferenceSubmissions(contracts);
			}
			List<String> contractIds = Utils.getContractIds(contracts);
			params = new MultivaluedHashMap<String, String>();
			params.put("data.contract._id__in", contractIds);
			params.putSingle("limit", String.valueOf(Integer.MAX_VALUE));
			params.putSingle("sort", "ngayNop");
			// Get ds cac lan nop tien theo contractIds
			Map<Object, List<Submission>> mapNoptiens = null;
			String formNoptien = commissionPolicy.getFormNoptien();
			if (formNoptien != null) {
				List<Submission> dsNopTien = submissionUtil.querySubmissionsByFormId(commissionPolicy.getFormNoptien(), params);
				if (dsNopTien != null) {
					mapNoptiens = SubmissionUtil.groupSubmissionsByField(dsNopTien, "contract");
				}
			}
			
			// Group contract by nguoi thu huong
			Map<Object, List<Submission>> mapGroups = SubmissionUtil.groupSubmissionsByField(contracts, CommissionPolicy.CONTRACT_CONGTACVIEN);
			List<Submission> listCommissions = new ArrayList<Submission>();
			List<Submission> listCommissionTrans = new ArrayList<Submission>();
			
			//Create commission submission by contract list
			for(Map.Entry<Object, List<Submission>> entry : mapGroups.entrySet()) {
				BeneficiaryCommission commission = commissionPolicy.createCommission(formCommission, formCommissionTran, formBeneficiary.get_id(), 
						 String.valueOf(entry.getKey()), policyPeriod, entry.getValue(), mapNoptiens);
				if (commission != null && commission.getHeader() != null) {
					listCommissions.add(commission.getHeader());
				}
				if (commission != null && commission.getDetails() != null) {
					listCommissionTrans.addAll(commission.getDetails());
				}
			}
			List<Submission> listStoredCommissions = updateOrCreateCommissions(formCommission, policyPeriod, listCommissions);
			
			// Update Commission reference before save into DB
			Map<String, Map<String, String>> mapByBeneficiary = new HashMap<String, Map<String,String>>();
			for (Submission submission : listStoredCommissions) {
				Object beneficiary = SubmissionUtil.getFieldValue(submission, CommissionPolicy.COMMISSION_BENEFICIARY);
				mapByBeneficiary.put(String.valueOf(beneficiary), SubmissionUtil.createReferenceMap(submission));
			}
			for (Submission submission : listCommissionTrans) {
				Object beneficiary = SubmissionUtil.getFieldValue(submission, CommissionPolicy.COMMISSION_BENEFICIARY);
				Map<String, String> ref = mapByBeneficiary.get(String.valueOf(beneficiary));
				if (ref != null) {
					submission.getData().put(CommissionPolicy.COMMISSION, ref);
				}
			}
			List<Submission> listStoredCommissionTrans = updateOrCreateCommissionTrans(formCommissionTran, policyPeriod, listCommissionTrans);
		}
		return actionResult;
	}

	private List<Submission> updateOrCreateCommissions(FormioForm formCommission, String policyPeriod, List<Submission> listCommissions) {
		List<Submission> listWriteCommissions = new ArrayList<Submission>();
		List<Submission> listDeleteCommissions = new ArrayList<Submission>();
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		params.putSingle("data.period", policyPeriod);
		params.putSingle("limit", String.valueOf(Integer.MAX_VALUE));
		List<Submission> currentCommissions = submissionUtil.querySubmissionsByFormId(formCommission.get_id(), params);
		Map<Object, List<Submission>> mapByBeneficiary = SubmissionUtil.groupSubmissionsByField(currentCommissions, CommissionPolicy.COMMISSION_BENEFICIARY);
		Set<Object> deletedBeneficiaries = new HashSet<Object>(mapByBeneficiary.keySet());
		for(Submission commission : listCommissions) {
			Object fieldValue = SubmissionUtil.getFieldValue(commission, CommissionPolicy.COMMISSION_BENEFICIARY);
			Object beneficiaryId = (fieldValue instanceof Map) ? ((Map<String, Object>) fieldValue).get(Submission._ID) : null;
			deletedBeneficiaries.remove(beneficiaryId);
			List<Submission> list = mapByBeneficiary.get(beneficiaryId);
			if (list == null || list.size() == 0) {
				listWriteCommissions.add(commission);
			} else if (list.size() == 1){
				//Update new commission value
				Submission updateCommission = CommissionPolicy.mergeCommission(list.get(0), commission);
				listWriteCommissions.add(updateCommission);
			} else if (list.size() > 1) {
				listWriteCommissions.add(commission);
				// Co nhieu hon 1 transaction tra hoa hong boi 1 loi gi do
				listDeleteCommissions.addAll(list);
			}
		}
		for (Object beneficiary : deletedBeneficiaries) {
			listDeleteCommissions.addAll(mapByBeneficiary.get(beneficiary));
		}
		List<Submission> listStoredCommissions = submissionUtil.storeSubmissions(formCommission, listWriteCommissions);
		if (listDeleteCommissions.size() > 0) {
			submissionUtil.deleteSubmissions(formCommission, listDeleteCommissions);
		}
		return listStoredCommissions;
	}
	private List<Submission> updateOrCreateCommissionTrans(FormioForm formCommissionTran, String policyPeriod, List<Submission> listCommissions) {
		List<Submission> listWriteCommissions = new ArrayList<Submission>();
		List<Submission> listDeleteCommissions = new ArrayList<Submission>();
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		params.putSingle("data.period", policyPeriod);
		params.putSingle("limit", String.valueOf(Integer.MAX_VALUE));
		List<Submission> currentCommissions = submissionUtil.querySubmissionsByFormId(formCommissionTran.get_id(), params);
		Map<String, List<Submission>> mapDbSubmission = SubmissionUtil.groupSubmissionsByReference(currentCommissions, CommissionPolicy.COMMISSION_CONTRACT);
		Map<String, List<Submission>> mapCurrentSubmission = SubmissionUtil.groupSubmissionsByReference(listCommissions, CommissionPolicy.COMMISSION_CONTRACT);
		Set<Object> deletedContracts = new HashSet<Object>(mapDbSubmission.keySet());
		for(Map.Entry<String, List<Submission>> entry : mapCurrentSubmission.entrySet()) {
			deletedContracts.remove(entry.getKey());
			Map<Object, Submission> mapCurrent = SubmissionUtil.groupSubmissionsByUniqueField(entry.getValue(), "name");
			Map<Object, List<Submission>> mapDb = SubmissionUtil.groupSubmissionsByField(mapDbSubmission.get(entry.getKey()), "name");
			for(Map.Entry<Object, Submission> e : mapCurrent.entrySet()) {
				List<Submission> dbSubs = mapDb.get(e.getKey());
				if (dbSubs == null || dbSubs.size() == 0) {
					listWriteCommissions.add(e.getValue());
				} else if (dbSubs.size() == 1) {
					Object status = SubmissionUtil.getFieldValue(dbSubs.get(0), "status");
					if (CommissionPolicy.COMMISSION_TRAN_STATUS_TEMPORARY.equals(status)) {
						Submission updateCommission = CommissionPolicy.mergeCommission(dbSubs.get(0), e.getValue());
						listWriteCommissions.add(updateCommission);
					} else {
						listDeleteCommissions.add(dbSubs.get(0));
					}
				} else if (dbSubs.size() > 1) {
					listWriteCommissions.add(e.getValue());
					listDeleteCommissions.addAll(dbSubs);
				}
			}
			for (Map.Entry<Object, List<Submission>> e: mapDb.entrySet()) {
				if (!mapCurrent.containsKey(e.getKey())) {
					listDeleteCommissions.addAll(e.getValue());
				}
			}
		}
		for (Object contract : deletedContracts) {
			listDeleteCommissions.addAll(mapDbSubmission.get(contract));
		}
		List<Submission> listStoredCommissions = submissionUtil.storeSubmissions(formCommissionTran, listWriteCommissions);
		if (listDeleteCommissions.size() > 0) {
			submissionUtil.deleteSubmissions(formCommissionTran, listDeleteCommissions);
		}
		return listStoredCommissions;
	}
	/*
	 * listCommissions - Danh sach tra hoa hong cho tung nguoi thu huong
	 * dsNoptien - Danh sach cac lan nop tien theo tung hop dong
	 * Can cu theo policy sinh ra du so lan chi tra hoa hong cho tvv.
	 * Trang thai waiting, khi khach hang nop tien se chuyen trang thai pending (san sang chi tra)
	 * Sau khi chi tra se chuyen trang thai paid
	 */
//	private List<Submission> generateCommissionTrans(FormioForm formCommisionTran,  List<Submission> listCommissions, Map<String, Submission> mapContractById, List<Submission> dsNoptien) {
//		List<Submission> result = new ArrayList<Submission>();
//		Map<Object, List<Submission>> mapByContract = SubmissionUtil.groupSubmissionsByField(dsNoptien, CommissionPolicy.COMMISSION_CONTRACT);
//		Map<Object, List<Submission>> mapCommissionBeneficiary = SubmissionUtil.groupSubmissionsByField(listCommissions, CommissionPolicy.COMMISSION_BENEFICIARY);
//		for(Map.Entry<Object, List<Submission>> entry : mapByContract.entrySet()) {
//			String contractId = String.valueOf(entry.getKey());
//			Submission contract = mapContractById.get(contractId);
//			Object beneficiaryId = SubmissionUtil.getFieldValue(contract, CommissionPolicy.COMMISSION_TVV); 
//			List<Submission> commissions = mapCommissionBeneficiary.get(beneficiaryId);
//			
//		}
//		for(Submission commission : listCommissions) {
//			Map<String, String> commissionRef = SubmissionUtil.createReferenceMap(commission);
//			
//		}
//		return result;
//	}
	
}
