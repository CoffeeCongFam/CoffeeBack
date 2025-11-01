package com.ucamp.coffee.domain.store.service;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreHelperService {
    private final StoreRepository repository;

    public Store findById(Long partnerStoreId) {
        return repository.findById(partnerStoreId).orElse(null);
    }

    public Optional<Store> findByMember(Member member) {
        return repository.findByMember(member);
    }
}
