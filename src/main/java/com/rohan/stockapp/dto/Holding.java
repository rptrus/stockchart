package com.rohan.stockapp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Holding {
	
	public String holding;
		
	LocalDateTime dateAcquired;
	
	LocalDateTime dateDisposed;
	
	String code;
		
	BigDecimal price;

}
