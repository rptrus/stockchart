package com.rohan.stockapp.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohan.stockapp.entity.Holding;
import com.rohan.stockapp.entity.Quote;
import com.rohan.stockapp.entity.User;
import com.rohan.stockapp.json.Stock;
import com.rohan.stockapp.json.StockAdd;
import com.rohan.stockapp.json.StockSet;
import com.rohan.stockapp.repository.HoldingRepository;
import com.rohan.stockapp.repository.UserRepository;

@RestController
public class Processor {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
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
	
	@Autowired
	private HoldingRepository holdingRepository;
	
	public void addStock(String json) throws JsonParseException, JsonMappingException, IOException {
		StockAdd stockAdd = objectmapper.readValue(json, StockAdd.class);
		System.out.println(stockAdd);
		// Add it to the Database
		// Process to the ChartConstruct
//		constructChart(stockAdd);
	}
	
	public int addStockMulti(String json, String username, String password) throws JsonParseException, JsonMappingException, IOException {
		int numberOfHoldings = 0;
		StockSet stockSet = objectmapper.readValue(json, StockSet.class);
		System.out.println(stockSet);
		// retrieve the user details to get existing allotment
		//fillTheDatabase(); // fill up with dummy data. look at a way to do this on startup in Spring (must be a way!)
		User theUser = userService.getUser(username);
		Set<Holding> userHoldings = userService.getUserHoldings(theUser); // A
		userHoldings.size();
		// Add it to the Database
		// TODO - find the codes in the JSON that aren't in the DB
		stockSet.getStocks(); // B
		// filter out the things that don't match, leaving the things that are left=new O(N^2) but OK for small porfolios
		// Next, do the other way, so that we can delete things out too
		List<Stock> myNewList = stockSet.getStocks().stream().filter(jStock-> userHoldings.stream().noneMatch(dbStock -> dbStock.getCode().equals(jStock.getStock()))  ).collect(Collectors.toList());
		List<Holding> myRemList = userHoldings.stream().filter(jStock-> stockSet.getStocks().stream().noneMatch(dbStock -> dbStock.getStock().equals(jStock.getCode()))  ).collect(Collectors.toList());
		for (Stock newStock : myNewList) {
			Instant addDate = Instant.parse(newStock.getDateAdded());
			LocalDateTime newDate = LocalDateTime.ofInstant(addDate, ZoneId.systemDefault());
			/*
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm");
			LocalDateTime formatDateTime = LocalDateTime.parse(newStock.getDateAdded(), formatter);
			*/
			
			Holding newHolding = new Holding();
			newHolding.setCode(newStock.getStock());
			newHolding.setDateAcquired(newDate);
			newHolding.setPrice(new BigDecimal(newStock.getPrice()));
			newHolding.setUser(theUser);
			//newHolding.setQuote(quote);
			userHoldings.add(newHolding);
			//theUser.setHoldings(userHoldings);			
			holdingRepository.save(newHolding);
			numberOfHoldings++;
		}
		for (Holding deleteStock : myRemList) {			 
			java.util.Iterator<Holding> iter = userHoldings.iterator();
			while (iter.hasNext()) {
				Holding holding = iter.next();
				if (holding.getCode().equals(deleteStock.getCode())) {
					System.out.println("We have a match - now we can delete "+holding.getCode());
					userHoldings.remove(holding);
					holdingRepository.save(holding);
					numberOfHoldings--;
				}
			}			
		}
		// Obtain the latest prices
		Map<String, BigDecimal> latestPrices = new HashMap<String, BigDecimal>();
		latestPrices.put("WBC", new BigDecimal(34.01).setScale(2, RoundingMode.HALF_EVEN));
		latestPrices.put("VAS", new BigDecimal(77.12).setScale(2, RoundingMode.HALF_EVEN));
		// TODO. Mock it up for now
		// Process to the ChartConstruct
		constructChart(stockSet, latestPrices);
		return numberOfHoldings;
	}
	
	public boolean checkIfUserExists(String username) {
		return userService.getUser(username) != null;
	}
	
	private void constructChart(StockSet stockSet, Map<String, BigDecimal> latestPrices) {
		synchroniseCurrentPrices(stockSet, latestPrices);
		chart.makePDFChart(stockSet, latestPrices);
	}
	
	// If we don't have a latest price, then what we will do is set it to the current price and flag an alert
	private void synchroniseCurrentPrices(StockSet stockSet, Map<String, BigDecimal> latestPrices) {
			for (Stock stock :stockSet.getStocks()) {
				if (latestPrices.get(stock.getStock()) == null) {
					logger.error("Missing 'Latest Price' of {}",stock.getStock());
					latestPrices.put(stock.getStock(), BigDecimal.valueOf(stock.getPrice()).setScale(2, RoundingMode.HALF_EVEN));
				}
			}
	}

	public void fillTheDatabase() {
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
	}
	
	//@PostConstruct
	public void testIt() throws URISyntaxException, IOException, InterruptedException, ExecutionException {
		
		chart.makePDFChart(null);
		System.out.println("Done with chart!");
		
		fillTheDatabase();

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

	@Transactional
	public void deleteStock(String json, String username, String password) throws JsonParseException, JsonMappingException, IOException {
		User theUser = userService.getUser(username); //DB
		Set<Holding> userHoldings = userService.getUserHoldings(theUser);
		int size = userHoldings.size();
		if (size >= 1) { // DB must have at least one
			
			StockSet stockSet = objectmapper.readValue(json, StockSet.class);
			Stock stock = stockSet.getStocks().get(0); // we only have one
			Holding holding2Delete = holdingRepository.findByCode(stock.getStock());
			//holdingRepository.delete(holding2Delete);
			if (userHoldings.remove(holding2Delete))
				holdingRepository.save(holding2Delete);
			System.out.println("Done");
		} else if (size == 0) {
			logger.error("Nothing to delete!");
		}
	}

	public int getStockPortfolio(String username, String password) {
		// retrieve the user details to get existing allotment
		//fillTheDatabase(); // fill up with dummy data. look at a way to do this on startup in Spring (must be a way!)
		User theUser = userService.getUser(username);
		Set<Holding> userHoldings = userService.getUserHoldings(theUser); // A

		Map<String, BigDecimal> latestPrices = new HashMap<String, BigDecimal>();
		latestPrices.put("WBC", new BigDecimal(34.01).setScale(2, RoundingMode.HALF_EVEN));
		latestPrices.put("VAS", new BigDecimal(77.12).setScale(2, RoundingMode.HALF_EVEN));
		// we don't have a stockset, buy we have a json which can be morphed into one
		
		StockSet stockSet = new StockSet();
		List<Stock> stockList = new ArrayList<Stock>();
		// hacky
		for (Holding holding : userHoldings) {
			Stock stock = new Stock();						
			stock.setDateAdded(holding.getDateAcquired().toString()); // do this later
			stock.setPrice(Float.valueOf(holding.getPrice().floatValue()));
			stock.setStock(holding.getCode());
			stockList.add(stock);
		}
		stockSet.setStocks(stockList);
		constructChart(stockSet, latestPrices);

		return userHoldings.size();
	}

}
