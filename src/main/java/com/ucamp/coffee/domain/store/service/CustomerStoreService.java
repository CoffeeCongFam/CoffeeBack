package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.domain.review.repository.ReviewRepository;
import com.ucamp.coffee.domain.store.dto.CustomerStoreNearByResponseDto;
import com.ucamp.coffee.domain.store.dto.CustomerStoreResponseDto;
import com.ucamp.coffee.domain.store.dto.MenuResponseDto;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.mapper.StoreMapper;
import com.ucamp.coffee.domain.store.repository.StoreHoursRepository;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import com.ucamp.coffee.domain.store.type.DayOfWeekType;
import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.repository.MemberSubscriptionRepository;
import com.ucamp.coffee.domain.subscription.service.CustomerSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerStoreService {
    private final StoreHelperService helperService;
    private final MenuService menuService;
    private final CustomerSubscriptionService customerSubscriptionService;
    private final StoreRepository repository;
    private final MemberSubscriptionRepository memberSubscriptionRepository;
    private final ReviewRepository reviewRepository;
    private final StoreHoursRepository storeHoursRepository;

    public CustomerStoreResponseDto readStoreInfo(Long partnerStoreId) {
        Store store = helperService.findById(partnerStoreId);

        List<StoreHours> results = repository.findStoreDetails(partnerStoreId);
        List<MenuResponseDto> menus = menuService.readMenuListByStore(partnerStoreId);
        List<CustomerSubscriptionResponseDto> subscriptions = customerSubscriptionService.readSubscriptionList();

        if (results.isEmpty()) return null;

        return StoreMapper.toCustomerStoreDto(results, store, menus, subscriptions);
    }

    public List<CustomerStoreNearByResponseDto> readNearbyStores(Double xPoint, Double yPoint, Double radius) {
        List<Store> nearbyStores = repository.findStoresWithinRadius(xPoint, yPoint, radius);

        List<Long> storeIds = nearbyStores.stream()
            .map(Store::getPartnerStoreId)
            .toList();

        List<Object[]> result = memberSubscriptionRepository.countSubscribersByStoreIds(storeIds);
        Map<Long, Integer> subscriberCounts = result.stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Long) row[1]).intValue()
            ));
        List<Object[]> stockResult = memberSubscriptionRepository.getRemainingStockByStoreIds(storeIds);
        Map<Long, Integer> subscriptionStocks = stockResult.stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Long) row[1]).intValue()
            ));

        List<Object[]> isSubResult = memberSubscriptionRepository.isSubscribedByMemberAndStoreIds(storeIds, 1L);
        Map<Long, Boolean> isSubscribedMap = isSubResult.stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Boolean) row[1]
            ));

        List<Object[]> countResult = reviewRepository.countByStoreIds(storeIds);
        Map<Long, Long> reviewCounts = countResult.stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Long) row[1]
            ));

        List<Object[]> avgResult = reviewRepository.averageRatingByStoreIds(storeIds);
        Map<Long, Double> averageRatings = avgResult.stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Double) row[1]
            ));

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<StoreHours> todayHours = storeHoursRepository.findByStore_PartnerStoreIdInAndDayOfWeek(storeIds, DayOfWeekType.valueOf(today.name().substring(0, 3)));

        Map<Long, String> storeStatusMap = todayHours.stream()
            .collect(Collectors.toMap(
                sh -> sh.getStore().getPartnerStoreId(),
                sh -> {
                    if ("Y".equals(sh.getIsClosed())) return "HOLIDAY";
                    LocalTime now = LocalTime.now();
                    LocalTime open = LocalTime.parse(sh.getOpenTime());
                    LocalTime close = LocalTime.parse(sh.getCloseTime());
                    return (now.isAfter(open) && now.isBefore(close)) ? "OPEN" : "CLOSED";
                }
            ));

        return nearbyStores.stream()
            .map(store -> CustomerStoreNearByResponseDto.builder()
                .storeId(store.getPartnerStoreId())
                .storeName(store.getStoreName())
                .storeStatus(storeStatusMap.getOrDefault(store.getPartnerStoreId(), "CLOSED"))
                .storeImage(store.getStoreImg())
                .roadAddress(store.getRoadAddress())
                .detailAddress(store.getDetailAddress())
                .xPoint(store.getXPoint())
                .yPoint(store.getYPoint())
                .subscriptionStock(subscriptionStocks.getOrDefault(store.getPartnerStoreId(), 0))
                .subscriberCount(subscriberCounts.getOrDefault(store.getPartnerStoreId(), 0))
                .isSubscribed(isSubscribedMap.getOrDefault(store.getPartnerStoreId(), false))
                .reviewCount(reviewCounts.getOrDefault(store.getPartnerStoreId(), 0L).intValue())
                .averageRating(averageRatings.getOrDefault(store.getPartnerStoreId(), 0.0))
                .build())
            .toList();
    }

}
