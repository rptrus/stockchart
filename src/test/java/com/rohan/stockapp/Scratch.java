package com.rohan.stockapp;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Scratch {

	public static void main(String[] args) {
		BigDecimal acquired= new BigDecimal(40.10);
		BigDecimal nowPrice= new BigDecimal(46.10);
		BigDecimal increase = (nowPrice.divide(acquired,2,RoundingMode.HALF_EVEN)
				.subtract(BigDecimal.ONE))
				.multiply(BigDecimal.TEN.pow(2)).setScale(0);
		System.out.println(increase);
	}
	
}
