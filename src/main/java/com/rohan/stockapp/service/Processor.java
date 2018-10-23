package com.rohan.stockapp.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohan.stockapp.entity.Holding;
import com.rohan.stockapp.entity.Quote;
import com.rohan.stockapp.entity.User;
import com.rohan.stockapp.json.StockAdd;
import com.rohan.stockapp.json.StockSet;
import com.rohan.stockapp.repository.UserRepository;

@RestController
public class Processor {
	
	@Autowired
	ObjectMapper objectmapper;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ChartConstruction chart;
	
	@Autowired
	private StockPriceRetriever stockPriceRetriever;
	
	@Autowired
	private UserService userService;
	
	public void addStock(String json) throws JsonParseException, JsonMappingException, IOException {
		StockAdd stockAdd = objectmapper.readValue(json, StockAdd.class);
		System.out.println(stockAdd);
		// Add it to the Database
		// Process to the ChartConstruct
//		constructChart(stockAdd);
	}
	
	public void addStockMulti(String json) throws JsonParseException, JsonMappingException, IOException {
		StockSet stockSet = objectmapper.readValue(json, StockSet.class);
		System.out.println(stockSet);
		// Add it to the Database
		// TODO
		// Obtain the latest prices
		Map<String, BigDecimal> latestPrices = new HashMap<String, BigDecimal>();
		latestPrices.put("WBC", new BigDecimal(34.01));
		latestPrices.put("VAS", new BigDecimal(77.12));
		// TODO. Mock it up for now
		// Process to the ChartConstruct
		constructChart(stockSet, latestPrices);
	}
	
	private void constructChart(StockSet stockSet, Map<String, BigDecimal> latestPrices) {
		chart.makePDFChart(stockSet, latestPrices);
	}
	
	//@PostConstruct
	public void testIt() throws URISyntaxException, IOException, InterruptedException, ExecutionException {
		
		chart.makePDFChart(null);
		System.out.println("Done with chart!");
		
		List<Map<String, Object>> holdingList = jdbcTemplate.queryForList("select * from holding");
		System.out.println(holdingList);
		
		User user = new User();
		user.setBio("The founding user");
		user.setDateJoined(LocalDateTime.now());
		user.setUsername("rohan");
		user.setPassword(Utils.md5Hash("password"));

		// 1
		Holding holding1 = new Holding(LocalDateTime.now(), LocalDateTime.now(), "VAS", new BigDecimal(1.23));
		Quote quote1 = new Quote("VAS", new BigDecimal(1.99));
		holding1.setQuote(quote1);
		holding1.setUser(user);
		quote1.setHolding(holding1);
		
		// 2
		Holding holding2 = new Holding(LocalDateTime.now(), LocalDateTime.now(), "WBC", new BigDecimal(26.99));
		Quote quote2 = new Quote("WBC", new BigDecimal(28.99));
		holding2.setQuote(quote2);
		holding2.setUser(user);
		quote2.setHolding(holding2);
		
		Set<Holding> stockSet = new HashSet<>();
		stockSet.add(holding1);
		stockSet.add(holding2);
		
		user.setHoldings(stockSet);
		
		userRepository.save(user);
		
		User retrieved = userRepository.findByUsername("rohan");
		User retrieved1 = userService.getUser("rohan");
		

		// need to poll 1 by 1
		Future<String> a = stockPriceRetriever.retrieveQuote("VTS");
		Future<String> b = stockPriceRetriever.retrieveQuote("BHP");
		Future<String> c = stockPriceRetriever.retrieveQuote("WBC");
		Future<String> d = stockPriceRetriever.retrieveQuote("KGN");
		Future<String> e = stockPriceRetriever.retrieveQuote("RHC");
		Future<String> f = stockPriceRetriever.retrieveQuote("FLT");
		
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
				
		System.out.println("VTS->"+ a.get());
		System.out.println("BHP->"+ b.get());		
		System.out.println("WBC->"+ c.get());
		System.out.println("KGN->"+ d.get());
		System.out.println("RHC->"+ e.get());
		System.out.println("FLT->"+ f.get());
		
		
		// regex
		
		String matchingRegex = "\"currency\":\"AUD\",\"regularMarketPrice\":{\"raw\":[0-9]+.[0-9]+,\"fmt\":\".*\"}";
		
		System.out.println("done!");
		
	}

}
