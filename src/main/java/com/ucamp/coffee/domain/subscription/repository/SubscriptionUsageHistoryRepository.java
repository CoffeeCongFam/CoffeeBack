package com.ucamp.coffee.domain.subscription.repository;

import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionUsageHistoryRepository extends JpaRepository<SubscriptionUsageHistory, Long> {
}
