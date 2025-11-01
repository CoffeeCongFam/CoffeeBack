package com.ucamp.coffee.domain.subscription.entity;

import java.time.LocalDateTime;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.purchase.entity.Purchase;

import com.ucamp.coffee.domain.subscription.type.UsageStatusType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "MEMBER_SUBSCRIPTION")
public class MemberSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberSubscriptionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
    
    private String isGift;

    @Enumerated(EnumType.STRING)
    private UsageStatusType usageStatus;
    
    private Integer dailyRemainCount;
    
    private LocalDateTime subscriptionStart;
    private LocalDateTime subscriptionEnd;
    
    private String isAutoPayment;
}
