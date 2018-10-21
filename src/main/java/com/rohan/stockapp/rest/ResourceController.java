package com.rohan.stockapp.rest;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rohan.stockapp.service.Processor;

@RestController
public class ResourceController {
	
	@Autowired
	Processor processor;
	
	//@PreAuthorize("hasAuthority('ADMIN_USER') or hasAuthority('STANDARD_USER')")
	@GetMapping(value="/get")
	public ResponseEntity<String> get(@RequestBody String json) {
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
	}
	
	@PostMapping(value="/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> add(@RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		processor.addStock(json);
		System.out.println(json);
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
	}

	@DeleteMapping(value="/delete")
	public ResponseEntity<String> delete(@RequestBody String json) {
		System.out.println(json);
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
		
	}
	
	@PutMapping(value="/modify")
	public ResponseEntity<String> modify(@RequestBody String json) {
		System.out.println(json);
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
		
	}

}
