package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.common.service.FileStorageService;
import com.ucamp.coffee.domain.store.dto.MenuCreateDTO;
import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
import com.ucamp.coffee.domain.store.dto.MenuUpdateDTO;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.mapper.MenuMapper;
import com.ucamp.coffee.domain.store.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {
    private final StoreHelperService storeHelperService;
    private final MenuRepository repository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void createMenuInfo(MenuCreateDTO dto, MultipartFile file) {
        Store store = storeHelperService.findById(dto.getPartnerStoreId());

        String imageUrl = null;
        if (file != null && !file.isEmpty()) imageUrl = fileStorageService.save(file);

        repository.save(MenuMapper.toEntity(dto, store, imageUrl));
    }

    public List<MenuResponseDTO> readMenuListByStore(Long partnerStoreId) {
        Store store = storeHelperService.findById(partnerStoreId);
        return repository.findMenuListWithStore(store)
            .stream()
            .map(MenuMapper::toDto)
            .toList();
    }

    public MenuResponseDTO readMenuInfo(Long menuId) {
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        return MenuMapper.toDto(menu);
    }

    @Transactional
    public void updateMenuInfo(Long menuId, MenuUpdateDTO dto, MultipartFile file) {
        String imageUrl = null;
        if (file != null && !file.isEmpty()) imageUrl = fileStorageService.save(file);

        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        menu.update(dto, imageUrl);
    }

    @Transactional
    public void deleteMenuInfo(Long menuId) {
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        menu.setDeletedAt(LocalDateTime.now());
    }
}