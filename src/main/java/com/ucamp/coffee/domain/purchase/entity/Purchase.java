package com.ucamp.coffee.domain.purchase.entity;

import java.time.LocalDateTime;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.purchase.type.PaymentStatus;
import com.ucamp.coffee.domain.subscription.entity.Subscription;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "PURCHASE")
public class Purchase extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long purchaseId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member buyer;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_member_id")
    private Member receiver;

	@ManyToOne
	@JoinColumn(name = "subscription_id")
	private Subscription subscription;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	private LocalDateTime refundedAt;

	private String isGift;
	private String giftMessage;

	private String purchaseType;
	
	//환불 처리
	public void refundedPurchase() {
		this.paymentStatus = PaymentStatus.REFUNDED;
		this.refundedAt = LocalDateTime.now();
	}

}
