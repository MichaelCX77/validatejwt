package com.api.validatejwt.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.validatejwt.v1.model.JwtResponseModel;
import com.api.validatejwt.v1.service.JwtService;

@RestController
@RequestMapping("/api/v1")
public class JwtController {

	@RestController
	@RequestMapping(value = "/jwt")
	public class ClienteController {

		@Autowired
		private JwtService jwtService;

		@PostMapping
		public ResponseEntity<JwtResponseModel> validateJwtToken() {
			
			return ResponseEntity.ok(new JwtResponseModel());
		}
	}
}
