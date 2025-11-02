package com.ucamp.coffee.domain.subscription.service;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.common.util.DateTimeUtil;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.store.dto.CustomerStoreSimpleDto;
import com.ucamp.coffee.domain.store.entity.Menu;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.service.StoreHelperService;
import com.ucamp.coffee.domain.subscription.dto.CustomerMemberSubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDto;
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

    public List<CustomerSubscriptionResponseDto> readSubscriptionList() {
        String email = "user1@example.com";
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        Store store = storeHelperService.findByMember(member)
            .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다."));

        return repository.findByStore(store)
            .stream()
            .map(subscription -> {
                CustomerStoreSimpleDto storeDto = CustomerStoreSimpleDto.builder()
                    .partnerStoreId(subscription.getStore().getPartnerStoreId())
                    .storeName(subscription.getStore().getStoreName())
                    .storeImg(subscription.getStore().getStoreImg())
                    .build();

                return SubscriptionMapper.toCustomerResponseDto(subscription, storeDto);
            })
            .collect(Collectors.toList());
    }

    public List<CustomerMemberSubscriptionResponseDto> readMemberSubscriptionList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CommonException(ApiStatus.UNAUTHORIZED);
        }

        Long memberId = Long.parseLong(authentication.getName());
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
