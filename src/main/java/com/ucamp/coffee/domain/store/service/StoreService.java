package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.common.service.KakaoService;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.dto.StoreResponseDto;
import com.ucamp.coffee.domain.store.dto.StoreUpdateDto;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.mapper.StoreMapper;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreService {
    private final KakaoService kakaoService;
    private final StoreRepository repository;
    private final MemberRepository memberRepository;

    // TODO: [메서드 공통] 카카오 OAuth 인증 기능 구현 후 이메일 잘 추출되는지 테스트 필요

    @Transactional
    public void createStoreInfo(String accessToken, StoreCreateDto dto) {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        repository.save(StoreMapper.toEntity(dto, member));
    }

    public StoreResponseDto readStoreInfo(String accessToken) {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        Store store = repository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));

        List<StoreHours> results = repository.findStoreDetails(store.getPartnerStoreId());

        if (results.isEmpty()) return null;

        return StoreMapper.toStoreResponseDto(results, store, member);
    }

    @Transactional
    public void updateStoreInfo(Long partnerStoreId, String accessToken, StoreUpdateDto dto) {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        // TODO: 점주 멤버의 전화번호 수정하기

        Store store = repository.findById(partnerStoreId)
            .orElseThrow(() -> new IllegalArgumentException("매장이 존재하지 않습니다."));

        store.update(dto);
    }
}
