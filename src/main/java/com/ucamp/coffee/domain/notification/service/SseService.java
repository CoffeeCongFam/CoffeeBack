package com.ucamp.coffee.domain.notification.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SseService {

	private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<Long, SseEmitter>();
	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
	
	/**
	 * Emitter 생성
	 * @param memberId
	 * @return
	 */
	public SseEmitter createEmitter(Long memberId) {
		
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
		userEmitters.put(memberId, emitter);
		
		//만료시 
		emitter.onCompletion(() -> userEmitters.remove(memberId));
		emitter.onTimeout(() -> userEmitters.remove(memberId));
		emitter.onError((e) -> userEmitters.remove(memberId));
		
		return emitter;
	}
	
	/**
	 * 사용자게에 알림 실시간 전송
	 * @param memberId
	 */
	public void sendNotificationToClient(Long memberId, String message) {
		SseEmitter emitter = userEmitters.get(memberId);
		if(emitter != null) {
			try {
				emitter.send(SseEmitter.event().name("notification").data(message));
			} catch (IOException e) {
				userEmitters.remove(memberId);
			}
		}
	}
}
