package com.smartform.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.ws.rs.core.SecurityContext;

@MappedSuperclass
public abstract class EntityBase extends PanacheEntityBase {
	/*
	 * 0 - Working
	 * 1 - Deleted
	 */
	@Column(name = "deleted")
	private int deleted;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "updated_date")
	private Date updatedDate;

	@Column(name = "created_timestamp")
	private Long createdTimestamp;

	@Column(name = "updated_timestamp")
	private Long updatedTimestamp;

	@Column(name = "created_user")
	private String createdUser;

	@Column(name = "updated_user")
	private String updatedUser;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "tenant_id")
	@JsonBackReference
	private Tenant tenant;

	public void setDefault(SecurityContext securityContext) {
		this.createdDate = new Date();
		this.updatedDate = this.createdDate;
		this.createdTimestamp = this.createdDate.getTime();
		this.updatedTimestamp = this.updatedDate.getTime();
		if (securityContext != null && securityContext.getUserPrincipal() != null) {
			if (this.createdUser == null) {
				this.createdUser = securityContext.getUserPrincipal().getName();
			}
			this.updatedUser = securityContext.getUserPrincipal().getName();
		}
	}
}
