package com.smartform.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.mapstruct.ap.internal.util.Strings;

import com.smartform.domain.EntityBase;
import com.smartform.repository.RepositoryBase;

public class ClassUtils {
	public static final String ENTITIES_PACKAGE = EntityBase.class.getPackageName();
	public static final String REPOSITORY_PACKAGE = RepositoryBase.class.getPackageName();
	public static final String SUFFIX_REPOSITORY = "repository";
	private static Map<String, Class<?>> entityClasses = null;
	private static Map<String, Class<?>> repositoryClasses = null;
	private static Map<String, Class<?>> getEntityClasses() {
    	if (entityClasses == null || entityClasses.isEmpty()) {
    		InputStream stream = EntityBase.class.getClassLoader().getResourceAsStream(ENTITIES_PACKAGE.replaceAll("[.]", "/"));
//    		InputStream stream = ClassLoader.getSystemClassLoader()
//    		          .getResourceAsStream(ENTITIES_PACKAGE.replaceAll("[.]", "/"));
	        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	        entityClasses = new HashMap<String, Class<?>>();
	        reader.lines()
	          .filter(line -> line.endsWith(".class"))
	          .forEach(line -> {
	        	  String name = line.substring(0, line.lastIndexOf('.'));
	        	  try {
					entityClasses.put(name.toLowerCase(), Class.forName(ENTITIES_PACKAGE + "." + name));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
	          });
    	}
    	return entityClasses;
    }
	private static Map<String, Class<?>> getRepositoryClasses() {
    	if (repositoryClasses == null || repositoryClasses.isEmpty()) {
    		InputStream stream = EntityBase.class.getClassLoader().getResourceAsStream(REPOSITORY_PACKAGE.replaceAll("[.]", "/"));
//    		InputStream stream = ClassLoader.getSystemClassLoader()
//    		          .getResourceAsStream(ENTITIES_PACKAGE.replaceAll("[.]", "/"));
	        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	        repositoryClasses = new HashMap<String, Class<?>>();
	        reader.lines()
	          .filter(line -> line.endsWith(".class"))
	          .forEach(line -> {
	        	  String name = line.substring(0, line.lastIndexOf('.'));
	        	  try {
					repositoryClasses.put(name.toLowerCase(), Class.forName(REPOSITORY_PACKAGE + "." + name));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
	          });
    	}
    	return repositoryClasses;
    }
    
	public static Class<?> getEntityClass(String entityName) {
		Map<String, Class<?>> mapClasses = getEntityClasses();
    	return mapClasses.get(entityName);
	}
	public static Class<?> getRepositoryyClass(String repositoryName) {
		if (Strings.isEmpty(repositoryName)) return null;
		Map<String, Class<?>> mapClasses = getRepositoryClasses();
		String name = repositoryName + SUFFIX_REPOSITORY;
    	return mapClasses.get(name);
	}
}
