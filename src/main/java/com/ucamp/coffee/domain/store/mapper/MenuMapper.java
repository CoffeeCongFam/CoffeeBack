package com.ucamp.coffee.domain.store.mapper;

import com.ucamp.coffee.domain.store.dto.MenuCreateDto;
import com.ucamp.coffee.domain.store.dto.MenuResponseDto;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.type.MenuType;

public class MenuMapper {
    public static Menu toEntity(MenuCreateDto dto, Store store) {
        return Menu.builder()
            .store(store)
            .menuType(MenuType.valueOf(dto.getMenuType()))
            .menuName(dto.getMenuName())
            .price(dto.getPrice())
            .menuImg(dto.getMenuImg())
            .menuDesc(dto.getMenuDesc())
            .menuStatus(dto.getMenuStatus())
            .build();
    }

    public static MenuResponseDto toDto(Menu menu) {
        return MenuResponseDto.builder()
            .menuId(menu.getMenuId())
            .partnerStoreId(menu.getStore().getPartnerStoreId())
            .menuType(menu.getMenuType().name())
            .menuName(menu.getMenuName())
            .price(menu.getPrice())
            .menuImg(menu.getMenuImg())
            .menuDesc(menu.getMenuDesc())
            .menuStatus(menu.getMenuStatus())
            .createdAt(menu.getCreatedAt())
            .updatedAt(menu.getUpdatedAt())
            .build();
    }
}
