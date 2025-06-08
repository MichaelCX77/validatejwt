package com.api.validatejwt.v1.model;

import java.io.Serializable;

import org.slf4j.MDC;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private Boolean isValid;
	private String requestId;
	
	public JwtDTO (Boolean isValid) {
		this.isValid =  isValid;
		this.requestId = MDC.get("requestId");
	}
}
