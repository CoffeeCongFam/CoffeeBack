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
	
}
