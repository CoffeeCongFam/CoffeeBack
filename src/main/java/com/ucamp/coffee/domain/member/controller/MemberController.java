package com.ucamp.coffee.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.JwtTokenProvider;
import com.ucamp.coffee.domain.member.dto.MemberDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.MemberService;
import com.ucamp.coffee.domain.member.type.MemberType;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class MemberController {
	private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

	// 일반회원/점주 회원가입 선택 화면
    @GetMapping
    public ResponseEntity<ApiResponse<?>> signupPage(){
    	return ResponseMapper.successOf("회원가입 선택 페이지입니다.");
    }
    
    // 카카오톡 로그인 성공 후, 일반회원/점주 회원가입 화면으로 다시 이동해서 DB에 저장
    // 일반회원 회원가입
    @PostMapping("/member")
    public Object registerMember(
            @RequestBody MemberDto memberDto,
            HttpServletRequest request) {

    	memberDto.setMemberType(MemberType.GENERAL);
        String tempJwt = getCookieValue(request, "TEMP_JWT");
        
        // JWT가 없거나 유효하지 않은 경우
        if (tempJwt == null || !jwtTokenProvider.validateToken(tempJwt)) {
            return ResponseMapper.failOf(HttpStatus.UNAUTHORIZED, this.getClass());
        }

        Claims claims = jwtTokenProvider.getClaims(tempJwt);
        memberDto.setEmail(claims.getSubject());
        
        Member savedMember = memberService.save(memberDto);
        return ResponseMapper.successOf(savedMember);
    }

    // 점주 회원가입
    @PostMapping("/stores")
    public Object registerOwner(
            @RequestBody MemberDto memberDto,
            HttpServletRequest request) {

    	memberDto.setMemberType(MemberType.STORE);
        String tempJwt = getCookieValue(request, "TEMP_JWT");
        if (tempJwt == null || !jwtTokenProvider.validateToken(tempJwt)) {
            return ResponseMapper.failOf(HttpStatus.UNAUTHORIZED, this.getClass());
        }

        Claims claims = jwtTokenProvider.getClaims(tempJwt);
        memberDto.setEmail(claims.getSubject());

        Member savedOwner = memberService.save(memberDto);
        return ResponseMapper.successOf(savedOwner);
    }

    // 쿠키값 가져오기
    private String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
    
}