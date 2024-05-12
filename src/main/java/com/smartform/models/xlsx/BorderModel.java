package com.smartform.models.xlsx;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class BorderModel extends HashMap<String, Object>{
	private static final long serialVersionUID = -2947147251023856587L;
	public BorderModel(Cell cell) {
		this.put("col", cell.getColumnIndex());
		this.put("row", cell.getRowIndex());
	}
	private Map<String, Object> createOption(int width, String color) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("width", width);
		if (color != null) {
			map.put("color", color);
		}
		return map;
	}
	public void setTop(int width, String color) {
		Map<String, Object> map = createOption(width, color);
		this.put("top", map);
	}
	public void setBottom(int width, String color) {
		Map<String, Object> map = createOption(width, color);
		this.put("bottom", map);
	}
}
