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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CustomerStoreService {
    private final StoreHelperService helperService;
    private final MenuService menuService;
    private final CustomerSubscriptionService customerSubscriptionService;
    private final StoreRepository repository;
    private final MemberSubscriptionRepository memberSubscriptionRepository;
    private final ReviewRepository reviewRepository;
    private final StoreHoursRepository storeHoursRepository;

    public List<CustomerStoreListResponseDTO> readStoreList() {
        List<Store> stores = repository.findAll(); // 매장 목록 조회
        List<Long> storeIds = stores.stream().map(Store::getPartnerStoreId).toList(); // 매장 아이디 목록 추출

        Map<Long, Integer> subscriberCounts = getSubscriberCounts(storeIds);        // 각 스토어 ID별 구독자 수를 조회하여 Map으로 저장
        Map<Long, Integer> subscriptionStocks = getSubscriptionStocks(storeIds);    // 각 스토어 ID별 구독 상품 재고 수를 조회하여 Map으로 저장
        Map<Long, Long> reviewCounts = getReviewCounts(storeIds);                   // 각 스토어 ID별 리뷰 개수를 조회하여 Map으로 저장
        Map<Long, Double> averageRatings = getAverageRatings(storeIds);             // 각 스토어 ID별 평균 평점을 조회하여 Map으로 저장
        Map<Long, String> storeStatusMap = getStoreStatusMap(storeIds);             // 각 스토어 ID별 상태를 조회하여 Map으로 저장

        // stores 리스트를 스트림으로 변환
        return stores.stream()
            // 각 스토어를 DTO로 매핑
            // 구독자 수, 재고, 리뷰 개수, 평균 평점, 상태를 함께 전달
            .map(store -> mapToStoreListDTO(store, subscriberCounts, subscriptionStocks,
                reviewCounts, averageRatings, storeStatusMap, null))
            .toList();
    }

    public CustomerStoreResponseDTO readStoreInfo(Long partnerStoreId) {
        // 클라이언트에서 보낸 ID 기반으로 매장 정보 조회
        Store store = helperService.findById(partnerStoreId);

        List<StoreHours> storeHoursList = repository.findStoreDetailsWithStoreHours(partnerStoreId);    // 특정 매장의 영업시간 정보를 조회
        List<MenuResponseDTO> menus = menuService.readMenuListByStore(partnerStoreId);                  // 특정 매장의 메뉴 리스트를 조회
        List<CustomerSubscriptionResponseDTO> subscriptions =
            customerSubscriptionService.readSubscriptionListByStore(store);                             // 특정 매장을 기준으로 고객 구독 리스트를 조회

        // 매장 정보와 함께 영업시간 정보, 메뉴 리스트, 고객 구독 리스트를 하나의 DTO로 변환하여 반환
        return StoreMapper.toCustomerStoreDto(storeHoursList, store, menus, subscriptions);
    }

    public List<CustomerStoreListResponseDTO> readNearbyStores(Double xPoint, Double yPoint, Double radius, Long memberId) {
        // 클라이언트에서 보낸 좌표와 반경 범위(기본값 2km)를 기준으로 근처 매장 정보 목록 조회
        List<Store> nearbyStores = repository.findStoresWithinRadius(xPoint, yPoint, radius);
        List<Long> storeIds = nearbyStores.stream().map(Store::getPartnerStoreId).toList(); // 매장 아이디 목록 추출

        Map<Long, Integer> subscriberCounts = getSubscriberCounts(storeIds);        // 각 스토어 ID별 구독자 수를 조회하여 Map으로 저장
        Map<Long, Integer> subscriptionStocks = getSubscriptionStocks(storeIds);    // 각 스토어 ID별 구독 상품 재고 수를 조회하여 Map으로 저장
        Map<Long, Long> reviewCounts = getReviewCounts(storeIds);                   // 각 스토어 ID별 리뷰 개수를 조회하여 Map으로 저장
        Map<Long, Double> averageRatings = getAverageRatings(storeIds);             // 각 스토어 ID별 평균 평점을 조회하여 Map으로 저장
        Map<Long, String> storeStatusMap = getStoreStatusMap(storeIds);             // 각 스토어 ID별 상태를 조회하여 Map으로 저장

        // 특정 회원이 각 스토어에 구독 중인지 여부를 조회하고 Map으로 변환
        Map<Long, Boolean> isSubscribedMap = memberSubscriptionRepository.isSubscribedByMemberAndStoreIds(storeIds, memberId)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Boolean) row[1]
            ));

        // 구독자 수, 재고, 리뷰, 평균 평점, 상태, 회원 구독 여부 정보를 함께 전달
        return nearbyStores.stream()
            .map(store -> mapToStoreListDTO(store, subscriberCounts, subscriptionStocks,
                reviewCounts, averageRatings, storeStatusMap, isSubscribedMap))
            .toList();
    }

    private Map<Long, Integer> getSubscriberCounts(List<Long> storeIds) {
        // 각 스토어 ID별 구독자 수를 조회하여 Map으로 반환
        return memberSubscriptionRepository.countSubscribersByStoreIds(storeIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Long) row[1]).intValue()
            ));
    }

    private Map<Long, Integer> getSubscriptionStocks(List<Long> storeIds) {
        // 각 스토어 ID별 구독 상품 재고 수를 조회하여 Map으로 반환
        return memberSubscriptionRepository.getRemainingStockByStoreIds(storeIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> ((Long) row[1]).intValue()
            ));
    }

    private Map<Long, Long> getReviewCounts(List<Long> storeIds) {
        // 각 스토어 ID별 리뷰 개수를 조회하여 Map으로 반환
        return reviewRepository.countByStoreIds(storeIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Long) row[1]
            ));
    }

    private Map<Long, Double> getAverageRatings(List<Long> storeIds) {
        // 각 스토어 ID별 평균 평점을 조회하여 Map으로 반환
        return reviewRepository.averageRatingByStoreIds(storeIds)
            .stream()
            .collect(Collectors.toMap(
                row -> (Long) row[0],
                row -> (Double) row[1]
            ));
    }

    private Map<Long, String> getStoreStatusMap(List<Long> storeIds) {
        // 각 스토어 ID별 상태를 조회
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<StoreHours> todayHours = storeHoursRepository.findByStore_PartnerStoreIdInAndDayOfWeek(storeIds,
            DayOfWeekType.valueOf(today.name().substring(0, 3))
        );

        // 오늘 영업시간을 기반으로 스토어 상태를 Map으로 반환
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
        // 매장 정보 및 통계 정보들을 DTO로 매핑
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
