package com.ucamp.coffee.domain.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.notification.entity.Notification;
import com.ucamp.coffee.domain.notification.repository.NotificationRepository;
import com.ucamp.coffee.domain.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final MemberRepository memberRepository;

	/**
	 * 새로운 알림 생성
	 * 
	 * @param memberId
	 * @param type
	 * @param content
	 */
	@Transactional
	public void createNotification(Long memberId, NotificationType type, String content) {

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));

		Notification notification = Notification.builder().member(member).notificationType(type)
				.notificationContent(content).build();
		
		notificationRepository.save(notification);
	}
}
