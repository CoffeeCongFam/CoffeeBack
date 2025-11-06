package com.ucamp.coffee.domain.subscription.entity;

import java.time.LocalDateTime;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.purchase.entity.Purchase;
import com.ucamp.coffee.domain.subscription.type.UsageStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "MEMBER_SUBSCRIPTION")
public class MemberSubscription extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberSubscriptionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "purchase_id")
	private Purchase purchase;

	private String isGift;

	@Enumerated(EnumType.STRING)
	private UsageStatus usageStatus;

	private Integer dailyRemainCount;

	private LocalDateTime subscriptionStart;
	private LocalDateTime subscriptionEnd;

	private String isAutoPayment;

	@PrePersist
	public void prePersist() {

		if (isAutoPayment == null)
			isAutoPayment = "N";

		if (usageStatus == null)
			usageStatus = UsageStatus.NOT_ACTIVATED;
	}

	// 보유 구독권 사용중으로 변경
	public void activateSubscription() {
		this.usageStatus = UsageStatus.ACTIVE;
	}
	
	// 보유 구독권 사용
	public void use(int quantity) {
		if(this.usageStatus == UsageStatus.EXPIRED) {
			throw new CommonException(ApiStatus.BAD_REQUEST, "만료된 구독권입니다.");
		}
		
		if(this.dailyRemainCount < quantity) {
			throw new CommonException(ApiStatus.BAD_REQUEST, "잔여 횟수가 부족합니다.");
		}
		
		if(this.usageStatus == UsageStatus.NOT_ACTIVATED) {
			activateSubscription();
		}
		
		this.dailyRemainCount -= quantity;
	}
	
	//보유 구독권 사용횟수 복구
	public void rollbackCount(int quantity) {
		this.dailyRemainCount += quantity;
	}

}
