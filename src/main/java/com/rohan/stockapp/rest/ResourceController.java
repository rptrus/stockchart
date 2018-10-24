package com.rohan.stockapp.rest;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mysql.jdbc.log.Log;
import com.rohan.stockapp.service.Processor;

@RestController
public class ResourceController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	Processor processor;
	
	//@PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('STANDARD_USER')")
	@GetMapping(value="/get")
	public ResponseEntity<String> get(@RequestBody String json) {
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
	}
	
	@Deprecated
	@PostMapping(value="/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> add(@RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		processor.addStock(json);
		System.out.println(json);
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
	}
	
	@PostMapping(value="/addmulti", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> addmulti(@RequestHeader("X-username") String username, @RequestHeader("X-password") String password, @RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		String arg = "\"Work in Progress!\"";
		if (processor.checkIfUserExists(username)) {
			logger.error("The user already exists - must use PUT");
			arg = "User already Exists - send a PUT request instead";
			// TODO return a custom error JSON
		}
		else
			processor.addStockMulti(json, username, password);
		System.out.println(json);
		return new ResponseEntity<String>(String.format("{ \"status\" : \"%s\" }",arg), HttpStatus.OK);
	}

	@PutMapping(value="/addmulti", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> modify(@RequestHeader("X-username") String username, @RequestHeader("X-password") String password, @RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		processor.addStockMulti(json, username, password);
		System.out.println(json);
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
		
	}
	
	@DeleteMapping(value="/delete", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> delete(@RequestBody String json) {
		System.out.println(json);
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
		
	}
	
	@PostConstruct
	public void filler() {
		processor.fillTheDatabase();
	}

}


// multi

/*
{
"stocks" : [
	{
	"stock":"WBC",
	"dateAdded" : "2018-10-20T13:23:43.533Z",
	"price" : 32.44
	},
	{
	"stock":"VAS",
	"dateAdded" : "2018-10-20T13:23:43.533Z",
	"price" : 72.43
	},
	]
	"comments" : "kecewa"
}
*/