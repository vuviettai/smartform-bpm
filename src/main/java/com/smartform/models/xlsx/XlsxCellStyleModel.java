package com.smartform.models.xlsx;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.Data;

@Data
public class XlsxCellStyleModel {
	private int fontInd;
	private String valign;
	private String halign;
	private String bcolor;
	private String fcolor;
	public void parseStyle(CellStyle cellStyle, Workbook workbook, XlsxWorkboolModel workbookModel) {
		fontInd = cellStyle.getFontIndex();
		Font font = workbook.getFontAt(fontInd);
		this.setHorizontalAligment(cellStyle.getAlignment());
		this.setVerticalAligment(cellStyle.getVerticalAlignment());
//		bcolor = cellStyle.getFillBackgroundColorColor().toString();
//		fcolor = cellStyle.getFillForegroundColorColor().toString();
		workbookModel.addFont(fontInd, font);
	}
	public void setHorizontalAligment(HorizontalAlignment align) {
		switch (align) {
		case CENTER:
			this.halign = "Center";
			break;
		case CENTER_SELECTION:
			//this.halign = "center_selection";
			this.halign = "Center";
			break;
		case DISTRIBUTED:
			this.halign = "Distributed";
			break;
		case FILL:
			this.halign = "Fill";
			break;
		case GENERAL:
			this.halign = "General";
			break;
		case JUSTIFY:
			this.halign = "Justify";
			break;
		case LEFT:
			this.halign = "Left";
			break;
		case RIGHT:
			this.halign = "Right";
			break;
		default:
			break;
		
		}
	}
	public void setVerticalAligment(VerticalAlignment align) {
		switch (align) {
		case CENTER:
			this.valign = "Middle";
			break;
		case BOTTOM:
			this.valign = "Bottom";
			break;
		case DISTRIBUTED:
			this.valign = "Distributed";
			break;
		case JUSTIFY:
			this.valign = "Justify";
			break;
		case TOP:
			this.valign = "Top";
			break;
		default:
			break;
		
		}
	}
}
