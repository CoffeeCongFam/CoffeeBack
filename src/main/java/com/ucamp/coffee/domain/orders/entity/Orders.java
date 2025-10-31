package com.ucamp.coffee.domain.orders.entity;

import java.time.LocalDateTime;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.orders.type.OrderStatusType;
import com.ucamp.coffee.domain.orders.type.OrderType;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "ORDERS")
public class Orders extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partner_store_id", nullable = false)
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_subscription_id", nullable = false)
	private MemberSubscription memberSubscription;

	private Integer totalQuantity;

	@Enumerated(EnumType.STRING)
	private OrderType orderType;

	@Enumerated(EnumType.STRING)
	private OrderStatusType orderStatus;

	private String rejectedReason;
	private Integer orderNumber;
	private LocalDateTime canceledAt;
	private LocalDateTime rejectedAt;

	// 소비자 주문 취소 메서드
	public void cancelOrder() {

		if (this.orderStatus == OrderStatusType.CANCELED) {
			throw new CommonException(ApiStatus.CONFLICT, "이미 취소된 주문입니다.");
		}

		this.orderStatus = OrderStatusType.CANCELED;
		this.canceledAt = LocalDateTime.now();

	}

}