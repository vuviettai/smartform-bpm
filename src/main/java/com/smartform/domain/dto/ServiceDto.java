package com.smartform.domain.dto;

import java.util.UUID;

import lombok.Data;
@Data
public class ServiceDto extends AbstractBaseDto {
    private UUID id;
	private String name;
	private String code;
    private String description;
}
