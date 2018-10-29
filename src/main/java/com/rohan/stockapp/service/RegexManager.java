package com.rohan.stockapp.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

// This may need to change if Yahoo changes their page data!

@Component
public class RegexManager {
	
	public String getLastPrice(String page, String code) {
		
		// we need to see this in here
		Pattern pattern = Pattern.compile(".*?"+code+"\\.AX.*?regularMarketPrice.*?fmt\":\"(\\d+\\.\\d+)\"}.*",Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher matcher = pattern.matcher(page);
		
		boolean matches = matcher.matches();
		
		if (matches) {			
			//System.out.println(matcher.group(1));
			return matcher.group(1);
			
		}
		return "nil"; // more descriptive than null		
	}
	
	public String getIndustry(String page) {
		
		// we need to see this in here
		Pattern pattern = Pattern.compile(".*?\"industry\":\"(.*)\"[,]?.*recommendationTrend\".*?",Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher matcher = pattern.matcher(page);
		
		boolean matches = matcher.matches();
		
		if (matches) {
			//System.out.println(StringUtils.substringBefore(matcher.group(1), "\""));
			return matcher.group(1);
			
		}
		return "nil"; // more descriptive than null		
	}


}
