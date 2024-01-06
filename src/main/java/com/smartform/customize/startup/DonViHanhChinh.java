package com.smartform.customize.startup;

import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.smartform.customize.models.GeoInfo;
import com.smartform.customize.service.GeoService;
import com.smartform.customize.service.MongodbService;
import com.smartform.models.Tree;
import com.smartform.rest.client.FormioService;

import io.quarkus.runtime.Startup;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

//@ApplicationScoped
public class DonViHanhChinh {

	public static final String PATH_DONVIHANHCHINH = "src/main/resources/DonViHanhChinh_24_05_2022.xlsx";
	 
	@Inject
	GeoService geoService;
	@Inject
    MongodbService mongodbService;
	@RestClient
	FormioService formioService;
	@Startup
	void init() { 
		try {
			List<String> profiles = getProfiles();
			if (profiles.contains("fnt")) {
				List<GeoInfo> listGeoInfos = geoService.readGeoInfo(PATH_DONVIHANHCHINH, 0);
				List<Tree> listTrees = geoService.createTrees(listGeoInfos);
				//mongodbService.storeGeoInfo(listTrees);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public List<String> getProfiles() {
	     return ConfigProvider.getConfig().unwrap(SmallRyeConfig.class).getProfiles();
	}
	public void storeGeoSubmission(List<Tree> listTrees) {
		
	}
}
