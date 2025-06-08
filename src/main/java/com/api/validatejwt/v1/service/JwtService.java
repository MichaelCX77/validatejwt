package com.api.validatejwt.v1.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.api.validatejwt.v1.enums.EnumRole;
import com.api.validatejwt.v1.exceptions.ClientException;
import com.api.validatejwt.v1.model.Claims;
import com.api.validatejwt.v1.model.Jwt;
import com.api.validatejwt.v1.model.JwtDTO;
import com.api.validatejwt.v1.util.ReflectionUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@Service
public class JwtService {

	public JwtDTO validate(Jwt jwtObj) {

		jwtObj.setClaims(getClaims(jwtObj));
		
		EnumRole.isValidRole(jwtObj.getClaims().getRole());
		
		return new JwtDTO(true);

	}

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	private Claims getClaims(Jwt jwt) {
		
		String payloadJson = null;
		
	    try {
	        String[] parts = jwt.getJwtToken().split("\\.");
	        if (parts.length != 3) {
	            throw new IllegalArgumentException();
	        }

	        payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
	        return objectMapper.readValue(payloadJson, Claims.class);

	    } catch (UnrecognizedPropertyException e) {
	        String allowedFields = Arrays.toString(getFieldNames(Claims.class));
	        throw new ClientException(HttpStatus.BAD_REQUEST, "Claim inválido:" + e.getPropertyName() + ". Campos permitidos: " + allowedFields);

	    } catch (JsonParseException | IllegalArgumentException e) {
	        throw new ClientException(HttpStatus.OK, "JWT inválido");
	    } catch (Exception e) {
	        throw new RuntimeException("Erro desconhecido: procure a equipe de suporte.", e);
	    }
	}
	
	private String[] getFieldNames(Class<?> clazz) {
	    return ReflectionUtils.getFieldNames(clazz);
	}

}
