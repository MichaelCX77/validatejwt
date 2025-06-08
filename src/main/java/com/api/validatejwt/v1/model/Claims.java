package com.api.validatejwt.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Claims {
	
	@JsonProperty("Role")
	private String role;
	
	@JsonProperty("Seed")
	private String seed;
	
	@JsonProperty("Name")
	private String name;
}
