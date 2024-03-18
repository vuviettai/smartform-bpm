package com.smartform.models.xlsx;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.Data;

@Data
public class XlsxCellStyleModel {
	private int fontInd;
	public void parseStyle(CellStyle cellStyle, Workbook workbook, XlsxWorkboolModel workbookModel) {
		fontInd = cellStyle.getFontIndex();
		Font font = workbook.getFontAt(fontInd);
		workbookModel.addFont(fontInd, font);
	}
}
