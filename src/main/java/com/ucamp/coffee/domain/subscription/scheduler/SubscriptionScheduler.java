package com.ucamp.coffee.domain.subscription.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ucamp.coffee.domain.subscription.service.MemberSubscriptionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

	private final  MemberSubscriptionService memberSubscriptionService;
	
	@Scheduled(cron = "0 00 08 * * *", zone = "Asia/Seoul")
	public void sendSubscriptionNotification() {
		
		memberSubscriptionService.notificationBefore7Days();
		memberSubscriptionService.notificationBefore3Days();
		memberSubscriptionService.notificationToday();
	}
	
}
