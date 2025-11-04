package com.ucamp.coffee.domain.subscription.service;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.MemberHelperService;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.service.MenuHelperService;
import com.ucamp.coffee.domain.store.service.StoreHelperService;
import com.ucamp.coffee.domain.subscription.dto.OwnerSubscriptionResponseDTO;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionCreateDTO;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionStatusDTO;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionMenu;
import com.ucamp.coffee.domain.subscription.mapper.SubscriptionMapper;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionMenuRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionRepository;
import com.ucamp.coffee.domain.subscription.type.SubscriptionStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerSubscriptionService {
    private final StoreHelperService storeHelperService;
    private final MenuHelperService menuHelperService;
    private final SubscriptionRepository repository;
    private final MemberHelperService memberHelperService;
    private final SubscriptionMenuRepository subscriptionMenuRepository;

    @Transactional
    public void createSubscriptionInfo(SubscriptionCreateDTO dto, Long memberId) {
        Member member = memberHelperService.findById(memberId)
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

    public List<OwnerSubscriptionResponseDTO> readSubscriptionList(Long memberId) {
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        return repository.findByStore(store)
            .stream()
            .map(SubscriptionMapper::toOwnerResponseDto)
            .collect(Collectors.toList());
    }

    public OwnerSubscriptionResponseDTO readSubscriptionInfo(Long subscriptionId) {
        return SubscriptionMapper.toOwnerResponseDto(repository.findByIdWithStore(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다.")));
    }

    @Transactional
    public void updateSubscriptionStatus(Long subscriptionId, SubscriptionStatusDTO dto, Long memberId) {
        Member member = memberHelperService.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Store store = storeHelperService.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        if (!Objects.equals(store.getMember().getMemberId(), memberId)) throw new CommonException(ApiStatus.UNAUTHORIZED);

        Subscription subscription = repository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));

        subscription.update(null, null, SubscriptionStatusType.valueOf(dto.getSubscriptionStatus()));
    }

    @Transactional
    public void deleteSubscriptionInfo(Long subscriptionId, Long memberId) {
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        if (!Objects.equals(store.getMember().getMemberId(), memberId)) throw new CommonException(ApiStatus.UNAUTHORIZED);

        Subscription subscription = repository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));

        subscription.setDeletedAt(LocalDateTime.now());
    }
}
