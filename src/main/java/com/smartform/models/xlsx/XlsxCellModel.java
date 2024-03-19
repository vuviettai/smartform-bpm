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
	//underlying value
	private String v;
	//formatted text (if applicable)
	private String w;
	//Formular
	private String f;
	private double n;
	private Date d;
	private Boolean b;
	public void parse(Cell cell, Workbook workbook, XlsxWorkboolModel workbookModel) {
		CellType cType = cell.getCellType();
		switch (cType) {
		case BLANK:
			t="z";
			break;
		case BOOLEAN:
			t = "b";
			b = cell.getBooleanCellValue();
			v = cell.getStringCellValue();
			w = v;
			break;
		case ERROR:
			t = "e";
			break;
		case FORMULA:
			t = "f";
			f = cell.getCellFormula();
			v = cell.getCellFormula();
			break;
		case NUMERIC:
			try {
				d = cell.getDateCellValue();
				t = "d";
			} catch (Exception e) {
				t = "n";
				n = cell.getNumericCellValue();
			}
			break;
		case STRING:
			t = "s";
			v = cell.getStringCellValue();
			w = cell.getStringCellValue();
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
