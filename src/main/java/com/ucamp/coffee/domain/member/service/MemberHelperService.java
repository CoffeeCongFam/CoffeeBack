package com.ucamp.coffee.domain.member.service;

import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberHelperService {
    private final MemberRepository repository;

    public Optional<Member> findById(Long memberId) {
        return repository.findById(memberId);
    }
}
