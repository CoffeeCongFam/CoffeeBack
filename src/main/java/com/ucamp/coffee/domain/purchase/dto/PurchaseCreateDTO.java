package com.ucamp.coffee.domain.purchase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseCreateDTO {

	private Long subscriptionId; //구독권 ID
	private String purchaseType; //결제 유형
	private Long receiverMemberId; //수신자 ID
	private String giftMessage; //선물 메시지
}
