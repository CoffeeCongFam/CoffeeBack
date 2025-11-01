package com.ucamp.coffee.domain.member.controller;

import com.ucamp.coffee.common.security.JwtTokenProvider;
import com.ucamp.coffee.domain.member.dto.KakaoUserDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.KakaoService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/login/oauth2/code")
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;

    // 카카오톡 간편 로그인
    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code,
    									@RequestParam String role,
                                        HttpServletResponse response) {
        try {
            // 카카오 토큰 발급
            String accessToken = kakaoService.getKakaoAccessToken(code);

            // 카카오 사용자 정보 조회
            KakaoUserDto kakaoUser = kakaoService.getUserInfo(accessToken);
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
            			"http://localhost:5173/signup/%s", role);
            	return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl)); 
            }
            
            // 회원이면 로그인 성공 -> 서비스 JWT 발급
            String jwt = jwtTokenProvider.generateToken(member.getMemberId());
            
            // role에 따라 일반회원/점주 홈 화면으로 이동
            String redirectUrl = "";
            if("me".equalsIgnoreCase(role)) {
            	redirectUrl = "http://localhost:5173/me";
            }
            else if("stores".equalsIgnoreCase(role)) {
            	redirectUrl = "http://localhost:5173/stores";
            }
            else {
            	redirectUrl = "http://localhost:5173/";
            }
            
            // JWT를 쿠키로 전달(보안성)
            Cookie jwtCookie = new Cookie("ACCESS_JWT", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60); // 1시간
        	response.addCookie(jwtCookie);
        	
        	return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("카카오 로그인 실패: " + e.getMessage());
        }
    }
}
