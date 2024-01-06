package com.smartform.customize.models;

import com.smartform.models.Tree;
import com.smartform.models.TreeElement;

public class GeoInfo {
	private String province;
	private String provinceCode;
	private String district;
	private String districtCode;
	private String ward;
	private String wardCode;
	
	public Tree createTree() {
		TreeElement provinceElm = new TreeElement(province, provinceCode, "province");
		TreeElement districtElm = new TreeElement(district, districtCode, "district");
		TreeElement wardElm = new TreeElement(ward, wardCode, "ward");
		Tree wardTree = new Tree(wardElm);
		Tree districtTree = new Tree(districtElm, wardTree);
		Tree provinceTree = new Tree(provinceElm, districtTree);
		return provinceTree;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getDistrictCode() {
		return districtCode;
	}
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}
	public String getWard() {
		return ward;
	}
	public void setWard(String ward) {
		this.ward = ward;
	}
	public String getWardCode() {
		return wardCode;
	}
	public void setWardCode(String wardCode) {
		this.wardCode = wardCode;
	}

	
	
}
