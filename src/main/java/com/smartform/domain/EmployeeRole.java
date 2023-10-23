package com.smartform.domain;

import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@RegisterForReflection
@Entity(name="employee-roles")
public class EmployeeRole extends EntityBase {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
	@Column(name = "role_name")
	private String roleName;
    private String description;
    
}
