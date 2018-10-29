package com.rohan.stockapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.codec.digest.DigestUtils;

public class Utils {
	
	public static String md5Hash(String text2Cypher) {
	    String hash = "35454B055CC325EA1AF2126E27707052";	   
	 
	    return DigestUtils
	      .md5Hex(text2Cypher).toUpperCase();
	         
	}
	
	public static BigDecimal rectify(BigDecimal input, int scale) {
		return input.setScale(scale, RoundingMode.HALF_EVEN);
	}
	
	public BigDecimal toBigdecimal(String input) {		
		return new BigDecimal(input.toString());
	}

}
