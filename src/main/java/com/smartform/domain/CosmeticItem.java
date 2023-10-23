package com.smartform.domain;

import java.util.Date;
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
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@RegisterForReflection
@Entity(name="cosmetic_items")
public class CosmeticItem extends EntityBase {
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
	
	@Column(name="lot_number")
	private String lot_number;
	@Column(name="produced_date")
	private Date producedDate;
	@Column(name="expired_date")
	private Date expiredDate;
	@Column(name="quantity")
	private Float quantity;
	@Column(name="description")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name="room_id")
	@JsonBackReference
	private Cosmetic cosmetic;
}
