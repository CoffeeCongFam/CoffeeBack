package com.ucamp.coffee.domain.notification.dto;

import java.time.LocalDateTime;

import com.ucamp.coffee.domain.notification.entity.Notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

	private Long notificationId;
	private String notificationType;
	private String notificationContent;
	//해당 타입에 대한 타겟 ID, order타입이면 orderId, subscription이면 memberSubscriptionId, gift는 purchaseId;
	private Long targetId; 
	private LocalDateTime readAt;
	private LocalDateTime createdAT;

	//entity -> DTO 변환
	public static NotificationResponseDTO toDTO(Notification entity) {
		return NotificationResponseDTO.builder().notificationId(entity.getNotificationId())
				.notificationType(entity.getNotificationType().name())
				.notificationContent(entity.getNotificationContent()).readAt(entity.getReadAt())
				.createdAT(entity.getCreatedAt()).targetId(entity.getTargetId()).build();
	}
}
