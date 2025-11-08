package com.ucamp.coffee.domain.subscription.repository;

import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionMenuRepository extends JpaRepository<SubscriptionMenu, Long> {
    List<SubscriptionMenu> findBySubscription(Subscription subscription);

    @Query("""
        SELECT sm
        FROM SubscriptionMenu sm
        JOIN FETCH sm.menu m
        WHERE sm.subscription.subscriptionId IN :subscriptionIds
    """)
    List<SubscriptionMenu> findBySubscriptionsIds(@Param("subscriptionIds") List<Long> subscriptionIds);;

    boolean existsByMenu_MenuId(Long menuId);
}
