package com.rohan.stockapp.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CONTACT")
public class Holding {

	public Holding(Long id, LocalDateTime dateAcquired, LocalDateTime dateDisposed, String code, BigDecimal price) {
		super();
		this.id = id;
		this.dateAcquired = dateAcquired;
		this.dateDisposed = dateDisposed;
		this.code = code;
		this.price = price;
	}
	
	public Holding() {}

	@Id
	public Long id;
		
	LocalDateTime dateAcquired;
	
	LocalDateTime dateDisposed;
	
	String code;
		
	BigDecimal price;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getDateAcquired() {
		return dateAcquired;
	}

	public void setDateAcquired(LocalDateTime dateAcquired) {
		this.dateAcquired = dateAcquired;
	}

	public LocalDateTime getDateDisposed() {
		return dateDisposed;
	}

	public void setDateDisposed(LocalDateTime dateDisposed) {
		this.dateDisposed = dateDisposed;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}


}
