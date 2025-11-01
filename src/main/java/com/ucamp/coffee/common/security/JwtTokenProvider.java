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

    private final long accessTokenValidTime = 1000L * 60 * 60; // 1시간
    private final long tempTokenValidTime = 1000L * 60 * 10;   // 10분 (임시 토큰)

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 서비스용 JWT 발급 (로그인 등)
    public String generateToken(Long memberId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(memberId));

        Date now = new Date();  // 현재 시각
        Date expiry = new Date(now.getTime() + accessTokenValidTime);  // 만료 시각

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 비밀키 반환
                .compact();  // JWT를 문자열로 직렬화
    }

    // 임시 토큰 (회원가입 추가정보 전달용)
    public String generateTempToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        claims.put("type", "TEMP"); // 구분용 클레임

        Date now = new Date(); // 현재 시각
        Date expiry = new Date(now.getTime() + tempTokenValidTime);  // 만료 시각

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 비밀키 반환
                .compact();  // JWT를 문자열로 직렬화
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
