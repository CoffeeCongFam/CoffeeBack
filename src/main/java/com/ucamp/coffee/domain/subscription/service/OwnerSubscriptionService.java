package com.ucamp.coffee.domain.subscription.service;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.service.MenuHelperService;
import com.ucamp.coffee.domain.store.service.StoreHelperService;
import com.ucamp.coffee.domain.subscription.dto.OwnerSubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDto;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionStatusDto;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionMenu;
import com.ucamp.coffee.domain.subscription.mapper.SubscriptionMapper;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionMenuRepository;
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
public class OwnerSubscriptionService {
    private final StoreHelperService storeHelperService;
    private final MenuHelperService menuHelperService;
    private final SubscriptionRepository repository;
    private final MemberRepository memberRepository;
    private final SubscriptionMenuRepository subscriptionMenuRepository;

    @Transactional
    public void createSubscriptionInfo(SubscriptionCreateDto dto) {
        String email = "store1@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        Subscription subscription = repository.save(SubscriptionMapper.toEntity(dto, store));

        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            List<Menu> menus = menuHelperService.findByIds(dto.getMenuIds());

            List<SubscriptionMenu> subscriptionMenus = menus.stream()
                .map(menu -> SubscriptionMenu.builder()
                    .subscription(subscription)
                    .menu(menu)
                    .build())
                .toList();

            subscriptionMenuRepository.saveAll(subscriptionMenus);
        }
    }

    public List<OwnerSubscriptionResponseDto> readSubscriptionList() {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        return repository.findByStore(store)
            .stream()
            .map(SubscriptionMapper::toOwnerResponseDto)
            .collect(Collectors.toList());
    }

    public OwnerSubscriptionResponseDto readSubscriptionInfo(Long subscriptionId) {
        return SubscriptionMapper.toOwnerResponseDto(repository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다.")));
    }

    @Transactional
    public void updateSubscriptionStatus(Long subscriptionId, SubscriptionStatusDto dto) {
        String email = "user@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Subscription subscription = repository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));

        subscription.update(null, null, SubscriptionStatusType.valueOf(dto.getSubscriptionStatus()));
    }

    /* TODO: 구독권 삭제 시 구독권-메뉴 테이블 SOFT DELETE 처리 */
}
