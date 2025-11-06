package com.ucamp.coffee.domain.notification.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.notification.dto.NotificationResponseDTO;
import com.ucamp.coffee.domain.notification.entity.Notification;
import com.ucamp.coffee.domain.notification.repository.NotificationRepository;
import com.ucamp.coffee.domain.notification.type.NotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final SseService sseService;
	
	/**
	 * 새로운 알림 생성
	 * 
	 * @param memberId
	 * @param type
	 * @param content
	 */
	@Transactional
	public void createNotification(Member member, NotificationType type, String content) {

		Notification notification = Notification.builder().member(member).notificationType(type)
				.notificationContent(content).build();

		notificationRepository.save(notification);
		
		//실시간 sse 전송
		try {
			sseService.sendNotificationToClient(member.getMemberId(), content);
		} catch(Exception e) {
			 System.out.println("SSE 알림 전송 실패: " + e.getMessage());
		}
	}

	/**
	 * 사용자 전체 알림 조회
	 * 
	 * @param memberId
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<NotificationResponseDTO> selectAllNotification(Long memberId) {

		List<Notification> notificationList = notificationRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId);

		List<NotificationResponseDTO> response = new ArrayList<>();

		for (Notification notification : notificationList) {
			response.add(NotificationResponseDTO.toDTO(notification));
		}

		return response;
	}

	/**
	 * 알림 읽음 처리
	 * 
	 * @param notificationId
	 */
	@Transactional
	public void updateNotificationRead(Long notificationId) {

		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "존재하지 않는 알림입니다."));

		if (notification.getReadAt() != null) {
			throw new CommonException(ApiStatus.CONFLICT, "이미 읽은 알림입니다.");
		}

		notification.readNotification();
	}

	/**
	 * 알림 삭제 처리
	 * 
	 * @param notificationId
	 */
	@Transactional
	public void deleteNotification(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "존재하지 않는 알림입니다."));
		
		if (notification.getDeletedAt() != null) {
			throw new CommonException(ApiStatus.CONFLICT, "이미 삭제된 알림입니다.");
		}

		notification.deleteNotification();
	}
}
