package com.ucamp.coffee.domain.member.service;

import com.ucamp.coffee.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberHelperService {
    private MemberRepository repository;


}
