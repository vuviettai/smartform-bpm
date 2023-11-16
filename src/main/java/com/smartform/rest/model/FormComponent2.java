package com.smartform.rest.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class FormComponent2 {
	private String label;
	private String labelPosition;
	private String placeholder;
	private String description;
	private String tooltip;
	private String prefix;
	private String suffix;
	private Map<String, Object> widget;
	private String inputMask;
	private String displayMask;
	private String applyMaskOn;
	private Boolean allowMultipleMasks;
	private String customClass;
	private String tabindex;
	private String autocomplete;
	private Boolean hidden;
	private Boolean hideLabel;
	private Boolean showWordCount;
	private Boolean showCharCount;
	private Boolean mask;
	private Boolean autofocus;
	private Boolean spellcheck;
	private Boolean disabled;
	private Boolean tableView;
	private Boolean modalEdit;
	private Boolean multiple;
	private Boolean persistent;
	private List<String> addons;
}


//{
//
//	  "inputFormat": "plain",
//	  "protected": false,
//	  "dbIndex": false,
//	  "case": "",
//	  "truncateMultipleSpaces": false,
//	  "encrypted": false,
//	  "redrawOn": "",
//	  "clearOnHide": true,
//	  "customDefaultValue": "",
//	  "calculateValue": "",
//	  "calculateServer": false,
//	  "allowCalculateOverride": false,
//	  "validateOn": "change",
//	  "validate": {
//	    "required": false,
//	    "pattern": "",
//	    "customMessage": "",
//	    "custom": "",
//	    "customPrivate": false,
//	    "json": "",
//	    "minLength": "",
//	    "maxLength": "",
//	    "strictDateValidation": false,
//	    "multiple": false,
//	    "unique": false
//	  },
//	  "unique": false,
//	  "errorLabel": "",
//	  "errors": "",
//	  "key": "textField",
//	  "tags": [],
//	  "properties": {},
//	  "conditional": {
//	    "show": null,
//	    "when": null,
//	    "eq": "",
//	    "json": ""
//	  },
//	  "customConditional": "",
//	  "logic": [],
//	  "attributes": {},
//	  "overlay": {
//	    "style": "",
//	    "page": "",
//	    "left": "",
//	    "top": "",
//	    "width": "",
//	    "height": ""
//	  },
//	  "type": "textfield",
//	  "input": true,
//	  "refreshOn": "",
//	  "dataGridLabel": false,
//	  "addons": [],
//	  "inputType": "text",
//	  "id": "em1vio",
//	  "defaultValue": ""
//	}