package com.ucamp.coffee.domain.subscription.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.domain.subscription.dto.MemberSubscriptionDTO;
import com.ucamp.coffee.domain.subscription.event.NoticeBefore3Event;
import com.ucamp.coffee.domain.subscription.event.NoticeBefore7Event;
import com.ucamp.coffee.domain.subscription.event.NoticeTodayEvent;
import com.ucamp.coffee.domain.subscription.mapper.MemberSubscriptionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberSubscriptionService {

	private final MemberSubscriptionMapper memberSubscriptionMapper;
	
	private final ApplicationEventPublisher publisher;
	
	/**
	 * 만료 7일 전에 알림
	 */
	@Transactional(readOnly = true)
	public void notificationBefore7Days() {
		List<MemberSubscriptionDTO> list = memberSubscriptionMapper.selectExpiring7Days();
		for(MemberSubscriptionDTO dto : list) {
			publisher.publishEvent(new NoticeBefore7Event(dto.getMemberId(), dto.getMemberSubscriptionId()));
		}
	}
	
	/**
	 * 만료 3일 전에 알림
	 */
	@Transactional(readOnly = true)
	public void notificationBefore3Days() {
		List<MemberSubscriptionDTO> list = memberSubscriptionMapper.selectExpiring3Days();
		for(MemberSubscriptionDTO dto : list) {
			publisher.publishEvent(new NoticeBefore3Event(dto.getMemberId(), dto.getMemberSubscriptionId()));
		}
	}
	
	/**
	 * 만료 당일 알림
	 */
	@Transactional(readOnly = true)
	public void notificationToday() {
		List<MemberSubscriptionDTO> list = memberSubscriptionMapper.selectExpiringToday();
		for(MemberSubscriptionDTO dto : list) {
			publisher.publishEvent(new NoticeTodayEvent(dto.getMemberId(), dto.getMemberSubscriptionId()));
		}
	}
}
