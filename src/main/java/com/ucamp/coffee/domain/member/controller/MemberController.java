package com.ucamp.coffee.domain.member.controller;

import java.util.Map;

import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.store.entity.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class MemberController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    String message = "";

    // 일반회원/점주 회원가입 화면
    @GetMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signupPage(@RequestParam String role){

        return ResponseMapper.successOf("회원가입 페이지 - role=" + role);
    }

    // 카카오톡 로그인 성공 후, 일반회원/점주 회원가입 화면으로 다시 이동해서 DB에 저장
    // 일반회원 회원가입
    @PostMapping("/signup/member")
    public ResponseEntity<ApiResponse<?>> registerMember(
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
        session.setAttribute("memberId", savedMember.getMemberId());

        Long memberId = (Long) session.getAttribute("memberId");

        // 성공 시
        return ResponseMapper.successOf(Map.of(
                "message", "성공",
                "memberId", memberId,
                "redirectUrl", "http://localhost:5173/me"
        ));
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
        session.setAttribute("memberId", savedMember.getMemberId());

        Long memberId = (Long) session.getAttribute("memberId");

        // 성공 시
        return ResponseMapper.successOf(Map.of(
                "message", "성공",
                "memberId", memberId,
                "redirectUrl", "http://localhost:5173/store"));
    }

    // 카카오톡 로그아웃 & 세션/쿠키 삭제
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpSession session,
                                                 HttpServletResponse response){
        // 세션 삭제
        if(session != null){
            session.invalidate();
        }

        // JWT 쿠키 삭제
        Cookie jwtCookie = new Cookie("accessToken", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);

        // 쿠키 삭제(세션 쿠키)
        Cookie sessionCookie = new Cookie("JSESSIONID", null);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);

        return ResponseMapper.successOf(Map.of("message", "로그아웃 성공"));
    }
    
    // 로그인 후 현재 인증된 사용자의 기본 정보를 반환하는 API
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> getMemberInfo(@AuthenticationPrincipal MemberDetails user){
    	
    	Long memberId = user.getMemberId();
        Member member = memberService.findById(memberId);  // 회원 조회
        Store store = memberService.findByStoreId(member.getMemberId());  // 제휴매장 조회

        MemberDto dto = MemberDto.builder()
        	    .memberId(member.getMemberId())
        	    .email(member.getEmail())
        	    .tel(member.getTel())
        	    .gender(member.getGender())
        	    .name(member.getName())
        	    .memberType(member.getMemberType())
        	    .activeStatus(member.getActiveStatus())
                .partnerStoreId(store.getPartnerStoreId())
        	    .build();
        
        return ResponseMapper.successOf(dto);
    }

    // 선물할 때, 전화번호 입력 시 memberId, 회원이름, 전화번호 전달
    @PostMapping("/me/purchase/gift/tel")
    public ResponseEntity<ApiResponse<?>> getTelInfo(@RequestBody MemberDto memberDto){

        String tel = memberDto.getTel();

        Member member = memberService.findByTel(tel);

        MemberDto dto = MemberDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .tel(member.getTel())
                .build();

        return ResponseMapper.successOf(dto);
    }
}