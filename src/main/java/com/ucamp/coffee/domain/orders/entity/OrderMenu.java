package com.ucamp.coffee.domain.orders.entity;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.store.entity.Menu;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "order_menu")
public class OrderMenu extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderMenuId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id")
	private Menu menu;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Orders orders;
	
	private Integer quantity;
}
