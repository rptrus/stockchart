package com.rohan.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.rohan.stockapp.service.Utils;

public abstract class BaseReportElement {
	
	//Code	Purchase date	Quote date	Now price	Movement (price)	Movement (percentage)
	
	BaseReportElement() {
		System.err.println();
	}
	
	
	
	public BaseReportElement(LocalDate dateAcquired, String code, BigDecimal acquiredPrice, BigDecimal nowPrice) {
		super();
		this.dateAcquired = dateAcquired;
		this.code = code;
		this.acquiredPrice = Utils.rectify(acquiredPrice,2);
		this.currentPrice = Utils.rectify(nowPrice,2);
	}



	LocalDate dateAcquired;	
	
	String code;
	
	BigDecimal acquiredPrice;
	
	BigDecimal currentPrice;
	
	//BigDecimal nowPrice;
	
	// movementPrice
	
	// movementPercentage
	
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}



	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}



	public BigDecimal geAcquiredPrice() {
		return acquiredPrice;
	}



	public void setAcquiredPrice(BigDecimal price) {
		this.acquiredPrice = price;
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
