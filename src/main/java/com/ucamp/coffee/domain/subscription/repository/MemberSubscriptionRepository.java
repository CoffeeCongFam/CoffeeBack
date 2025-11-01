package com.ucamp.coffee.domain.subscription.repository;

import java.util.Optional;

import com.ucamp.coffee.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;

import java.util.List;

@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, Long> {
    @Query("""
        SELECT ms
        FROM MemberSubscription ms
        JOIN FETCH ms.purchase p
        JOIN FETCH p.subscription s
        JOIN FETCH s.store st
        WHERE ms.member = :member
    """)
    List<MemberSubscription> findAllByMemberWithRelations(@Param("member") Member member);

    @Query("""
    SELECT o.store.partnerStoreId, COUNT(ms)
    FROM MemberSubscription ms
    JOIN Orders o ON o.memberSubscription = ms
    WHERE o.store.partnerStoreId IN :storeIds
    GROUP BY o.store.partnerStoreId
""")
    List<Object[]> countSubscribersByStoreIds(@Param("storeIds") List<Long> storeIds);

	//구매 ID로 보유 구독권 찾기
	Optional<MemberSubscription> findByPurchase_PurchaseId(Long purchaseId);

    @Query("""
    SELECT o.store.partnerStoreId, SUM(ms.dailyRemainCount)
    FROM MemberSubscription ms
    JOIN Orders o ON o.memberSubscription = ms
    WHERE o.store.partnerStoreId IN :storeIds
    GROUP BY o.store.partnerStoreId
""")
    List<Object[]> getRemainingStockByStoreIds(@Param("storeIds") List<Long> storeIds);

    @Query("""
    SELECT o.store.partnerStoreId, 
           CASE WHEN COUNT(ms) > 0 THEN true ELSE false END
    FROM MemberSubscription ms
    JOIN Orders o ON o.memberSubscription = ms
    WHERE o.store.partnerStoreId IN :storeIds
    AND ms.member.memberId = :memberId
    GROUP BY o.store.partnerStoreId
""")
    List<Object[]> isSubscribedByMemberAndStoreIds(@Param("storeIds") List<Long> storeIds,
                                                   @Param("memberId") Long memberId);
}
