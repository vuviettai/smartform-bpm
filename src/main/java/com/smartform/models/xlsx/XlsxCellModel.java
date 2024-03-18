package com.smartform.models.xlsx;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.Data;

@Data
public class XlsxCellModel {
	//Cell type
	private String t;
	//Cell style
	private XlsxCellStyleModel s;
	//String value
	private String v;
	//Formular
	private String f;
	private double n;
	private Date d;
	private Boolean b;
	public void parse(Cell cell, Workbook workbook, XlsxWorkboolModel workbookModel) {
		CellType cType = cell.getCellType();
		switch (cType) {
		case BLANK:
			break;
		case BOOLEAN:
			b = cell.getBooleanCellValue();
			break;
		case ERROR:
			break;
		case FORMULA:
			f = cell.getCellFormula();
			break;
		case NUMERIC:
			n = cell.getNumericCellValue();
			try {
				d = cell.getDateCellValue();
			} catch (Exception e) {}
			break;
		case STRING:
			v = cell.getStringCellValue();
			break;
		case _NONE:
			break;
		default:
			break;
		}
		s = new XlsxCellStyleModel();
		s.parseStyle(cell.getCellStyle(), workbook,  workbookModel);
		
		
	}
}
