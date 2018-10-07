package com.rohan.stockapp.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.rohan.stockapp.entity.Holding;
import com.rohan.stockapp.entity.Quote;
import com.rohan.stockapp.repository.HoldingRepository;

@Component
public class Processor {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	HoldingRepository holdingRepository;
	
	
	@PostConstruct
	public void testIt() throws URISyntaxException, IOException {
		
		
		List nudge = jdbcTemplate.queryForList("select * from holding");
		System.out.println(nudge);
		
		Holding holding = new Holding(2L, LocalDateTime.now(), LocalDateTime.now(), "VAS", new BigDecimal(1.23));
		Quote quote = new Quote(2L, "VAS", new BigDecimal(1.99));
		holding.setQuote(quote);
		holdingRepository.save(holding);				
		
		//https://finance.yahoo.com/quote/DDR.AX
		URI uri = new URI("https://finance.yahoo.com/quote/DDR.AX");		
		//,"currency":"AUD","regularMarketPrice":{"raw":35.5,"fmt":"35.50"}
		
		String code = "DDR";		
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);
		System.out.println(result.getBody());
		
		String aQuote = result.getBody();
		System.out.println(aQuote);
		
		Files.write(Paths.get("c:\\temp\\output.txt"), aQuote.getBytes());
		
		// regex
		
		String matchingRegex = "\"currency\":\"AUD\",\"regularMarketPrice\":{\"raw\":[0-9]+.[0-9]+,\"fmt\":\".*\"}";
		
		System.out.println("done!");
		
	}

}
