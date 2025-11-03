package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerStoreService {
    private final StoreRepository repository;
    private final MemberHelperService memberHelperService;

    @Transactional
    public void createStoreInfo(StoreCreateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CommonException(ApiStatus.UNAUTHORIZED);
        }

        Long memberId = Long.parseLong(authentication.getName());
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        repository.save(StoreMapper.toEntity(dto, member));
    }

    public OwnerStoreResponseDTO readStoreInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CommonException(ApiStatus.UNAUTHORIZED);
        }

        Long memberId = Long.parseLong(authentication.getName());
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Store store = repository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));

        List<StoreHours> results = repository.findStoreDetails(store.getPartnerStoreId());

        if (results.isEmpty()) return null;

        return StoreMapper.toOwnerStoreResponseDto(results, store, member);
    }

    @Transactional
    public void updateStoreInfo(Long partnerStoreId, StoreUpdateDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CommonException(ApiStatus.UNAUTHORIZED);
        }

        Long memberId = Long.parseLong(authentication.getName());
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member.setTel(dto.getTel());

        Store store = repository.findById(partnerStoreId)
            .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));

        store.update(dto);
    }
}
