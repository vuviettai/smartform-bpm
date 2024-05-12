package com.smartform.models.xlsx;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Font;

import lombok.Data;

@Data
public class XlsxWorkbookStyles {
	private List<FontModel> Fonts = new ArrayList<FontModel>();
	public void addFont(int fontInd, Font font) {
		while(fontInd >= Fonts.size()) {
			Fonts.add(null);
		}
		Fonts.set(fontInd, new FontModel(font));
	}
}
