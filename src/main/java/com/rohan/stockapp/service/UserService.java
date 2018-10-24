package com.rohan.stockapp.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rohan.stockapp.entity.Holding;
import com.rohan.stockapp.entity.User;
import com.rohan.stockapp.repository.UserRepository;

// https://medium.com/@nydiarra/secure-a-spring-boot-rest-api-with-json-web-token-reference-to-angular-integration-e57a25806c50
// https://medium.com/oril/secure-your-spring-boot-api-with-json-web-tokens-44aa9dc51fdc

@Component
public class UserService {
	
	@Autowired
	 private UserRepository userRepository;
	
	//@Autowired
	//private TokenService tokenService;
	 
	 /*
	 @Autowired
	    UserService(UserRepository userRepository, TokenService tokenService) {
	        this.userRepository = userRepository;
	        this.tokenService = tokenService;
	    }
	    */
	 
	 public User getUser(String username) {
	        return userRepository.findByUsername(username);
	    }
	 
	 public Set<Holding> getUserHoldings(User user) {
		return user.getHoldings();
	 }
	 
	  public User saveUser(User user) {
	        User savedUser = userRepository.save(user);
	        //System.out.println(tokenService.allocateToken(savedUser.getUsername()));
	        return savedUser;
	    }

}
