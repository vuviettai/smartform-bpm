package com.smartform.models.xlsx;

import java.awt.Dimension;
import java.util.Base64;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;

import lombok.Data;

@Data
public class XlsxCellModel {
	// Cell type
	private String t;
	// Cell style
	private XlsxCellStyleModel s;
	// underlying value
	private String v;
	// formatted text (if applicable)
	private String w;
	// Formular
	private String f;
	/** Rich text encoding (if applicable) */
	private Object r;

	/** HTML rendering of the rich text (if applicable) */
	private String h;

	private double n;
	private Date d;
	private Boolean b;
	// private Dimension dim;
	private int[] loc; // [s.r, s.c, e.r. e.c]
	private double[] size; // [width, height]

	public void parse(XSSFShape shape, XSSFClientAnchor clientAnchor) {
		t = "z";
		XSSFPicture picture = null;
		if (shape instanceof XSSFPicture) {
			picture = (XSSFPicture) shape;
		}
		if (picture != null) {
			Dimension dim = picture.getImageDimension();
			size = new double[2];
			size[0] = dim.getWidth();
			size[1] = dim.getHeight();
			String data = Base64.getEncoder().encodeToString(picture.getPictureData().getData());
			r = "p";
			// h = String.format("<img style='display:block; width:%s;height:%s;'
			// src='data:image;base64,%s/>", width, height, data);
			h = data;
			loc = new int[4];
			loc[0] = clientAnchor.getRow1();
			loc[1] = clientAnchor.getCol1();
			loc[2] = clientAnchor.getRow2();
			loc[3] = clientAnchor.getCol2();
		}
	}

	public void parse(Cell cell, Workbook workbook, XlsxWorkboolModel workbookModel) {
		CellType cType = cell.getCellType();
		switch (cType) {
			case BLANK:
				t = "z";
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
				//Front does not support type 'f'
				t = "z";
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
		if (cell.getCellStyle() != null) {
			s = new XlsxCellStyleModel();
			s.parseStyle(cell.getCellStyle(), workbook, workbookModel);
		}
	}
}
