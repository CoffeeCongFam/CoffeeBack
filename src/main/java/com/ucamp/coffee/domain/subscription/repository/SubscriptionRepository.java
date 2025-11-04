package com.ucamp.coffee.domain.subscription.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByStore(Store store);
    
    @Query("""
    	      select s from Subscription s
    	      join fetch s.store
    	      where s.subscriptionId = :id
    	  """)
    	  Optional<Subscription> findByIdWithStore(@Param("id") Long id);
}
