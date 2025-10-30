package com.ucamp.coffee.domain.orders.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ucamp.coffee.domain.orders.entity.OrderMenu;
import com.ucamp.coffee.domain.orders.entity.Orders;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

	//특정 주문ID에 속한 모든 메뉴 가져오기
	List<OrderMenu> findAllByOrders(Orders order);
}
