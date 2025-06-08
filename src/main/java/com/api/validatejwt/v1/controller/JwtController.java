package com.api.validatejwt.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.validatejwt.v1.model.Jwt;
import com.api.validatejwt.v1.model.JwtDTO;
import com.api.validatejwt.v1.service.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class JwtController {

	@Autowired
	private JwtService jwtService;

	@PostMapping(value = "/jwt")
	public ResponseEntity<JwtDTO> validateJwtToken(@RequestBody @Valid Jwt jwt) {
		
		return ResponseEntity.ok(jwtService.validate(jwt));
	}
}
