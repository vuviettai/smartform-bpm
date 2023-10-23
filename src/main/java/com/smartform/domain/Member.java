package com.smartform.domain;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
/*
 * Quản lý khách hàng thành viên, sử dụng dịch vụ theo gói
 */
@Data
@RegisterForReflection
@Entity(name="members")
public class Member extends EntityBase {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="customer_id")
	@JsonBackReference
	private Customer customer;
	@Column(name = "code")
	private String code;
	@Column(name = "note")
    private String note;
	@Column(name = "status")
    private String status; 
    
}
