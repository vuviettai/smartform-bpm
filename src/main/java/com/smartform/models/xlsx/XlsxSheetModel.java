package com.smartform.models.xlsx;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFAnchor;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFShape;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class XlsxSheetModel extends HashMap<String, Object>{
	private static final long serialVersionUID = -1331590840147824426L;
	private List<MergeModel> merges = new ArrayList<MergeModel>();
	private List<ColModel> cols = new ArrayList<ColModel>();
	private List<ShapeModel> shapes = new ArrayList<ShapeModel>();
	private int firstRow = 0;
	private CellAddress firstCell;
	private CellAddress lastCell;
	
	public XlsxSheetModel() {
		this.put("!cols", cols);
		this.put("!merges", merges);
		this.put("!shapes", shapes);
	}
	public void parse(Sheet sheet, XlsxWorkboolModel workbookModel) {
		firstRow = sheet.getFirstRowNum();
		firstCell = new CellAddress(firstRow, Short.MAX_VALUE);
		lastCell = new CellAddress(sheet.getLastRowNum(), 0);
		for (int i = firstRow; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			short minColInd = row.getFirstCellNum();
			short maxColInd = row.getLastCellNum();
			if (lastCell.getColumn() < maxColInd) {
				lastCell = new CellAddress(lastCell.getRow(), maxColInd);
			}
			if (firstCell.getColumn() > minColInd) {
				firstCell = new CellAddress(firstCell.getRow(), minColInd);
			}
			parseRow(row, sheet.getWorkbook(), workbookModel);
		}
		for(CellRangeAddress rangeAddr : sheet.getMergedRegions()) {
			merges.add(new MergeModel(rangeAddr));
		}
		for(int col = firstCell.getColumn(); col <= lastCell.getColumn(); col++) {
			ColModel colModel = new ColModel();
			//CellStyle colStyle = sheet.getColumnStyle(col);
			int widthUnits = sheet.getColumnWidth(col); 
			colModel.setWch(widthUnits/256);
			float widthPx = poiWidthToPixels(widthUnits);
			//float colWidthPx = sheet.getColumnWidthInPixels(col); 
			colModel.setWpx(widthPx);
			cols.add(colModel);
		}
		this.put("!ref", firstCell.toString() + ":" + lastCell.toString());
		XSSFDrawing drawing = (XSSFDrawing) sheet.getDrawingPatriarch();
		List<XSSFShape> listShapes = drawing.getShapes();
		for(XSSFShape shape : listShapes) {
//			ShapeModel shapeModel = new ShapeModel(shape);
//			shapes.add(shapeModel);
			XSSFAnchor anchor = shape.getAnchor();
			if (anchor instanceof XSSFClientAnchor) {
				XlsxCellModel shapeModel = new XlsxCellModel();
				XSSFClientAnchor clientAnchor = (XSSFClientAnchor)anchor;
				shapeModel.parse(shape, clientAnchor);
				CellAddress address = new CellAddress(clientAnchor.getRow1(), clientAnchor.getCol1());
				this.put(address.toString(), shapeModel);
				
			}
			
		}
	
	}
	public static int poiWidthToPixels(final double widthUnits) {
        if (widthUnits <= 256) {
            return (int) Math.round((widthUnits / 28));
        } else {
            return (int) (Math.round(widthUnits * 8.5 / 256));
        }
}
	private void parseRow(Row row, Workbook workbook, XlsxWorkboolModel workbookModel) {
		int lastCell = row.getLastCellNum();
		for (int col = 0; col < lastCell; col++) {
			Cell cell = row.getCell(col);
			parseCell(cell, workbook,  workbookModel);
		}
	}
	private void parseCell(Cell cell, Workbook workbook, XlsxWorkboolModel workbookModel) {
		if (cell != null) {
			XlsxCellModel cellModel = new XlsxCellModel();
			cellModel.parse(cell, workbook, workbookModel);
			this.put(cell.getAddress().toString(), cellModel);
		}
	}
}

