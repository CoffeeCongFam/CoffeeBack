package com.ucamp.coffee.common.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	
    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_VALID_TIME = 1000L * 60 * 60; // 1시간
    private static final long REFRESH_TOKEN_VALID_TIME = 1000L * 60 * 60 * 24 * 14; // 14일
    private static final long TEMP_TOKEN_VALID_TIME = 1000L * 60 * 10;   // 10분 (임시 토큰)

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 토큰 생성 공통 코드
    private String generateToken(Claims claims, long validity) {
        Date now = new Date();  // 현재 시각
        Date expiry = new Date(now.getTime() + validity);  // 만료 시각

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 비밀키 반환
                .compact();  // JWT를 문자열로 직렬화
    }

    // 서비스용 JWT 발급 (로그인 등)
    public String generateAccessToken(Long memberId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));
        claims.put("type", "ACCESS");

        return generateToken(claims, ACCESS_TOKEN_VALID_TIME);
    }

    // 리프레시 토큰 발급 (AccessToken 만료 시 재발급을 위해 사용되는 토큰)
    public String generateRefreshToken(Long memberId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));
        claims.put("type", "REFRESH");

        return generateToken(claims, REFRESH_TOKEN_VALID_TIME);
    }

    // 임시 토큰 (회원가입 추가정보 전달용)
    public String generateTempToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        claims.put("type", "TEMP"); // 구분용 클레임

        return generateToken(claims, TEMP_TOKEN_VALID_TIME);
    }



    // 토큰 파싱 (검증 포함)
    public Claims getClaims(String token) {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
        return jws.getBody();
    }

    // 유효성 검사
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
