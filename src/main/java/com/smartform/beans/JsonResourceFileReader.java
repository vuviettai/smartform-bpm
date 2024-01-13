package com.smartform.beans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class JsonResourceFileReader {
	private final ObjectMapper mapper;

    @Inject
    public JsonResourceFileReader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> List<T> readFile(File file, Class<T> elementClass) throws IOException {
//        CollectionType listType =
//                mapper.getTypeFactory().constructCollectionType(ArrayList.class, elementClass);
//        JavaType setType = mapper.getTypeFactory().constructCollectionType(Set.class, elementClass);
//        JavaType stringType = mapper.getTypeFactory().constructType(String.class);
//        CollectionType mapType =
//                mapper.getTypeFactory().constructMapType(Map.class, stringType, elementClass);
//        return mapper.readValue(file, listType);
    	return null;
    }
}
