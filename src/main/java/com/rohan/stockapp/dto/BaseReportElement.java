package com.rohan.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class BaseReportElement {
	
	//Code	Purchase date	Quote date	Now price	Movement (price)	Movement (percentage)
	
	BaseReportElement() {
		System.err.println();
	}
	
	
	
	public BaseReportElement(LocalDate dateAcquired, String code, BigDecimal price) {
		super();
		this.dateAcquired = dateAcquired;
		this.code = code;
		this.price = price;
	}



	LocalDate dateAcquired;	
	
	String code;
	
	BigDecimal price;
	
	// movementPrice
	
	// movementPercentage
	
	public BigDecimal getPrice() {
		return price;
	}



	public void setPrice(BigDecimal price) {
		this.price = price;
	}



	public LocalDate getDateAcquired() {
		return dateAcquired;
	}

	public void setDateAcquired(LocalDate dateAcquired) {
		this.dateAcquired = dateAcquired;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
