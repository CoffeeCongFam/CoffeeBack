package com.ucamp.coffee.domain.review.repository;

import com.ucamp.coffee.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r.store.partnerStoreId, COUNT(r) FROM Review r WHERE r.store.partnerStoreId IN :storeIds GROUP BY r.store.partnerStoreId")
    List<Object[]> countByStoreIds(@Param("storeIds") List<Long> storeIds);

    @Query("SELECT r.store.partnerStoreId, AVG(r.rating) FROM Review r WHERE r.store.partnerStoreId IN :storeIds GROUP BY r.store.partnerStoreId")
    List<Object[]> averageRatingByStoreIds(@Param("storeIds") List<Long> storeIds);
}
