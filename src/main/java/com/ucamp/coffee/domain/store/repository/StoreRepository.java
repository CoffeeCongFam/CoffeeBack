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

    @Query(value = """
        SELECT *
        FROM partner_store s
        WHERE 6371 * 2 *
            ASIN(SQRT(
                POWER(SIN((:xPoint - s.x_point) * 3.141592653589793 / 180 / 2), 2) +
                COS(:xPoint * 3.141592653589793 / 180) * COS(s.x_point * 3.141592653589793 / 180) *
                POWER(SIN((:yPoint - s.y_point) * 3.141592653589793 / 180 / 2), 2)
            )) <= :radius
    """, nativeQuery = true)
    List<Store> findStoresWithinRadius(@Param("xPoint") Double xPoint,
                                       @Param("yPoint") Double yPoint,
                                       @Param("radius") Double radius);
}
