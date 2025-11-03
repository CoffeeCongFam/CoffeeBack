package com.ucamp.coffee.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ucamp.coffee.domain.member.dto.MemberDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.member.type.ActiveStatusType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;

	public Member save(MemberDto memberDto) {
		Member member = Member.builder()
						.email(memberDto.getEmail())
						.tel(memberDto.getTel())
						.gender(memberDto.getGender())
						.name(memberDto.getName())
						.memberType(memberDto.getMemberType())
						.activeStatus(ActiveStatusType.ACTIVE)
						.build();
		
		return memberRepository.save(member);
	}

	public Member findById(Long memberId) {
		return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
	}

}
