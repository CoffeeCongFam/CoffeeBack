package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.domain.review.repository.ReviewRepository;
import com.ucamp.coffee.domain.store.dto.CustomerStoreListResponseDTO;
import com.ucamp.coffee.domain.store.dto.CustomerStoreResponseDTO;
import com.ucamp.coffee.domain.store.dto.MenuResponseDTO;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.mapper.StoreMapper;
import com.ucamp.coffee.domain.store.repository.StoreHoursRepository;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import com.ucamp.coffee.domain.store.type.DayOfWeekType;
import com.ucamp.coffee.domain.subscription.dto.CustomerSubscriptionResponseDTO;
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

    public List<CustomerStoreListResponseDTO> readStoreList() {
        List<Store> stores = repository.findAll();
        List<Long> storeIds = stores.stream().map(Store::getPartnerStoreId).toList();

        Map<Long, Integer> subscriberCounts = getSubscriberCounts(storeIds);
        Map<Long, Integer> subscriptionStocks = getSubscriptionStocks(storeIds);
        Map<Long, Long> reviewCounts = getReviewCounts(storeIds);
        Map<Long, Double> averageRatings = getAverageRatings(storeIds);
        Map<Long, String> storeStatusMap = getStoreStatusMap(storeIds);

        return stores.stream()
            .map(store -> mapToStoreListDTO(store, subscriberCounts, subscriptionStocks,
                reviewCounts, averageRatings, storeStatusMap, null))
            .toList();
    }

    public CustomerStoreResponseDTO readStoreInfo(Long partnerStoreId) {
        Store store = helperService.findById(partnerStoreId);

        List<StoreHours> results = repository.findStoreDetails(partnerStoreId);
        List<MenuResponseDTO> menus = menuService.readMenuListByStore(partnerStoreId);
        List<CustomerSubscriptionResponseDTO> subscriptions = customerSubscriptionService.readSubscriptionList(store.getMember().getMemberId());

        if (results.isEmpty()) return null;

        return StoreMapper.toCustomerStoreDto(results, store, menus, subscriptions);
    }

    public List<CustomerStoreListResponseDTO> readNearbyStores(Double xPoint, Double yPoint, Double radius, Long memberId) {
        List<Store> nearbyStores = repository.findStoresWithinRadius(xPoint, yPoint, radius);
        List<Long> storeIds = nearbyStores.stream().map(Store::getPartnerStoreId).toList();

        Map<Long, Integer> subscriberCounts = getSubscriberCounts(storeIds);
        Map<Long, Integer> subscriptionStocks = getSubscriptionStocks(storeIds);
        Map<Long, Long> reviewCounts = getReviewCounts(storeIds);
        Map<Long, Double> averageRatings = getAverageRatings(storeIds);
        Map<Long, String> storeStatusMap = getStoreStatusMap(storeIds);

        Map<Long, Boolean> isSubscribedMap = memberSubscriptionRepository.isSubscribedByMemberAndStoreIds(storeIds, memberId)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Boolean) row[1]
            ));

        return nearbyStores.stream()
            .map(store -> mapToStoreListDTO(store, subscriberCounts, subscriptionStocks,
                reviewCounts, averageRatings, storeStatusMap, isSubscribedMap))
            .toList();
    }

    private Map<Long, Integer> getSubscriberCounts(List<Long> storeIds) {
        return memberSubscriptionRepository.countSubscribersByStoreIds(storeIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Long) row[1]).intValue()
            ));
    }

    private Map<Long, Integer> getSubscriptionStocks(List<Long> storeIds) {
        return memberSubscriptionRepository.getRemainingStockByStoreIds(storeIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Long) row[1]).intValue()
            ));
    }

    private Map<Long, Long> getReviewCounts(List<Long> storeIds) {
        return reviewRepository.countByStoreIds(storeIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Long) row[1]
            ));
    }

    private Map<Long, Double> getAverageRatings(List<Long> storeIds) {
        return reviewRepository.averageRatingByStoreIds(storeIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Double) row[1]
            ));
    }

    private Map<Long, String> getStoreStatusMap(List<Long> storeIds) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<StoreHours> todayHours = storeHoursRepository.findByStore_PartnerStoreIdInAndDayOfWeek(storeIds,
            DayOfWeekType.valueOf(today.name().substring(0, 3))
        );

        return todayHours.stream()
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
    }

    private CustomerStoreListResponseDTO mapToStoreListDTO(
        Store store,
        Map<Long, Integer> subscriberCounts,
        Map<Long, Integer> subscriptionStocks,
        Map<Long, Long> reviewCounts,
        Map<Long, Double> averageRatings,
        Map<Long, String> storeStatusMap,
        Map<Long, Boolean> isSubscribedMap
    ) {
        return CustomerStoreListResponseDTO.builder()
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
            .reviewCount(reviewCounts.getOrDefault(store.getPartnerStoreId(), 0L).intValue())
            .averageRating(averageRatings.getOrDefault(store.getPartnerStoreId(), 0.0))
            .isSubscribed(isSubscribedMap == null ? null : isSubscribedMap.getOrDefault(store.getPartnerStoreId(), false))
            .build();
    }
}
