package com.ucamp.coffee.domain.purchase.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ucamp.coffee.domain.purchase.type.RefundReasonType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PurchaseAllResponseDTO {

	private String sender;
	private String receiver;
	private Long subscriptionId;
	private String subscriptionName;
	private LocalDateTime paidAt;
	private Long purchaseId;
	private Long memberSubscriptionId;
	private String paymentStatus;
	private String isGift;
	private String giftMessage;
	private Integer paymentAmount;
	private String usageStatus;
	private LocalDateTime refundedAt;
	private String storeName;
	private String merchantUid;
	private String purchaseType;

	private List<RefundReasonType> refundReasons = new ArrayList<>();

}
