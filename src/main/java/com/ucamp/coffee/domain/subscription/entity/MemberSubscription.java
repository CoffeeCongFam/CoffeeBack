package com.ucamp.coffee.domain.subscription.entity;

import java.time.LocalDateTime;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.purchase.entity.Purchase;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    private String isExpired;
    
    private Integer dailyRemainCount;
    
    private LocalDateTime subscriptionStart;
    private LocalDateTime subscriptionEnd;
    
    private String isAutoPayment;
    
    
    
    
}
