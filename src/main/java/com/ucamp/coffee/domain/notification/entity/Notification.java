package com.ucamp.coffee.domain.notification.entity;

import java.time.LocalDateTime;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.notification.type.NotificationType;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "NOTIFICATION")
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notificationId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
	
	private LocalDateTime readAt;
	private String notificationContent;
	private Long targetId;
	
	//알림 읽음처리
	public void readNotification() {
		this.readAt = LocalDateTime.now();
	}
	
	//알림 삭제
	public void deleteNotification() {
		setDeletedAt(LocalDateTime.now());
	}
	
}
