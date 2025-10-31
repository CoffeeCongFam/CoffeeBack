package com.ucamp.coffee.domain.store.repository;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("""
        SELECT sh
        FROM StoreHours sh
        JOIN FETCH sh.store s
        WHERE s.partnerStoreId = :storeId
    """)
    List<StoreHours> findStoreDetails(@Param("storeId") Long storeId);

    Optional<Store> findByMember(Member member);
}
