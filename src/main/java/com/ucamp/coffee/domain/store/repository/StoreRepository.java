package com.ucamp.coffee.domain.store.repository;

import com.ucamp.coffee.domain.store.entity.Store;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("""
            SELECT s, sh
            FROM Store s LEFT JOIN StoreHours sh
            ON sh.store = s
            WHERE s.partnerStoreId = :storeId
        """)
    List<Object[]> findStoreDetails(@Param("storeId") Long storeId);
}
