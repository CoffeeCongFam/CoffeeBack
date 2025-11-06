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
        // 받은 메뉴 아이디 목록이 없다면 빈 배열 반환
        if (menuIds == null || menuIds.isEmpty()) return Collections.emptyList();

        // 있다면 해당하는 메뉴 목록 조회
        return repository.findAllById(menuIds);
    }

    public List<MenuResponseDTO> readMenuListByStore(Long partnerStoreId) {
        // 받은 매장 아이디를 통해 매장 정보 조회
        Store store = storeHelperService.findById(partnerStoreId);

        // 해당 매장의 메뉴 목록 조회 및 DTO 목록 반환
        return repository.findMenuListWithStore(store)
            .stream()
            .map(MenuMapper::toDto)
            .toList();
    }
}
