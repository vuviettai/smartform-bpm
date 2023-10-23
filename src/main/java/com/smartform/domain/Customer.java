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
@Entity(name="customers")
public class Customer extends EntityBase {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "code")
	private String code;
	@Column(name = "gender")
	private String gender;
	@Column(name = "age")
	private int age;
	@Column(name = "birthday")
	private int birthday;
	@Column(name = "personal_id")
	private String personalId;
	@Column(name = "phone_number")
	private String phoneNumber;
	@Column(name = "address")
	private String address;
	@Column(name = "note")
    private String note;
	@Column(name = "status")
    private String status; 
    
}
