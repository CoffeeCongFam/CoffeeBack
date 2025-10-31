package com.ucamp.coffee.domain.store.service.helper;

import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreHelperService {
    private final StoreRepository repository;

    public Store findById(Long partnerStoreId) {
        return repository.findById(partnerStoreId).orElse(null);
    }
}
