package com.ucamp.coffee.domain.subscription.service;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDto;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionStatusDto;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.mapper.SubscriptionMapper;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionRepository;
import com.ucamp.coffee.domain.subscription.type.SubscriptionStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository repository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void createSubscriptionInfo(String accessToken, SubscriptionCreateDto dto) {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeRepository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        repository.save(SubscriptionMapper.toEntity(dto, store));
    }

    public List<SubscriptionResponseDto> readSubscriptionList(String accessToken) {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeRepository.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        return repository.findByStore(store)
            .stream()
            .map(SubscriptionMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    public SubscriptionResponseDto readSubscriptionInfo(Long subscriptionId) {
        return SubscriptionMapper.toResponseDto(repository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다.")));
    }

    @Transactional
    public void updateSubscriptionStatus(String accessToken, Long subscriptionId, SubscriptionStatusDto dto) {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Subscription subscription = repository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));

        subscription.update(null, null, SubscriptionStatusType.valueOf(dto.getSubscriptionStatus()));
    }
}
