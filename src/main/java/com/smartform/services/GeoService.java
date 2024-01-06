package com.smartform.services;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.smartform.customize.models.GeoInfo;
import com.smartform.models.Tree;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GeoService {
	public List<GeoInfo> readGeoInfo(String path, int sheetInd) throws Exception {
		FileInputStream file = new FileInputStream(new File(path));
		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheetAt(sheetInd);
		workbook.close();
		List<GeoInfo> listGeoInfos = new ArrayList<GeoInfo>();
		for (int i = 1; i < sheet.getLastRowNum(); i++) {
			Row row = sheet.getRow(i);
			GeoInfo geoInfo = new GeoInfo();
			listGeoInfos.add(geoInfo);
			Cell cell = null;
			int lastCell = row.getLastCellNum();
			if (lastCell >= 1) {
				cell = row.getCell(0);
				geoInfo.setProvince(cell.getStringCellValue());
			}
			if (lastCell >= 2) {
				cell = row.getCell(1);
				geoInfo.setProvinceCode(cell.getStringCellValue());
			}
			if (lastCell >= 3) {
				cell = row.getCell(2);
				geoInfo.setDistrict(cell.getStringCellValue());
			}
			if (lastCell >= 4) {
				cell = row.getCell(3);
				geoInfo.setDistrictCode(cell.getStringCellValue());
			}
			if (lastCell >= 5) {
				cell = row.getCell(4);
				geoInfo.setWard(cell.getStringCellValue());
			}
			if (lastCell >= 6) {
				cell = row.getCell(5);
				geoInfo.setWardCode(cell.getStringCellValue());
			}
		}
		return listGeoInfos;
	}
	public List<Tree> createTrees(List<GeoInfo> geoInfos) {
		List<Tree> result = new ArrayList<Tree>();
		Map<String, Tree> mapProvinces = new HashMap<String, Tree>();
		List<Tree> listRawInfo = geoInfos.stream().map(new Function<GeoInfo, Tree>() {

			@Override
			public Tree apply(GeoInfo t) {
				// TODO Auto-generated method stub
				return t.createTree();
			}
		}).collect(Collectors.toList());
		for(Tree province : listRawInfo) {
			Tree storedProvince = mapProvinces.get(province.getNode().getName());
			if (storedProvince == null) {
				mapProvinces.put(province.getNode().getName(), province);
				result.add(province);
			} else {
				storedProvince.merge(province);
			}
		}
		return result;
	}
}
