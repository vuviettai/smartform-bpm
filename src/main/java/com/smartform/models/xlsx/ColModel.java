package com.smartform.models.xlsx;


import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import lombok.Data;

@Data
public class ColModel {
	private int index;
	private int wch;
	private float wpx;
	private String valign;
	private String halign;
	public ColModel(int index) {
		this.index = index;
	}
	public void setHorizontalAligment(HorizontalAlignment align) {
		switch (align) {
		case CENTER:
			this.halign = "center";
			break;
		case CENTER_SELECTION:
			this.halign = "center_selection";
			break;
		case DISTRIBUTED:
			this.halign = "distributed";
			break;
		case FILL:
			this.halign = "fill";
			break;
		case GENERAL:
			this.halign = "general";
			break;
		case JUSTIFY:
			this.halign = "justify";
			break;
		case LEFT:
			this.halign = "left";
			break;
		case RIGHT:
			this.halign = "right";
			break;
		default:
			break;
		
		}
	}
	public void setVerticalAligment(VerticalAlignment align) {
		switch (align) {
		case CENTER:
			this.valign = "center";
			break;
		case BOTTOM:
			this.valign = "center";
			break;
		case DISTRIBUTED:
			this.valign = "distributed";
			break;
		case JUSTIFY:
			this.valign = "justify";
			break;
		case TOP:
			this.valign = "top";
			break;
		default:
			break;
		
		}
	}
}
