package com.ucamp.coffee.domain.store.service.customer;

import com.ucamp.coffee.domain.store.dto.CustomerStoreResponseDto;
import com.ucamp.coffee.domain.store.dto.MenuResponseDto;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.entity.StoreHours;
import com.ucamp.coffee.domain.store.mapper.StoreMapper;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import com.ucamp.coffee.domain.store.service.MenuService;
import com.ucamp.coffee.domain.store.service.helper.StoreHelperService;
import com.ucamp.coffee.domain.subscription.dto.SubscriptionResponseDto;
import com.ucamp.coffee.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerStoreService {
    private final StoreHelperService helperService;
    private final MenuService menuService;
    private final SubscriptionService subscriptionService;
    private final StoreRepository repository;

    public CustomerStoreResponseDto readStoreInfoForCustomer(Long partnerStoreId) {
        Store store = helperService.findById(partnerStoreId);

        List<StoreHours> results = repository.findStoreDetails(partnerStoreId);
        List<MenuResponseDto> menus = menuService.readMenuListByStore(partnerStoreId);
        List<SubscriptionResponseDto> subscriptions = subscriptionService.readSubscriptionList(null);

        if (results.isEmpty()) return null;

        return StoreMapper.toCustomerStoreDto(results, store, menus, subscriptions);
    }
}
