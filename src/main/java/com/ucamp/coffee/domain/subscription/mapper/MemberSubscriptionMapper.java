package com.ucamp.coffee.domain.subscription.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ucamp.coffee.domain.subscription.dto.MemberSubscriptionDTO;

@Mapper
public interface MemberSubscriptionMapper {

	List<MemberSubscriptionDTO> selectExpiring7Days();
	
	List<MemberSubscriptionDTO> selectExpiring3Days();
	
	List<MemberSubscriptionDTO> selectExpiringToday();
}
