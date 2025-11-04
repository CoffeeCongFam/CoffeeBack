//package com.ucamp.coffee.domain.notification.controller;
//
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import com.ucamp.coffee.common.security.MemberDetails;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequiredArgsConstructor
//public class SseController {
//
//	@GetMapping(value = "api/common/connect", produces = "text/event-stream")
//	public SseEmitter connect(@AuthenticationPrincipal MemberDetails member) {
//		
//		Long memberId = member.getMemberId();
//		
////		return notificationService.connect(memberId);
//		return null;
//		
//	}
//}
