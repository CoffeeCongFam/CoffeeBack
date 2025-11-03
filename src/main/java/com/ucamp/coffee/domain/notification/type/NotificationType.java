package com.ucamp.coffee.domain.notification.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

	SUBSCRIPTION("구독"), // 구독 관련 알림
	ORDER("주문"), // 주문 관련 알림
	GIFT("선물"), // 선물 관련 알림
	EVENT("이벤트"), // 이벤트/프로모션 알림
	NOTICE("공지"), // 공지 알림
	ETC("기타"); // 기타 알림

	private final String description;
}
