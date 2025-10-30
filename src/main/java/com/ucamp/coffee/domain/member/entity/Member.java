package com.ucamp.coffee.domain.member.entity;

import com.ucamp.coffee.common.entity.BaseEntity;
import com.ucamp.coffee.domain.member.type.ActiveStatusType;
import com.ucamp.coffee.domain.member.type.GenderType;
import com.ucamp.coffee.domain.member.type.MemberType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "MEMBER")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 20)
    private String tel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private GenderType gender;

    @Column(nullable = false, length = 20)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MemberType memberType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActiveStatusType activeStatus;
}