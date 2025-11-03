package com.ucamp.coffee.domain.member.controller;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.common.security.JwtTokenProvider;
import com.ucamp.coffee.domain.member.dto.KakaoUserDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.KakaoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login/oauth2/code")
@RequiredArgsConstructor
public class KakaoController {
    @Value("${host}")
    private String host;

    @Value("${frontend.port}")
    private String frontendPort;

    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;

    // 카카오톡 간편 로그인
    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody Map<String,
                                        String> requestBody,
                                        HttpServletResponse response) {
        String code = requestBody.get("code");
        String role = requestBody.get("role");

        try {
            // 카카오 토큰 발급
            String kakaoAccessToken = kakaoService.getKakaoAccessToken(code, role);

            // 카카오 사용자 정보 조회
            KakaoUserDto kakaoUser = kakaoService.getUserInfo(kakaoAccessToken);
            String email = kakaoUser.getEmail();
            
            // DB에 회원 존재 여부 확인
            Member member = kakaoService.findByEmail(email).orElse(null);

            // 회원이 아니면, 시 JWT 발급(회원가입을 위한 임시토큰) 후 추가정보 화면으로 리다이렉트
            if(member == null) {
            	String tempJwt = jwtTokenProvider.generateTempToken(email, role);
            	
            	// JWT는 쿠키로 전달(보안성)
            	Cookie cookie = new Cookie("TEMP_JWT", tempJwt);
            	cookie.setHttpOnly(true);
            	cookie.setPath("/");
            	cookie.setMaxAge(10 * 60); // 10분
            	response.addCookie(cookie);
            	
            	// 회원가입시 추가 입력을 위해 일반회원/점주 회원가입 페이지로 이동
            	String redirectUrl = String.format(
                        host + ":" + frontendPort + "/signup?role=%s", role);
            	return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl)); 
            }
            
            // 회원이면 로그인 성공 -> 서비스 JWT 발급
            String jwtAccessToken = jwtTokenProvider.generateAccessToken(member.getMemberId());
            String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(member.getMemberId());
            
            // role에 따라 일반회원/점주 홈 화면으로 이동
            String redirectUrl = "";
            if("member".equalsIgnoreCase(role)) {
            	redirectUrl = host + ":" + frontendPort + "/me";
            }
            else if("customer".equalsIgnoreCase(role)) {
            	redirectUrl = host + ":" + frontendPort + "/stores";
            }
            else {
            	redirectUrl = host + ":" + frontendPort + "/";
            }
            
            // JWT를 쿠키로 전달(보안성)
            ResponseCookie accessCookie = ResponseCookie.from("ACCESS_JWT", jwtAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 60)
                .build();
            response.addHeader("Set-Cookie", accessCookie.toString());

            ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_JWT", jwtRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("None")
                .maxAge(60 * 60 * 24 * 14)
                .build();
            response.addHeader("Set-Cookie", refreshCookie.toString());

        	return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CommonException(ApiStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
