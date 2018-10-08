package com.rohan.stockapp.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
	
	@Autowired
	ChartConstruction chart;
	
	@Autowired
	StockPriceRetriever stockPriceRetriever;
	
	@PostConstruct
	public void testIt() throws URISyntaxException, IOException, InterruptedException, ExecutionException {
		
		chart.makePDFChart();
		System.out.println("Done with chart!");
		
		List nudge = jdbcTemplate.queryForList("select * from holding");
		System.out.println(nudge);
		
		Holding holding = new Holding(2L, LocalDateTime.now(), LocalDateTime.now(), "VAS", new BigDecimal(1.23));
		Quote quote = new Quote(2L, "VAS", new BigDecimal(1.99));
		holding.setQuote(quote);
		quote.setHolding(holding);
//		holdingRepository.save(holding);				

		// need to poll 1 by 1
		Future<String> a = stockPriceRetriever.retrieveQuote("VTS");
		Future<String> b = stockPriceRetriever.retrieveQuote("BHP");
		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
				
		System.out.println("->"+ a.get());
		System.out.println("->"+ b.get());
		
		
		// regex
		
		String matchingRegex = "\"currency\":\"AUD\",\"regularMarketPrice\":{\"raw\":[0-9]+.[0-9]+,\"fmt\":\".*\"}";
		
		System.out.println("done!");
		
	}

}
