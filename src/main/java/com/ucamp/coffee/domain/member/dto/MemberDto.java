package com.ucamp.coffee.domain.member.dto;

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
}
