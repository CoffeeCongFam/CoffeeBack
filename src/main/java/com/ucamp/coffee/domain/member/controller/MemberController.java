package com.ucamp.coffee.domain.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.JwtTokenProvider;
import com.ucamp.coffee.domain.member.dto.MemberDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.service.MemberService;
import com.ucamp.coffee.domain.member.type.ActiveStatusType;
import com.ucamp.coffee.domain.member.type.MemberType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    @Value("${host}")
    private String host;

    @Value(("${frontend.port}"))
    private String frontendPort;

	private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

	// 일반회원/점주 회원가입 화면
    @GetMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signupPage(@RequestParam String role){
    	
    	return ResponseMapper.successOf("회원가입 페이지 - role=" + role);
    }
    
    // 카카오톡 로그인 성공 후, 일반회원/점주 회원가입 화면으로 다시 이동해서 DB에 저장
    // 일반회원 회원가입
    @PostMapping("/signup/member")
    public Object registerMember(
            @RequestBody MemberDto memberDto,
            HttpServletRequest request,
            HttpServletResponse response) {
    	
    	// TODO: 에러 처리 필요
    	// 회원가입 insert
        memberDto.setMemberType(MemberType.GENERAL);
        memberDto.setActiveStatus(ActiveStatusType.ACTIVE);
        
        Member savedMember = memberService.save(memberDto);
        
        // JWT 생성
        String accessToken = jwtTokenProvider.generateToken(savedMember.getMemberId());
        
        // HttpOnly 쿠키로 JWT 전달
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);  // JS에서 접근 불가
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 *60);  // 1시간
        response.addCookie(cookie);
        
        // 세션 등록
        HttpSession session = request.getSession();
        session.setAttribute("user", savedMember);
        
        // 성공 시
        return ResponseMapper.successOf(Map.of(
        		"message", "성공",
        		"redirectUrl", host + ":" + frontendPort + "/me"));
    }
    
    // 점주 회원가입
    @PostMapping("/signup/store")
    public Object registerStores(
            @RequestBody MemberDto memberDto,
            HttpServletRequest request,
            HttpServletResponse response) {
    	
    	// TODO: 에러 처리 필요
    	// 회원가입 insert
        memberDto.setMemberType(MemberType.STORE);
        memberDto.setActiveStatus(ActiveStatusType.ACTIVE);
        
        Member savedMember = memberService.save(memberDto);
        
        // JWT 생성
        String accessToken = jwtTokenProvider.generateToken(savedMember.getMemberId());
        
        // HttpOnly 쿠키로 JWT 전달
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);  // JS에서 접근 불가
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 *60);  // 1시간
        response.addCookie(cookie);
        
        // 세션 등록
        HttpSession session = request.getSession();
        session.setAttribute("user", savedMember);
        
        // 성공 시
        return ResponseMapper.successOf(Map.of(
        		"message", "성공",
        		"redirectUrl", host + ":" + frontendPort + "/store"));
    }
    
}