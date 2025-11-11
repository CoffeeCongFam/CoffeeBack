package com.ucamp.coffee.domain.purchase.entity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

	private Integer paymentAmount;
	private String merchantUid;
	private String impUid;
	private LocalDateTime paidAt;

	// 환불 처리
	public void refundedPurchase() {
		this.paymentStatus = PaymentStatus.REFUNDED;
		this.refundedAt = LocalDateTime.now();
	}

	// 구매 상태 변경
	public void changePaymentStatus(PaymentStatus status) {
		this.paymentStatus = status;
	}

	// 결제 검증 성공
	public void handlePaymentSuccess(Long paidAt, String impUid, String purchaseType) {
		this.paymentStatus = PaymentStatus.PAID;
		this.paidAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(paidAt), ZoneId.systemDefault());
		this.impUid = impUid;
		this.paidAt = Instant.ofEpochSecond(paidAt).atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
		this.purchaseType = purchaseType;
	}

}
