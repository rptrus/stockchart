package com.rohan.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.rohan.stockapp.entity.Holding;

// takes data from entities, Rest Results, computations, a bit of this and that etc

public class StockReportElement extends BaseReportElement {
	
	public StockReportElement() {
		super();
	}
	
	public StockReportElement(LocalDate dateAcquired, String code, BigDecimal acquiredPrice, BigDecimal nowPrice, Integer numberOfUnits) {
		super(dateAcquired, code, acquiredPrice, nowPrice, numberOfUnits);
	}

	// set to true if the stock has already been sold
	// assuming whole stock is disposed
	public boolean disposed() {
		return false;
	}
	
	public boolean active() {
		return true;
	}

	
// nothing more
	
}
