package com.ucamp.coffee.domain.purchase.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseReceiveGiftDTO {

	private Long memberSubscriptionId;
	private String sender;
	private String receiver;
	private String productName;
	private int price;
	private int subscriptionPeriod;
	private LocalDateTime subscriptionEnd;
	private String subscriptionType;
	private String subscriptionImg;
	private String storeName;
	private String giftMessage;
	private LocalDateTime createdAt;
	private String status;
	private int maxDailyUsage;
}
