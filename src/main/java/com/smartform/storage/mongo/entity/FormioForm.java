package com.smartform.storage.mongo.entity;

import java.util.List;

import com.smartform.rest.model.Access;
import com.smartform.rest.model.FormComponent;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@MongoEntity(collection="formio")
public class FormioForm extends BaseFormioEntity {

	private String title;
	private String name;
	private String path;
	private String type;
	private String display;
	private List<String> tags;
	private Boolean isBundle;
	private List<Access> submissionAccess;
	private List<FormComponent> components;
	private String machineName;
	private String parentFormId;
	
	// entity methods
    public static FormioForm findByName(String name) {
        return find("name", name).firstResult();
    }
}

