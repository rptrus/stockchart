package com.rohan.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.rohan.stockapp.service.Utils;

public abstract class BaseReportElement {
	
	//Code	Purchase date	Quote date	Now price	Movement (price)	Movement (percentage)
	
	BaseReportElement() {
		System.err.println();
	}
	
	
	
	public BaseReportElement(LocalDate dateAcquired, String code, BigDecimal acquiredPrice, BigDecimal nowPrice, Integer numberOfUnits) {
		super();
		this.dateAcquired = dateAcquired;
		this.code = code;
		this.acquiredPrice = acquiredPrice != null ? Utils.rectify(acquiredPrice,2) : acquiredPrice;
		this.currentPrice = currentPrice!=null ? Utils.rectify(nowPrice,2) : nowPrice;
		this.numberOfUnits = numberOfUnits;
	}



	LocalDate dateAcquired;	
	
	String code;
	
	BigDecimal acquiredPrice;
	
	BigDecimal currentPrice;
	
	Integer numberOfUnits;
	
	//BigDecimal nowPrice;
	
	// movementPrice
	
	// movementPercentage
	
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}



	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}



	public BigDecimal getAcquiredPrice() {
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

	public Integer getNumberOfUnits() {
		return numberOfUnits;
	}

	public void setNumberOfUnits(Integer numberOfUnits) {
		this.numberOfUnits = numberOfUnits;
	}

	

}
