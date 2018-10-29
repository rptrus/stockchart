package com.rohan.stockapp.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class StockPriceRetriever {
	
	@Autowired
	RegexManager regexManager;
	
	public StockPriceRetriever(RegexManager regexManager) {
		this.regexManager = regexManager;
	}
	
	@Async
	public Future<String> retrieveQuote(String code) {
		
		StopWatch stopwatch = new StopWatch();
		String aQuotePage = null;
		String quote = null;
		String industry = null;
		
		try {
		//https://finance.yahoo.com/quote/DDR.AX like this
			
		//URI uri = new URI("https://finance.yahoo.com/quote/DDR.AX");
		
		UriComponents uri = UriComponentsBuilder.newInstance()
			      .scheme("https").host("finance.yahoo.com").path("/quote/").path(code).path(".AX").build();
		
		//,"currency":"AUD","regularMarketPrice":{"raw":35.5,"fmt":"35.50"}
		
		//String code = "DDR";		
		
		stopwatch.start();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);
		//System.out.println(result.getBody());
		aQuotePage = result.getBody();
		quote = regexManager.getLastPrice(aQuotePage, code);
		stopwatch.stop();
		System.out.println("["+code+"] Seconds: "+stopwatch.getTotalTimeSeconds());
		//industry = regexManager.getIndustry(aQuotePage);
		
		Files.write(Paths.get("c:\\temp\\output"+code+".txt"), aQuotePage.getBytes());
		} catch (IOException e){
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new AsyncResult<String>(quote);

	}

}
