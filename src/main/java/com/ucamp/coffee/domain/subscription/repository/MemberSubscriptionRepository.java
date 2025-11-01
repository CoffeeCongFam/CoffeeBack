package com.ucamp.coffee.domain.subscription.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;

@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, Long> {

	//구매 ID로 보유 구독권 찾기
	Optional<MemberSubscription> findByPurchase_PurchaseId(Long purchaseId);

}
