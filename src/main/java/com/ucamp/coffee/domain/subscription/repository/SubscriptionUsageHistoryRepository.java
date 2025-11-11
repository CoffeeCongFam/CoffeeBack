package com.ucamp.coffee.domain.subscription.repository;

import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionUsageHistoryRepository extends JpaRepository<SubscriptionUsageHistory, Long> {
    @Query("""
        SELECT uh
        FROM SubscriptionUsageHistory uh
        WHERE uh.memberSubscription.memberSubscriptionId IN :memberSubscriptionIds
    """)
    List<SubscriptionUsageHistory> findByMemberSubscriptionIds(@Param("memberSubscriptionIds") List<Long> memberSubscriptionIds);
}
