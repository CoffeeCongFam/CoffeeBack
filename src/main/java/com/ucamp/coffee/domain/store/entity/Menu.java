package com.ucamp.coffee.domain.store.entity;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.store.dto.MenuUpdateDTO;
import com.ucamp.coffee.domain.store.type.MenuType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "menu")
public class Menu extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long menuId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partner_store_id")
	private Store store;
	
	@Enumerated(EnumType.STRING)
	private MenuType menuType;
	
	private String menuName;
	
	private Integer price;
	
	private String menuImg;
	private String menuDesc;
	private String menuStatus;

    public void update(MenuUpdateDTO dto) {
        if (dto.getMenuName() != null) this.menuName = dto.getMenuName();
        if (dto.getPrice() != null) this.price = dto.getPrice();
        if (dto.getMenuImg() != null) this.menuImg = dto.getMenuImg();
        if (dto.getMenuDesc() != null) this.menuDesc = dto.getMenuDesc();
        if (dto.getMenuType() != null) this.menuType = MenuType.valueOf(dto.getMenuType());
        if (dto.getMenuStatus() != null) this.menuStatus = dto.getMenuStatus();
    }
}
