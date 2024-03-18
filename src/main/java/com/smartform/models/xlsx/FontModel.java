package com.smartform.models.xlsx;

import org.apache.poi.ss.usermodel.Font;

import lombok.Data;

@Data
public class FontModel {
	private boolean italic;
	private boolean bold;
	private short height;
	private boolean strikeout;
	private byte underline;
	private String name;
	public FontModel(Font font) {
		bold = font.getBold();
		italic = font.getItalic();
		height = font.getFontHeight();
		name = font.getFontName();
		underline = font.getUnderline();
		strikeout = font.getStrikeout();
	}
}
