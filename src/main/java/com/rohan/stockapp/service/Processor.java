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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohan.stockapp.dto.StockReportElement;
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
	
	public int addStockMulti(String json, String username, String password) throws JsonParseException, JsonMappingException, IOException, InterruptedException, ExecutionException {
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
		Map<String, BigDecimal> returnedPrices = null;
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
			newHolding.setNumberOfUnits(newStock.getNumberOfUnits());
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
		//returnedPrices = getLatestPrices(stockElementList);
		List<StockReportElement> stockElementList = toStockElementList(userHoldings, getLatestPrices(userHoldings));
		constructChart(stockElementList); // TODO Have this DB driven, not JSON driven
		return numberOfHoldings;
	}
	
	// We will rectify the DB elements (that have been sync'd by JSON together with the pulled stock prices  
	private List<StockReportElement> toStockElementList(Set<Holding> userHoldings, Map<String, BigDecimal> latestStockPrices) {
		List<StockReportElement> stockList = new ArrayList<>();
		for (Holding holding : userHoldings) {
			StockReportElement stockReportElement = new StockReportElement();
			stockReportElement.setCode(holding.getCode());
			stockReportElement.setAcquiredPrice(holding.getPrice());
			stockReportElement.setDateAcquired(holding.getDateAcquired());
			stockReportElement.setNumberOfUnits(holding.getNumberOfUnits());
			stockReportElement.setCurrentPrice(latestStockPrices.get(holding.getCode()));
			stockList.add(stockReportElement);
		}
		return stockList;
	}
	
	public boolean checkIfUserExists(String username) {
		return userService.getUser(username) != null;
	}
	
	private void constructChart(List<StockReportElement> stockElementList) {
		synchroniseCurrentPrices(stockElementList); // can remove this
		chart.makePDFChart(stockElementList);
	}
	
	// If we don't have a latest price, then what we will do is set it to the current price and flag an alert
	private void synchroniseCurrentPrices(List<StockReportElement> stockElementList) {
			for (StockReportElement stock : stockElementList) {
					if (stock.getCurrentPrice() == null) 
							stock.setCurrentPrice(stock.getAcquiredPrice()); // hack. 0% growth
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
		Holding holding1 = new Holding(LocalDateTime.now(), LocalDateTime.now(), "VAS", new BigDecimal(1.23),10);
		Quote quote1 = new Quote("VAS", new BigDecimal(1.99));
		holding1.setQuote(quote1);
		holding1.setUser(user);
		quote1.setHolding(holding1);
		
		// 2
		Holding holding2 = new Holding(LocalDateTime.now(), LocalDateTime.now(), "WBC", new BigDecimal(26.99),20);
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
	
	public int getStockPortfolio(String username, String password) throws InterruptedException, ExecutionException {
		return getStockPortfolio(username, password, null);
	}

	public int getStockPortfolio(String username, String password, Set<Holding> userHoldings) throws InterruptedException, ExecutionException {
		// retrieve the user details to get existing allotment
		//fillTheDatabase(); // fill up with dummy data. look at a way to do this on startup in Spring (must be a way!)
		int num = 0; 
		if (userHoldings == null) {
			User theUser = userService.getUser(username);
			userHoldings = userService.getUserHoldings(theUser); // A
		}
		num = userHoldings.size();
		List<StockReportElement> stockElementList = toStockElementList(userHoldings, getLatestPrices(userHoldings));
		constructChart(stockElementList);

		return num;
	}
	
	// Test the hell out of this....
	private Map<String, BigDecimal> getLatestPrices(Set<Holding> holdings) throws InterruptedException, ExecutionException {
		Map<String, BigDecimal> returnedPrices = new HashMap<String, BigDecimal>();
		Map<String, Future<String>> waitForPrices = new HashMap<String, Future<String>>();
		for (Holding stock: holdings) {
			Future<String> currentPrice = stockPriceRetriever.retrieveQuote(stock.getCode());
			waitForPrices.put(stock.getCode(), currentPrice);			
		}
		// now we just get all our values that have been computed in parallel 
		for (Map.Entry<String, Future<String>> entry : waitForPrices.entrySet())
		{
		    returnedPrices.put(entry.getKey(), new BigDecimal(entry.getValue().get()));
		}
		return returnedPrices;
	}

}
