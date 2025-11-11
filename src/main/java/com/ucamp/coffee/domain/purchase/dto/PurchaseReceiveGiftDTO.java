package com.ucamp.coffee.domain.purchase.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseReceiveGiftDTO {

	private Long purchaseId;
	private Long memberSubscriptionId;
	private String sender;
	private String receiver;
	private String subscriptionName;
	private int price;
	private int subscriptionPeriod;
	private LocalDateTime subscriptionStart;
	private LocalDateTime subscriptionEnd;
	private String subscriptionType;
	private String subscriptionImg;
	private String storeName;
	private String giftMessage;
	private String usageStatus;
	private int dailyRemainCount;
	private LocalDateTime refundedAt;
	
	private List<MenuDTO> menuList;
	private List<UsageHistoryDTO> usageHistoryList;
	
	@Data
	@NoArgsConstructor
	public static class MenuDTO {
		private long menuId;
		private String menuName;
	}
	
	@Data
	@NoArgsConstructor
	public static class UsageHistoryDTO {
		private long usageHistoryId;
		private LocalDateTime usedAt;
	}
	
}
