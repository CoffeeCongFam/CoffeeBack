package com.ucamp.coffee.domain.purchase.dto;

import lombok.Data;

@Data
public class PortOneTokenResponseDTO {
	private int code;
	private String message;
	private TokenData response;
	
	@Data
	public class TokenData {
		private String access_token;
		private int expired_at;
	}
}
