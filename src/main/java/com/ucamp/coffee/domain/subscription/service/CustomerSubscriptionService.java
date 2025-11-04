package com.ucamp.coffee.domain.subscription.service;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDTO;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.service.StoreHelperService;
import com.ucamp.coffee.domain.subscription.dto.CustomerMemberSubscriptionResponseDTO;
import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDTO;
import com.ucamp.coffee.domain.subscription.entity.MemberSubscription;
import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionMenu;
import com.ucamp.coffee.domain.subscription.entity.SubscriptionUsageHistory;
import com.ucamp.coffee.domain.subscription.mapper.SubscriptionMapper;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionMenuRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionRepository;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionUsageHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerSubscriptionService {
    private final StoreHelperService storeHelperService;
    private final SubscriptionRepository repository;
    private final MemberRepository memberRepository;
    private final MemberSubscriptionRepository memberSubscriptionRepository;
    private final SubscriptionMenuRepository subscriptionMenuRepository;
    private final SubscriptionUsageHistoryRepository subscriptionUsageHistoryRepository;

    public List<CustomerSubscriptionResponseDTO> readSubscriptionList(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        return repository.findByStore(store)
            .stream()
            .map(subscription -> {
                CustomerStoreSimpleDTO storeDto = CustomerStoreSimpleDTO.builder()
                    .partnerStoreId(subscription.getStore().getPartnerStoreId())
                    .storeName(subscription.getStore().getStoreName())
                    .storeImg(subscription.getStore().getStoreImg())
                    .build();

                return SubscriptionMapper.toCustomerResponseDto(subscription, storeDto);
            })
            .collect(Collectors.toList());
    }

    // 매장 id 기반으로 매장 상세 정보 조회
    public List<CustomerSubscriptionResponseDTO> readSubscriptionListByStore(Store store) {
        return repository.findByStore(store)
            .stream()
            .map(subscription -> {
                CustomerStoreSimpleDTO storeDto = CustomerStoreSimpleDTO.builder()
                    .partnerStoreId(subscription.getStore().getPartnerStoreId())
                    .storeName(subscription.getStore().getStoreName())
                    .storeImg(subscription.getStore().getStoreImg())
                    .build();

                return SubscriptionMapper.toCustomerResponseDto(subscription, storeDto);
            })
            .toList();
    }

    public List<CustomerMemberSubscriptionResponseDTO> readMemberSubscriptionList(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        List<MemberSubscription> subscriptions =
            memberSubscriptionRepository.findAllByMemberWithRelations(member);

        if (subscriptions.isEmpty()) return Collections.emptyList();

        List<Long> subscriptionIds = subscriptions.stream()
            .map(sub -> sub.getPurchase().getSubscription().getSubscriptionId())
            .toList();

        List<SubscriptionMenu> subscriptionMenus = subscriptionMenuRepository.findBySubscriptionsIds(subscriptionIds);
        Map<Long, List<Menu>> menusMap = subscriptionMenus.stream()
            .collect(Collectors.groupingBy(
                sm -> sm.getSubscription().getSubscriptionId(),
                Collectors.mapping(SubscriptionMenu::getMenu, Collectors.toList())
            ));

        List<Long> memberSubscriptionIds = subscriptions.stream()
            .map(MemberSubscription::getMemberSubscriptionId)
            .toList();
        List<SubscriptionUsageHistory> usageHistories =
            subscriptionUsageHistoryRepository.findByMemberSubscriptionIds(memberSubscriptionIds);
        Map<Long, List<SubscriptionUsageHistory>> usageMap = usageHistories.stream()
            .collect(Collectors.groupingBy(
                uh -> uh.getMemberSubscription().getMemberSubscriptionId()
            ));

        return subscriptions.stream()
            .map(sub -> {
                Subscription subscription = sub.getPurchase().getSubscription();
                List<Menu> menus = menusMap.getOrDefault(subscription.getSubscriptionId(), Collections.emptyList());
                List<SubscriptionUsageHistory> histories = usageMap.getOrDefault(sub.getMemberSubscriptionId(), Collections.emptyList());
                return SubscriptionMapper.toCustomerMemberResponseDto(sub, menus, histories);
            })
            .toList();
    }
}
