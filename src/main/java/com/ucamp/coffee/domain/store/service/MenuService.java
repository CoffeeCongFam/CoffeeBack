package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.domain.store.dto.MenuCreateDto;
import com.ucamp.coffee.domain.store.dto.MenuResponseDto;
import com.ucamp.coffee.domain.store.dto.MenuUpdateDto;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.mapper.MenuMapper;
import com.ucamp.coffee.domain.store.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {
    private final StoreHelperService storeHelperService;
    private final MenuRepository repository;

    @Transactional
    public void createMenuInfo(MenuCreateDto dto) {
        Store store = storeHelperService.findById(dto.getPartnerStoreId());
        repository.save(MenuMapper.toEntity(dto, store));
    }

    public List<MenuResponseDto> readMenuListByStore(Long partnerStoreId) {
        Store store = storeHelperService.findById(partnerStoreId);
        return repository.findMenuListWithStore(store)
            .stream()
            .map(MenuMapper::toDto)
            .toList();
    }

    public MenuResponseDto readMenuInfo(Long menuId) {
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        return MenuMapper.toDto(menu);
    }

    @Transactional
    public void updateMenuInfo(Long menuId, MenuUpdateDto dto) {
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        menu.update(dto);
    }

    @Transactional
    public void deleteMenuInfo(Long menuId) {
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        menu.setDeletedAt(LocalDateTime.now());
    }
}