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
import com.rohan.stockapp.enums.StatusFlags;
import com.rohan.stockapp.json.Status;
import com.rohan.stockapp.service.Processor;

@RestController
public class ResourceController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	Processor processor;
	
	@GetMapping(value="/", produces = "application/json")
	public ResponseEntity<Status> get(@RequestHeader("X-username") String username, @RequestHeader("X-password") String password) {
		Status status = new Status();
		HttpStatus requestStatus = HttpStatus.OK;
		int num = processor.getStockPortfolio(username, password);
		status.setComments("Processed request successfully");
		return new ResponseEntity<Status>(status, requestStatus);
	}
	
	@Deprecated
	@PostMapping(value="/add", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> add(@RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		processor.addStock(json);
		System.out.println(json);
		return new ResponseEntity<String>("{ \"status\" : \"Work in progress!\" }", HttpStatus.OK);
	}
	
	@PostMapping(value="/addmulti", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Status> addmulti(@RequestHeader("X-username") String username, @RequestHeader("X-password") String password, @RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		Status status = new Status();
		HttpStatus requestStatus;
		status.setComments("Processed stock add request successfully");
		if (processor.checkIfUserExists(username)) {
			logger.error("The user already exists - must use PUT");
			status.setComments("User already Exists - send a PUT request instead");
			status.setStatus(StatusFlags.FAIL.name());
			requestStatus =  HttpStatus.BAD_REQUEST;			
		}
		else {
			int num = processor.addStockMulti(json, username, password);
			status.setCount(num);
			requestStatus =  HttpStatus.OK;
		} 
		System.out.println(json);
		return new ResponseEntity<Status>(status, requestStatus);
	}

	@PutMapping(value="/addmulti", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Status> modify(@RequestHeader("X-username") String username, @RequestHeader("X-password") String password, @RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		Status status = new Status();
		HttpStatus requestStatus = HttpStatus.OK;
		int num = processor.addStockMulti(json, username, password);
		status.setComments("Processed stock add request successfully");
		status.setCount(num);
		System.out.println(json);
		return new ResponseEntity<Status>(status, requestStatus);
		
	}
	
	@DeleteMapping(value="/delete", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Status> delete(@RequestHeader("X-username") String username, @RequestHeader("X-password") String password, @RequestBody String json) throws JsonParseException, JsonMappingException, IOException {
		Status status = new Status();
		HttpStatus requestStatus = HttpStatus.OK;
		status.setComments("Processed delete request successfully [negative count permissable on delete]");
		processor.deleteStock(json, username, password);
		System.out.println(json);
		return new ResponseEntity<Status>(status, requestStatus);
		
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