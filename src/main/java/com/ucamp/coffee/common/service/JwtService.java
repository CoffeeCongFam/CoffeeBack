package com.ucamp.coffee.common.service;

import com.ucamp.coffee.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenProvider jwtTokenProvider;

    public String refreshAccessToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 액세스 토큰입니다.");
        }

        Long memberId = Long.parseLong(jwtTokenProvider.getClaims(refreshToken).getSubject());
        return jwtTokenProvider.generateAccessToken(memberId);
    }
}
