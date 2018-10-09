package com.rohan.stockapp.service;

import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;

public class Utils {
	
	public static String md5Hash(String text2Cypher) {
	    String hash = "35454B055CC325EA1AF2126E27707052";	   
	 
	    return DigestUtils
	      .md5Hex(text2Cypher).toUpperCase();
	         
	}

}
