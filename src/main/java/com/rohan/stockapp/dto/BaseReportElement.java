package com.rohan.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class BaseReportElement {
	
	//Code	Purchase date	Quote date	Now price	Movement (price)	Movement (percentage)
	
	BaseReportElement() {
		System.err.println();
	}
	
	LocalDate dateAcquired;	
	
	String code;
	
	BigDecimal price;
	
	// movementPrice
	
	// movementPercentage
	
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
