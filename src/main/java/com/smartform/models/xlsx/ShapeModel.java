package com.smartform.models.xlsx;

import java.awt.Dimension;
import java.util.Base64;

import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFShape;

import lombok.Data;

@Data
public class ShapeModel {
	private String name;
	private int type;
	private String data;
	private double w;
	private double h;
	private Coordinate s;
	private Coordinate e;
	public ShapeModel(XSSFShape shape) {
		parseShape(shape);
	}
	public void parseShape(XSSFShape shape) {
		name = shape.getShapeName();
		XSSFDrawing drawing = shape.getDrawing();
		XSSFPicture picture =null;
		if (shape instanceof XSSFPicture) {
			picture = (XSSFPicture) shape;
		}
		if (picture != null) {
			Dimension dim = picture.getImageDimension();
			this.w = dim.getWidth();
			this.h = dim.getHeight();
			data = Base64.getEncoder().encodeToString(picture.getPictureData().getData());
			XSSFClientAnchor clientAnchor = picture.getClientAnchor();
			this.s = new Coordinate(clientAnchor.getRow1(), clientAnchor.getCol1());
			this.e = new Coordinate(clientAnchor.getRow2(), clientAnchor.getCol2());
		}
	}
}
