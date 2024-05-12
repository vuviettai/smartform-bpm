package com.smartform.models.xlsx;

import org.apache.poi.ss.util.CellRangeAddress;

import lombok.Data;

@Data
public class MergeModel {
	private Coordinate s;
	private Coordinate e;
	public MergeModel(CellRangeAddress rangeAddr) {
		s = new Coordinate(rangeAddr.getFirstRow(), rangeAddr.getFirstColumn());
		e = new Coordinate(rangeAddr.getLastRow(), rangeAddr.getLastColumn());
	}
}
