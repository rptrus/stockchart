package com.rohan.stockapp.rest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
	
	@GetMapping(value="/get")
	public String get() {
		return "hello";
	}
	
	@PostMapping(value="/add")
	public String add() {
		return ":)";
	}
	
	@DeleteMapping(value="/delete")
	public String delete() {
		return ":~(";
	}
	
	@PutMapping(value="/modify")
	public String modify() {
		return "now";
	}

}
