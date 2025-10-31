package com.ucamp.coffee.domain.subscription.entity;

import com.ucamp.coffee.common.entity.BaseEntity;

import jakarta.persistence.Entity;
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
@Table(name = "Subscription_Usage_History")
public class SubscriptionUsageHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long subscriptionUsageHistoryId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_subscription_id")
	private MemberSubscription memberSubscription;
	
	
	
}
