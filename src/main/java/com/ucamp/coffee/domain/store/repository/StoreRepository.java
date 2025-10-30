package com.ucamp.coffee.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.store.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

}
