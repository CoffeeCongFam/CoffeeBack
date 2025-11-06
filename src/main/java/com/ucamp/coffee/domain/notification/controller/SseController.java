package com.ucamp.coffee.domain.notification.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.notification.service.SseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SseController {

	private final SseService sseService;
	
	/**
	 * SseEmitter 연결
	 * @param member
	 * @return
	 */
	@GetMapping(value = "/api/common/connect", produces = "text/event-stream;charset=UTF-8")
	public SseEmitter connect(@AuthenticationPrincipal MemberDetails member) {
		
		Long memberId = member.getMemberId();
		
		return sseService.createEmitter(memberId);
		
	}
}
