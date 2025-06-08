package com.api.validatejwt.v1.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private Boolean isValid;
	private String message;

	public JwtDTO (Boolean isValid) {
		this.isValid =  isValid;
	}
}
