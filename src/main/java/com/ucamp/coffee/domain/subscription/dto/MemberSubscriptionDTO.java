package com.ucamp.coffee.domain.subscription.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberSubscriptionDTO {

	private Long memberSubscriptionId;
	private Long memberId;
	private LocalDateTime subscriptionEnd;
}
