package com.ucamp.coffee.domain.store.entity;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "PARTNER_STORE")
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerStoreId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 50)
    private String businessNumber;

    @Column(nullable = false, length = 100)
    private String storeName;

    @Column(nullable = false, length = 200)
    private String roadAddress;

    @Column(nullable = false, length = 200)
    private String detailAddress;

    @Column(nullable = false, length = 200)
    private String detailInfo;

    @Column(length = 200)
    private String storeImg;

    @Column(nullable = false, length = 30)
    private String storeTel;

    @Column(nullable = false)
    private Double xPoint;

    @Column(nullable = false)
    private Double yPoint;
}
