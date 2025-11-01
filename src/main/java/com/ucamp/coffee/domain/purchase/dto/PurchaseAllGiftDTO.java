package com.ucamp.coffee.domain.purchase.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseAllGiftDTO {

	private String isGift;
	private String sender;
	private String receiver;
	private Long subscriptionId;
	private String subscriptionName;
	private LocalDateTime createdAt;
	private Long purchaseId;
	private Long memberSubscriptionId;
}
