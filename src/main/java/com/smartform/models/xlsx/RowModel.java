package com.smartform.models.xlsx;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.Units;

import lombok.Data;

@Data
public class RowModel {
	private float hpt;
	private float hpx;
	private int level;
	public RowModel(Row row) {
		hpt = row.getHeightInPoints();
		hpx = Units.pointsToPixel(hpt);
		level = row.getOutlineLevel();
	}
}
