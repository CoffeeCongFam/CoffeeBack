package com.ucamp.coffee.domain.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.orders.entity.Orders;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {


	//스토어ID로 전체 주문 조회
	List<Orders> findAllByStore_PartnerStoreId(Long partnerStoreID);
}
