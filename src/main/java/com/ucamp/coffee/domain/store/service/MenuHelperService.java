package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.mapper.MenuMapper;
import com.ucamp.coffee.domain.store.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuHelperService {
    private final StoreHelperService storeHelperService;
    private final MenuRepository repository;

    public List<Menu> findByIds(List<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) return Collections.emptyList();
        return repository.findAllById(menuIds);
    }

    public List<MenuResponseDTO> readMenuListByStore(Long partnerStoreId) {
        Store store = storeHelperService.findById(partnerStoreId);
        return repository.findMenuListWithStore(store)
            .stream()
            .map(MenuMapper::toDto)
            .toList();
    }
}
