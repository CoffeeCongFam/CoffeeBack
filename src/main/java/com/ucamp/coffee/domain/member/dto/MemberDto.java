package com.ucamp.coffee.domain.member.dto;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.type.ActiveStatusType;
import com.ucamp.coffee.domain.member.type.GenderType;
import com.ucamp.coffee.domain.member.type.MemberType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDto {
	private Long memberId;
	private String email;
	private String tel;
	private GenderType gender;
	private String name;
	private MemberType memberType;
	private ActiveStatusType activeStatus;

    // 점주용 : memebrId로 제휴매장ID 가져오기
    private Long partnerStoreId;

    public MemberDto(Member member){
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.tel = member.getTel();
        this.gender = member.getGender();
        this.name = member.getName();
        this.memberType = member.getMemberType();
        this.activeStatus = member.getActiveStatus();
    }
}
