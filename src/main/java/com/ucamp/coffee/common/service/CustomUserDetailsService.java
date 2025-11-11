package com.ucamp.coffee.common.service;

import com.ucamp.coffee.common.entity.CustomUserDetails;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class CustomUserDetailsService {
    private final MemberRepository repository;

    @Bean
    public UserDetailsService repository() {
        return username -> repository.findByEmail(username)
            .map(member -> new CustomUserDetails(member.getMemberId()))
            .orElseThrow(() -> new UsernameNotFoundException("해당 사용자가 존재하지 않습니다."));
    }
}
