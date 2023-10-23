package com.smartform.domain;

import java.util.UUID;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
/*
 * Cơ sở
 */
@Data
@RegisterForReflection
@Entity(name="branches")
public class Branch extends EntityBase {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
	@Column(name="name")
	private String name;
	@Column(name="code")
	private String code;
	@Column(name="address")
	private String address;
	@Column(name="contact_phone")
	private String contactPhone;
	@Column(name="description")
    private String description;
    
}
