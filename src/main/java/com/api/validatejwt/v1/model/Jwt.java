package com.api.validatejwt.v1.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Jwt implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@NotBlank(message = "jwt é obrigatório")
	private String jwt;

}
