package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.common.service.OciObjectStorageService;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {
    private final StoreHelperService storeHelperService;
    private final MenuRepository repository;
    private final OciObjectStorageService ociObjectStorageService;

    @Transactional
    public void createMenuInfo(MenuCreateDTO dto, MultipartFile file) throws IOException {
        // 매장 아이디를 통해 매장 정보 조회
        Store store = storeHelperService.findById(dto.getPartnerStoreId());

        // 이미지 스토리지(현재 로컬)에 저장
        String imageUrl = null;
        if (file != null && !file.isEmpty()) imageUrl = ociObjectStorageService.uploadFile(file);

        // 메뉴 데이터 저장
        repository.save(MenuMapper.toEntity(dto, store, imageUrl));
    }

    public List<MenuResponseDTO> readMenuListByStore(Long partnerStoreId) {
        // 매장 아이디를 통해 매장 정보 조회
        Store store = storeHelperService.findById(partnerStoreId);

        // 해당 매장의 메뉴 목록을 조회하여 DTO 목록으로 반환
        return repository.findMenuListWithStore(store)
            .stream()
            .map(MenuMapper::toDto)
            .toList();
    }

    public MenuResponseDTO readMenuInfo(Long menuId) {
        // 메뉴 아이디를 통해 메뉴 정보 조회 및 DTO 반환
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        return MenuMapper.toDto(menu);
    }

    @Transactional
    public void updateMenuInfo(Long menuId, MenuUpdateDTO dto, MultipartFile file) throws IOException {
        // 이미지 스토리지(현재 로컬)에 저장
        String imageUrl = null;
        if (file != null && !file.isEmpty()) imageUrl = ociObjectStorageService.uploadFile(file);

        // 메뉴 아이디를 통해 메뉴 조회
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        menu.update(dto, imageUrl); // 더티체킹을 통해 메뉴 정보 수정
    }

    @Transactional
    public void deleteMenuInfo(Long menuId) {
        // 메뉴 정보 조회
        Menu menu = repository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("해당 메뉴가 존재하지 않습니다."));
        menu.setDeletedAt(LocalDateTime.now());
    }
}