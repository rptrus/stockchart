package com.rohan.stockapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rohan.stockapp.repository.UserRepository;

@Component
public class Authorisation {
	
	@Autowired
	UserRepository userRepository;
	
	public boolean auth(String user) {
		com.rohan.stockapp.entity.User aUser = userRepository.findByUsername("rohan");
		System.out.println(aUser.getPassword());
		return true;
	}

}
