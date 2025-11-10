package com.ucamp.coffee.domain.subscription.service;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.common.service.OciUploaderService;
import com.ucamp.coffee.common.util.DateTimeUtil;
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
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionMenuRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionRepository;
import com.ucamp.coffee.domain.subscription.type.SubscriptionStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final OciUploaderService ociUploaderService;
    private final MemberSubscriptionRepository memberSubscriptionRepository;

    @Transactional
    public void createSubscriptionInfo(SubscriptionCreateDTO dto, MultipartFile file, Long memberId) throws IOException {
        // 점주 및 매장 정보 조회
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        // 이미지 파일 업로드
        String imageUrl = null;
        try {
            imageUrl = ociUploaderService.uploadSafely(file);
        } catch (Exception e) {
            imageUrl = "";
        }

        // 구독권 정보 등록
        Subscription subscription = repository.save(SubscriptionMapper.toEntity(dto, store, imageUrl));

        // 메뉴 아이디 목록이 존재한다면 구독권-메뉴 목록 등록
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
        // 점주 및 매장 정보 조회
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        // 매장별 구독권 목록 조회
        List<Subscription> subscriptions = repository.findByStore(store);

        return subscriptions.stream()
            .map(subscription -> {
                // 구독권-메뉴 목록 조회
                List<SubscriptionMenu> subscriptionMenus = subscriptionMenuRepository.findBySubscription(subscription);

                // 메뉴 목록 추출
                List<MenuResponseDTO> menus = subscriptionMenus.stream()
                    .map(SubscriptionMenu::getMenu)
                    .map(menu -> {
                        boolean isUpdatable = !subscriptionMenuRepository.existsByMenu_MenuIdAndSubscription_SubscriptionStatus(menu.getMenuId(), SubscriptionStatusType.ONSALE);
                        return MenuMapper.toDto(menu, isUpdatable);
                    })
                    .collect(Collectors.toList());

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
                    .deletedAt(DateTimeUtil.toUtcDateTime(subscription.getDeletedAt()))
                    .expiredAt(DateTimeUtil.toUtcDateTime(memberSubscriptionRepository.findLatestSubscriptionEnd(subscription.getSubscriptionId())))
                    .build();
            })
            .collect(Collectors.toList());
    }

    public OwnerSubscriptionResponseDTO readSubscriptionInfo(Long subscriptionId) {
        // 구독권 정보 조회
        Subscription subscription = repository.findByIdWithStore(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));

        // 구독권-메뉴 목록 조회
        List<SubscriptionMenu> subscriptionMenus = subscriptionMenuRepository.findBySubscription(subscription);
        
        // 메뉴 목록 추출
        List<MenuResponseDTO> menus = subscriptionMenus.stream()
            .map(SubscriptionMenu::getMenu)
            .map(menu -> {
                boolean isUpdatable = !subscriptionMenuRepository.existsByMenu_MenuIdAndSubscription_SubscriptionStatus(menu.getMenuId(), SubscriptionStatusType.ONSALE);
                return MenuMapper.toDto(menu, isUpdatable);
            })
            .toList();

        long count = memberSubscriptionRepository.countActiveSubscriptions(subscriptionId, LocalDateTime.now());
        LocalDateTime expiredAt = memberSubscriptionRepository.findLatestSubscriptionEnd(subscriptionId);
        return SubscriptionMapper.toOwnerResponseDto(subscription, menus, count <= 0, expiredAt);
    }

    @Transactional
    public void updateSubscriptionStatus(Long subscriptionId, SubscriptionStatusDTO dto, Long memberId) {
        // 점주 및 매장 정보 조회
        Member member = memberHelperService.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        // 해당 멤버가 매장의 점주가 아니라면 예외 처리
        if (!Objects.equals(store.getMember().getMemberId(), memberId)) throw new CommonException(ApiStatus.UNAUTHORIZED);

        // 구독권 정보 조회 및 수정
        Subscription subscription = repository.findById(subscriptionId)
            .orElseThrow(() -> new IllegalArgumentException("해당 구독권이 존재하지 않습니다."));
        subscription.update(null, null, SubscriptionStatusType.valueOf(dto.getSubscriptionStatus()));
    }
}
