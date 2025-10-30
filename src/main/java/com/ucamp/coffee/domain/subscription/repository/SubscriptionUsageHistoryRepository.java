package com.ucamp.coffee.domain.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;

@Repository
public interface SubscriptionUsageHistoryRepository extends JpaRepository<SubscriptionUsageHistory, Long> {

}
