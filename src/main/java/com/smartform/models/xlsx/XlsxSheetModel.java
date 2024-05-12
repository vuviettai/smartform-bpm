package com.smartform.models.xlsx;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFAnchor;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFShape;

import com.smartform.utils.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class XlsxSheetModel extends HashMap<String, Object>{
	private static final long serialVersionUID = -1331590840147824426L;
	private List<MergeModel> merges = new ArrayList<MergeModel>();
	private List<ColModel> cols = new ArrayList<ColModel>();
	private List<RowModel> rows = new ArrayList<RowModel>();
	private List<ShapeModel> shapes = new ArrayList<ShapeModel>();
	private List<BorderModel> borders = new ArrayList<BorderModel>();
	private int firstRow = 0;
	private CellAddress firstCell;
	private CellAddress lastCell;
	
	public XlsxSheetModel() {
		this.put("!cols", cols);
		this.put("!merges", merges);
		this.put("!shapes", shapes);
		this.put("!rows", rows);
		this.put("!borders", borders);
	}
	public void parse(Sheet sheet, XlsxWorkboolModel workbookModel, Object lastCellName) {
		firstRow = sheet.getFirstRowNum();
		int lastRow = sheet.getLastRowNum();
		//Always parser from the first column 
		firstCell = new CellAddress(firstRow, 0);
		CellAddress cfgLastCell =null;
		if (lastCellName instanceof String && !StringUtil.isEmpty((String)lastCellName)) {
			cfgLastCell = new CellAddress((String)lastCellName);
		}
		lastCell = cfgLastCell != null ? cfgLastCell : new CellAddress(lastRow, 0);
		for (int i = firstRow; i <= lastCell.getRow(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			short minColInd = row.getFirstCellNum();
			short maxColInd = row.getLastCellNum();
			if (lastCell.getColumn() < maxColInd && cfgLastCell == null) {
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
			ColModel colModel = new ColModel(col);
			CellStyle colStyle = sheet.getColumnStyle(col);
			int widthUnits = sheet.getColumnWidth(col); 
			colModel.setWch(widthUnits/256);
			//ShippingEms prefers poiWidthToPixels
			//Receipt review prefers getColumnWidthInPixels
			float widthPx = poiWidthToPixels(widthUnits);
			//float colWidthPx = sheet.getColumnWidthInPixels(col); 
			float colWidthPx = (float)(widthUnits/256.0*Units.DEFAULT_CHARACTER_WIDTH);
			colModel.setWpx(colWidthPx);
			colModel.setVerticalAligment(colStyle.getVerticalAlignment());
			colModel.setHorizontalAligment(colStyle.getAlignment());
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
		RowModel rowModel = new RowModel(row);
		this.rows.add(rowModel);
		for (int col = 0; col < lastCell; col++) {
			Cell cell = row.getCell(col);
			if (cell == null) continue;
			parseCell(cell, workbook,  workbookModel);
			parseCellBorder(cell);
		}
	}
	private void parseCell(Cell cell, Workbook workbook, XlsxWorkboolModel workbookModel) {
		XlsxCellModel cellModel = new XlsxCellModel();
		cellModel.parse(cell, workbook, workbookModel);
		this.put(cell.getAddress().toString(), cellModel);
	}
	private void parseCellBorder(Cell cell) {
		CellStyle cellStyle = cell.getCellStyle();
		BorderModel borderModel = null;
		switch (cellStyle.getBorderBottom()) {
		case DASHED:
			break;
		case DASH_DOT:
			break;
		case DASH_DOT_DOT:
			break;
		case DOTTED:
			break;
		case DOUBLE:
			break;
		case HAIR:
			break;
		case MEDIUM:
			break;
		case MEDIUM_DASHED:
			break;
		case MEDIUM_DASH_DOT:
			break;
		case MEDIUM_DASH_DOT_DOT:
			break;
		case NONE:
			if (borderModel == null) {
				borderModel = new BorderModel(cell);
			}
			borderModel.setBottom(0, "#fff");
			break;
		case SLANTED_DASH_DOT:
			break;
		case THICK:
			break;
		case THIN:
			break;
		default:
			break;
		
		}
		switch (cellStyle.getBorderTop()) {
		case DASHED:
			break;
		case DASH_DOT:
			break;
		case DASH_DOT_DOT:
			break;
		case DOTTED:
			break;
		case DOUBLE:
			break;
		case HAIR:
			break;
		case MEDIUM:
			break;
		case MEDIUM_DASHED:
			break;
		case MEDIUM_DASH_DOT:
			break;
		case MEDIUM_DASH_DOT_DOT:
			break;
		case NONE:
			if (borderModel == null) {
				borderModel = new BorderModel(cell);
			}
			borderModel.setTop(0, "#fff");
			break;
		case SLANTED_DASH_DOT:
			break;
		case THICK:
			break;
		case THIN:
			break;
		default:
			break;
		
		}
		if (borderModel != null) {
			this.borders.add(borderModel);
		}
	}
}

