package com.rohan.stockapp.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.rohan.stockapp.entity.Holding;
import com.rohan.stockapp.repository.HoldingRepository;

@Component
public class Processor {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	HoldingRepository holdingRepository;
	
	@PostConstruct
	public void testIt() {
		List nudge = jdbcTemplate.queryForList("select * from holding");
		System.out.println(nudge);
		
		Holding holding = new Holding(2L, LocalDateTime.now(), LocalDateTime.now(), "VAS", new BigDecimal(1.23));
		holdingRepository.save(holding);
		
		System.out.println("done!");
		
	}

}
