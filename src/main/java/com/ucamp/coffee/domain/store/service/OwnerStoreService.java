package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.store.dto.OwnerStoreResponseDto;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.dto.StoreUpdateDto;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.mapper.StoreMapper;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerStoreService {
    private final StoreRepository repository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createStoreInfo(StoreCreateDto dto) {
        String email = "user1@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        repository.save(StoreMapper.toEntity(dto, member));
    }

    public OwnerStoreResponseDto readStoreInfo() {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Store store = repository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));

        List<StoreHours> results = repository.findStoreDetails(store.getPartnerStoreId());

        if (results.isEmpty()) return null;

        return StoreMapper.toOwnerStoreResponseDto(results, store, member);
    }

    @Transactional
    public void updateStoreInfo(Long partnerStoreId, StoreUpdateDto dto) {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // TODO: 점주 멤버의 전화번호 수정하기

        Store store = repository.findById(partnerStoreId)
            .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));

        store.update(dto);
    }

}
