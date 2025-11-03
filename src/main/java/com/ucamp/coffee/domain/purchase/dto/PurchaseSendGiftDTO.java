package com.ucamp.coffee.domain.purchase.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseSendGiftDTO {

	private Long purchaseId;
	private String sender;
	private String receiver;
	private String subscriptionName;
	private int price;
	private int subscriptionPeriod;
	private String storeName;
	private LocalDateTime paidAt;
	private String purchaseType;
	private String giftMessage;
	private String subscriptionType;
	private String subscriptionImg;
	private int maxDailyUsage;
	private List<MenuDTO> menuList;
	
	@Data
	@NoArgsConstructor
	public static class MenuDTO {
		private Long menuId;
		private String menuName;
	}
}
