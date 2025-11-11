package com.ucamp.coffee.domain.store.mapper;

import com.ucamp.coffee.common.util.DateTimeUtil;
import com.ucamp.coffee.domain.store.dto.MenuCreateDTO;
import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.type.MenuType;

public class MenuMapper {
    public static Menu toEntity(MenuCreateDTO dto, Store store, String image) {
        return Menu.builder()
            .store(store)
            .menuType(MenuType.valueOf(dto.getMenuType()))
            .menuName(dto.getMenuName())
            .price(dto.getPrice())
            .menuImg(image)
            .menuDesc(dto.getMenuDesc())
            .menuStatus(dto.getMenuStatus())
            .build();
    }

    public static MenuResponseDTO toDto(Menu menu, boolean isUpdatable) {
        return MenuResponseDTO.builder()
            .menuId(menu.getMenuId())
            .partnerStoreId(menu.getStore().getPartnerStoreId())
            .menuType(menu.getMenuType().name())
            .menuName(menu.getMenuName())
            .price(menu.getPrice())
            .menuImg(menu.getMenuImg())
            .menuDesc(menu.getMenuDesc())
            .menuStatus(menu.getMenuStatus())
            .createdAt(DateTimeUtil.toUtcDateTime(menu.getCreatedAt()))
            .updatedAt(DateTimeUtil.toUtcDateTime(menu.getUpdatedAt()))
            .deletedAt(DateTimeUtil.toUtcDateTime(menu.getDeletedAt()))
            .isUpdatable(isUpdatable)
            .build();
    }
}
