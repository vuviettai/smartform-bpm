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
 * Cộng tác viên
 */
@Data
@RegisterForReflection
@Entity(name="colaborators")
public class Colaborator extends EntityBase {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	private String code;
	private String gender;
	private int age;
	@Column(name = "personal_id")
	private String personalId;
	@Column(name = "phone_number")
	private String phoneNumber;
	private String address;
    private String note;
    private String status; //Fired, Working
    
}
