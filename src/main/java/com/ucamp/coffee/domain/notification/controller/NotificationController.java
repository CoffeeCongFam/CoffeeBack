package com.ucamp.coffee.domain.notification.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.domain.notification.dto.NotificationResponseDTO;
import com.ucamp.coffee.domain.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	// 알림 모두 조회(날짜순) - 삭제 안된 알림만
	@GetMapping("/api/common/notification")
	public ResponseEntity<ApiResponse<?>> searchAllNotification() {

		Long memberId = 1L; // -----------------------추후 수정
		List<NotificationResponseDTO> response = notificationService.selectAllNotification(memberId);

		return ResponseMapper.successOf(response);
	}

	// 알림 읽음 처리
	@PatchMapping("/api/common/notification/{notificationId}")
	public ResponseEntity<ApiResponse<?>> readNotification(@PathVariable Long notificationId) {

		notificationService.updateNotificationRead(notificationId);

		return ResponseMapper.successOf(null);
	}

	// 알림 모두 삭제
	@DeleteMapping("/api/common/notification/{notificationId}")
	public ResponseEntity<ApiResponse<?>> deleteNotification(@PathVariable Long notificationId) {

		notificationService.deleteNotification(notificationId);

		return ResponseMapper.successOf(null);
	}
}
