package com.ucamp.coffee.domain.member.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.security.JwtTokenProvider;
import com.ucamp.coffee.domain.member.dto.KakaoUserDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.KakaoService;
import com.ucamp.coffee.domain.member.type.MemberType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;

    // 카카오톡 간편 로그인
    @GetMapping("/callback")
    public void kakaoLogin(@RequestParam String code,
    		@RequestParam(required = false) String state,
    						HttpServletRequest request,
                             HttpServletResponse response) {
    	
    	String redirectUrl = "http://localhost:5173/";
    	
        try {
            // 카카오 토큰 발급
            String accessToken = kakaoService.getKakaoAccessToken(code);
            
            // 카카오 사용자 정보 조회
            KakaoUserDto kakaoUser = kakaoService.getUserInfo(accessToken);
            String email = kakaoUser.getEmail();
            kakaoUser.setRole(state);

            // DB에 회원 존재 여부 확인
            Optional<Member> memberOptional = kakaoService.findByEmail(email);

            // 회원이 아니면, 시 JWT 발급(회원가입을 위한 임시토큰) 후 추가정보 화면으로 리다이렉트
            if(memberOptional.isEmpty()) {
            	
            	// TODO
            	String tempJwt = jwtTokenProvider.generateTempToken(email);
            	
            	String baseUrl = "http://localhost:5173/";
            	String page = "member".equals(state) ? "MemberSignup" : "CustomerSignUp";
            	
            	redirectUrl = baseUrl + page + "?token=" + tempJwt;
            }
            else {
            	// 회원이면 로그인 성공 -> 서비스 JWT 발급
            	Member member = memberOptional.get();
                String jwt = jwtTokenProvider.generateToken(member.getMemberId());
            
                // JWT를 쿠키로 전달(보안성)
                Cookie cookie = new Cookie("accessToken", jwt);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(60 * 60); // 1시간
                response.addCookie(cookie);
                
                // 세션 등록
                HttpSession session = request.getSession();
                session.setAttribute("memberId", member.getMemberId());

                // 일반회원 / 점주 홈으로 리다이렉트
                String baseUrl = "http://localhost:5173/";
            	String page = MemberType.GENERAL.equals(member.getMemberType()) ? "me" : "CafeSignUp";
                redirectUrl = baseUrl + page;
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
        	response.sendRedirect(redirectUrl);
		} catch (Exception e) {
			System.err.println("리다이렉트 처리 중 오류 발생: " + e.getMessage());
		}
    }
}
