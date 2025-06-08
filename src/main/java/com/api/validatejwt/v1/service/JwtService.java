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

import jakarta.validation.ValidationException;

@Service
public class JwtService {

	public JwtDTO validate(Jwt jwtObj) {
		jwtObj.setClaims(validateClaimNames(jwtObj));
		EnumRole.isValidRole(jwtObj.getClaims().getRole());
		return new JwtDTO(true);
	}

	private Claims validateClaimNames(Jwt jwt) {
		String payloadJson;

		try {
			String[] parts = jwt.getJwtToken().split("\\.");
			if (parts.length != 3) {
				throw new IllegalArgumentException("JWT inválido");
			}

			payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
			Claims claims = new ObjectMapper().readValue(payloadJson, Claims.class);

			if (claims.getName() != null && claims.getName().matches(".*\\d.*")) {
				throw new ValidationException("Claim inválido: 'Name' não pode conter números");
			}

			return claims;

		} catch (UnrecognizedPropertyException e) {
			String allowedFields = Arrays.toString(getFieldNames(Claims.class));
			throw new ClientException(HttpStatus.OK,
					"Claim inválido: " + e.getPropertyName() + " / Campos permitidos: " + allowedFields);

		} catch (JsonParseException e){
			throw new ClientException(HttpStatus.OK, "JWT inválido");
		}
		catch (IllegalArgumentException | ValidationException e) {
			throw new ClientException(HttpStatus.OK, e.getMessage());

		} catch (Exception e) {
			throw new RuntimeException("Erro desconhecido: procure a equipe de suporte.", e);
		}
	}

	private String[] getFieldNames(Class<?> clazz) {
		return ReflectionUtils.getFieldNames(clazz);
	}
}
