package com.api.validatejwt.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
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
	@NotBlank
	private String role;
	
	@JsonProperty("Seed")
	@NotBlank
	private String seed;
	
	@JsonProperty("Name")
	@NotBlank
	private String name;
}
