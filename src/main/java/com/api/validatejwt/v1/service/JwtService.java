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

	private Claims getClaims(Jwt jwt) {

		try {
			String[] parts = jwt.getJwtToken().split("\\.");
			if (parts.length != 3)
				throw new IllegalArgumentException("JWT malformado");

			String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
			
			return new ObjectMapper().readValue(payloadJson, Claims.class);
		} catch (UnrecognizedPropertyException e) {
			throw new ClientException(HttpStatus.BAD_REQUEST, "Claims permitidos:" + Arrays.toString(ReflectionUtils.getFieldNames(Claims.class)));
		} catch(JsonParseException e) {
			throw new ClientException(HttpStatus.BAD_REQUEST, "JWT Inválido");
		} catch (Exception e) {
			throw new RuntimeException("Erro ao desconhecido: Procure a equipe de suporte", e);
		}
	}

}
