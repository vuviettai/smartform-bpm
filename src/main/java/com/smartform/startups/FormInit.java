package com.smartform.startups;

import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.smartform.rest.client.FormioClient;
import com.smartform.rest.client.FormsflowService;

import io.quarkus.runtime.Startup;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FormInit {
	@RestClient
	@Inject
	FormioClient formioService;
	@RestClient
	@Inject
	FormsflowService formsflowService;
	@Startup
	void init() { 
		
	}
	public List<String> getProfiles() {
	     return ConfigProvider.getConfig().unwrap(SmallRyeConfig.class).getProfiles();
	}
}
