package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.common.service.KakaoService;
import com.ucamp.coffee.common.util.DateTimeUtil;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.type.ActiveStatusType;
import com.ucamp.coffee.domain.member.type.GenderType;
import com.ucamp.coffee.domain.member.type.MemberType;
import com.ucamp.coffee.domain.store.dto.StoreCreateDto;
import com.ucamp.coffee.domain.store.dto.StoreHoursResponseDto;
import com.ucamp.coffee.domain.store.dto.StoreResponseDto;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.mapper.StoreMapper;
import com.ucamp.coffee.domain.store.repository.MemberRepository;
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
    // private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Transactional
    public void createMember() {
        memberRepository.save(Member.builder()
            .email("user@example.com")
            .tel("010-1234-5678")
            .gender(GenderType.F)
            .name("홍길동")
            .memberType(MemberType.STORE)
            .activeStatus(ActiveStatusType.ACTIVE)
            .build());
    }

    @Transactional
    public void createStoreInfo(StoreCreateDto dto, String accessToken) {
        // TODO: 카카오 OAuth 인증 기능 구현 후 이메일 잘 추출되는지 테스트 필요
        // String email = kakaoService.getEmailFromAccessToken(accessToken);
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email).orElse(null);

        // Member member = memberService.findByEmail(dto.getEmail()).orElse(null);

        repository.save(StoreMapper.toEntity(dto, member));
    }

    public StoreResponseDto readStoreInfo(String accessToken) {
        // TODO: 카카오 OAuth 인증 기능 구현 후 이메일 잘 추출되는지 테스트 필요
        // String email = kakaoService.getEmailFromAccessToken(accessToken);
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email).orElse(null);

        List<Object[]> results = repository.findStoreDetails(2L);

        if (results.isEmpty()) return null;

        Store store = (Store) results.get(0)[0];

        List<StoreHoursResponseDto> storeHoursDtos = results.stream()
            .map(
            row -> {
                StoreHours sh = (StoreHours) row[1];

                return new StoreHoursResponseDto(
                    sh.getDayOfWeek().name(),
                    DateTimeUtil.toUtcDateTime(sh.getOpenTime()),
                    DateTimeUtil.toUtcDateTime(sh.getCloseTime()),
                    sh.getIsClosed().name()
                );
            })
            .toList();

        return new StoreResponseDto(
            store.getStoreName(),
            store.getStoreTel(),
            member.getTel(),
            store.getRoadAddress(),
            store.getDetailAddress(),
            store.getBusinessNumber(),
            store.getDetailInfo(),
            storeHoursDtos
        );
    }
}
