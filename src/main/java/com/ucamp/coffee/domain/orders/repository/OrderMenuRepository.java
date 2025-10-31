package com.ucamp.coffee.domain.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.orders.entity.OrderMenu;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

	
}
