package com.api.validatejwt.v1.model;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Jwt implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@NotBlank(message = "Token JWT não pode ser vazio")
	@NotNull(message = "Token JWT não pode ser vazio")
	private String jwtToken;
	
	@Null(message = "O campo 'claims' deve estar ausente")
	private Claims claims; 

}
