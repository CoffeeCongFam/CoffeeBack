package com.ucamp.coffee.domain.purchase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PortOnePaymentResponseDTO {
	private int code;
	private String message;
	private Response response;

	@Data
	@NoArgsConstructor
	public static class Response {
		private String imp_uid;
		private String merchant_uid;
		private String pay_method;
		private String pg_provider;
		private String pg_tid;
		private String pg_id;
		private Integer amount;
		private String currency;
		private String apply_num;
		private String buyer_name;
		private String card_code;
		private String card_name;
		private String card_quota;
		private String card_number;
		private String card_type;
		private String status;
		private Long started_at;
		private Long paid_at;
		private Long canceled_at;
		private Long failed_at;
	}
}
