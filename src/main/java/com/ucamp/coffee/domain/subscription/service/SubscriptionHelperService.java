package com.ucamp.coffee.domain.subscription.service;

import com.ucamp.coffee.domain.subscription.entity.Subscription;
import com.ucamp.coffee.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubscriptionHelperService {
    private final SubscriptionRepository repository;

    public Optional<Subscription> findById(Long subscriptionId) {
        return repository.findById(subscriptionId);
    }
}
