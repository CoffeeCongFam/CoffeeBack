package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.common.service.OciObjectStorageService;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.MemberHelperService;
import com.ucamp.coffee.domain.store.dto.OwnerStoreResponseDTO;
import com.ucamp.coffee.domain.store.dto.StoreCreateDTO;
import com.ucamp.coffee.domain.store.dto.StoreUpdateDTO;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.mapper.StoreMapper;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerStoreService {
    private final StoreRepository repository;
    private final MemberHelperService memberHelperService;
    private final OciObjectStorageService ociObjectStorageService;

    @Transactional
    public void createStoreInfo(StoreCreateDTO dto, MultipartFile file, Long memberId) {
        // 멤버 정보 조회 및 저장
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 이미지 스토리지에 저장
        String imageUrl = null;
        if (file != null && !file.isEmpty()) imageUrl = ociObjectStorageService.uploadFile(file);

        repository.save(StoreMapper.toEntity(dto, member, imageUrl));
    }

    public OwnerStoreResponseDTO readStoreInfo(Long memberId) {
        // 점주 정보 조회 및 해당 점주의 매장 조회
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Store store = repository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));

        // 패치 조인을 통해 매장 및 운영시간 목록 조회
        List<StoreHours> results = repository.findStoreDetailsWithStoreHours(store.getPartnerStoreId());

        return StoreMapper.toOwnerStoreResponseDto(results, store, member);
    }

    @Transactional
    public void updateStoreInfo(Long partnerStoreId, StoreUpdateDTO dto, Long memberId, MultipartFile file) {
        // 점주 정보 조회
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // 이미지 스토리지에 저장
        String imageUrl = null;
        if (file != null && !file.isEmpty()) imageUrl = ociObjectStorageService.uploadFile(file);

        member.setTel(dto.getTel()); // 점주 전화번호 수정

        // 매장 정보 조회 및 수정
        Store store = repository.findById(partnerStoreId)
            .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));
        store.update(dto, imageUrl);
    }
}
