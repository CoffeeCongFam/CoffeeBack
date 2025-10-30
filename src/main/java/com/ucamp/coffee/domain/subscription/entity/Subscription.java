package com.ucamp.coffee.domain.subscription.entity;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.store.entity.Store;
import com.ucamp.coffee.domain.subscription.type.SubscriptionStatusType;
import com.ucamp.coffee.domain.subscription.type.SubscriptionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "SUBSCRIPTION")
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_store_id", nullable = false)
    private Store store;

    @Column(nullable = false, length = 50)
    private String subscriptionName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, length = 100)
    private String subscriptionDesc;

    @Column(nullable = false)
    private Integer totalSale;

    @Column(length = 200)
    private String subscriptionImg;

    @Column(nullable = false)
    private Integer salesLimitQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", length = 50)
    private SubscriptionType subscriptionType;

    private Integer subscriptionPeriod;

    private Integer maxDailyUsage;

    private Integer remainSalesQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SubscriptionStatusType subscriptionStatus;
}