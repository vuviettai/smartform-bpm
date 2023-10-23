package com.smartform.domain;

import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@RegisterForReflection
@Entity(name="cosmetics")
public class Cosmetic extends EntityBase {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
	private String name;
	private String code;
	//Hang san xuat
	private String producer;
	//Nuoc san xuat
	private String origin;	
    private String description;
    
}
