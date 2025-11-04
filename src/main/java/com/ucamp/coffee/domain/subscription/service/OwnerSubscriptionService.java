package com.ucamp.coffee.domain.subscription.service;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.common.service.FileStorageService;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.MemberHelperService;
import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.mapper.MenuMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OwnerSubscriptionService {
    private final StoreHelperService storeHelperService;
    private final MenuHelperService menuHelperService;
    private final SubscriptionRepository repository;
    private final MemberHelperService memberHelperService;
    private final SubscriptionMenuRepository subscriptionMenuRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void createSubscriptionInfo(SubscriptionCreateDTO dto, MultipartFile file, Long memberId) {
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        String imageUrl = null;
        if (file != null && !file.isEmpty()) imageUrl = fileStorageService.save(file);

        Subscription subscription = repository.save(SubscriptionMapper.toEntity(dto, store, imageUrl));

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

        List<Subscription> subscriptions = repository.findByStore(store);

        return subscriptions.stream()
            .map(subscription -> {
                // SubscriptionMenu 조회
                List<SubscriptionMenu> subscriptionMenus = subscriptionMenuRepository.findBySubscription(subscription);

                List<MenuResponseDTO> menus = subscriptionMenus.stream()
                    .map(SubscriptionMenu::getMenu)
                    .map(MenuMapper::toDto)
                    .collect(Collectors.toList());

                // DTO 생성
                return OwnerSubscriptionResponseDTO.builder()
                    .subscriptionId(subscription.getSubscriptionId())
                    .partnerStoreId(store.getPartnerStoreId())
                    .storeName(store.getStoreName())
                    .subscriptionName(subscription.getSubscriptionName())
                    .price(subscription.getPrice())
                    .subscriptionDesc(subscription.getSubscriptionDesc())
                    .totalSale(subscription.getTotalSale())
                    .subscriptionImg(subscription.getSubscriptionImg())
                    .salesLimitQuantity(subscription.getSalesLimitQuantity())
                    .subscriptionType(subscription.getSubscriptionType() != null ? subscription.getSubscriptionType().name() : null)
                    .subscriptionPeriod(subscription.getSubscriptionPeriod())
                    .maxDailyUsage(subscription.getMaxDailyUsage())
                    .remainSalesQuantity(subscription.getRemainSalesQuantity())
                    .subscriptionStatus(subscription.getSubscriptionStatus() != null ? subscription.getSubscriptionStatus().name() : null)
                    .menus(menus)
                    .build();
            })
            .collect(Collectors.toList());
    }

    public OwnerSubscriptionResponseDTO readSubscriptionInfo(Long subscriptionId) {
        Subscription subscription = repository.findByIdWithStore(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));

        // SubscriptionMenu 조회
        List<SubscriptionMenu> subscriptionMenus = subscriptionMenuRepository.findBySubscription(subscription);
        List<MenuResponseDTO> menus = subscriptionMenus.stream()
                .map(SubscriptionMenu::getMenu)
                .map(MenuMapper::toDto)
                .toList();

        // 메뉴까지 포함해서 DTO 생성
        return SubscriptionMapper.toOwnerResponseDto(subscription, menus);
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
