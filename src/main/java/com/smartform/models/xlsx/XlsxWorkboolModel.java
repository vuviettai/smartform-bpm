package com.smartform.models.xlsx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.Data;

@Data
public class XlsxWorkboolModel extends HashMap<String, Object>{
	private static final long serialVersionUID = -5885572425630050678L;
	private String type;
	private String storage;
	private List<String> sheetNames = new ArrayList<String>();
	private Map<String, XlsxSheetModel> sheets = new HashMap<String, XlsxSheetModel>();
	private List<PictureModel> pictures = new ArrayList<PictureModel>();
	private XlsxWorkbookStyles styles = new XlsxWorkbookStyles();
	public XlsxWorkboolModel() {
		this.put("SheetNames", sheetNames);
		this.put("Sheets", sheets);
		this.put("Styles", styles);
	}
	public void parse(Workbook workbook) {
		int sheetCount = workbook.getNumberOfSheets();
		for (int ind = 0; ind < sheetCount; ind++) {
			Sheet sheet = workbook.getSheetAt(ind);
			sheetNames.add(sheet.getSheetName());
			XlsxSheetModel sheetModel = new XlsxSheetModel();
			sheetModel.parse(sheet, this);
			sheets.put(sheet.getSheetName(), sheetModel);
		}	
		List<? extends PictureData> listPictures = workbook.getAllPictures();
		if (listPictures != null) {
			for (PictureData picData : listPictures) {
				pictures.add(new PictureModel(picData));
			}
		}
		
	}
	public void addFont(int fontInd, Font font) {
		styles.addFont(fontInd, font);
	}
}
