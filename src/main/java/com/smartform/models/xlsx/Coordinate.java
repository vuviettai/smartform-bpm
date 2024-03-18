package com.smartform.models.xlsx;

import lombok.Data;

@Data
public class Coordinate {
	private int r;
	private int c;
	public Coordinate(int r, int c) {
		this.r = r;
		this.c = c;
	}
}
