package com.ucamp.coffee.domain.subscription.repository;

import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;

@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, Long> {

	// 구매 ID로 보유 구독권 찾기
	Optional<MemberSubscription> findByPurchase_PurchaseId(Long purchaseId);

	@Query("""
			    SELECT ms
			    FROM MemberSubscription ms
			    JOIN FETCH ms.purchase p
			    JOIN FETCH p.subscription s
			    JOIN FETCH s.store st
			    WHERE ms.memberSubscriptionId = :id
			""")
	Optional<MemberSubscription> findSubscriptionById(@Param("id") Long id);
}
