package com.smartform.models.xlsx;

import java.util.Base64;

import org.apache.poi.ss.usermodel.PictureData;

import lombok.Data;

@Data
public class PictureModel {
	private String mineType;
	private int type;
	private String data;
	public PictureModel(PictureData picData) {
		mineType = picData.getMimeType();
		type = picData.getPictureType();
		data = Base64.getEncoder().encodeToString(picData.getData());
	}
}
