package com.ucamp.coffee.domain.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;

@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, Long> {

}
