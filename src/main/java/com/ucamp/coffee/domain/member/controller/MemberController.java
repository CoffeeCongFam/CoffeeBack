package com.ucamp.coffee.domain.member.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiResponse;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.JwtTokenProvider;
import com.ucamp.coffee.common.security.MemberDetails;
import com.ucamp.coffee.domain.member.dto.MemberDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.member.service.MemberService;
import com.ucamp.coffee.domain.member.type.ActiveStatusType;
import com.ucamp.coffee.domain.member.type.MemberType;
import com.ucamp.coffee.domain.store.entity.Store;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {
    @Value("${server.host}")
    private String host;

    @Value("${server.backend-port}")
    private int backPort;

    @Value("${server.frontend-port}")
    private int frontPort;

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

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

        String redirectUrl = buildUrl() + "/me";
        log.info("==================================");
        log.info("MC 1: " + redirectUrl);
        log.info("==================================");

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
        cookie.setMaxAge(-1);  // 브라우저 창을 닫으면 삭제
        response.addCookie(cookie);

        // 세션 등록
        HttpSession session = request.getSession();
        session.setAttribute("memberId", savedMember.getMemberId());

        Long memberId = (Long) session.getAttribute("memberId");

        // 성공 시
        return ResponseMapper.successOf(Map.of(
                "message", "성공",
                "memberId", memberId,
                "redirectUrl", redirectUrl
        ));
    }

    // 점주 회원가입
    @PostMapping("/signup/store")
    public Object registerStores(
            @RequestBody MemberDto memberDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        String redirectUrl = buildUrl() + "/CafeSignUp";
        log.info("==================================");
        log.info("MC 2: " + redirectUrl);
        log.info("==================================");

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
        cookie.setMaxAge(-1);  // 브라우저 창을 닫으면 삭제
        response.addCookie(cookie);

        // 세션 등록
        HttpSession session = request.getSession();
        session.setAttribute("memberId", savedMember.getMemberId());

        Long memberId = (Long) session.getAttribute("memberId");

        // 성공 시
        return ResponseMapper.successOf(Map.of(
                "message", "성공",
                "memberId", memberId,
                "redirectUrl", redirectUrl));
    }

    // 카카오톡 로그아웃 & 세션/쿠키 삭제
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request,
                                                 HttpServletResponse response){
    	// 세션 삭제
        request.getSession().invalidate();

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

    // 일반회원/점주 회원탈퇴
    // 탈퇴 시, 활동 상태(ACTIVE, INACTIVE, WITHDRAW)만 변경
    @PatchMapping("/active/update")
    public ResponseEntity<ApiResponse<?>> withdrawMember(
    					@AuthenticationPrincipal MemberDetails user,
    					HttpServletRequest request,
    					HttpServletResponse response){

    	// 쿠키에서 JWT 가져오기
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            throw new CommonException(ApiStatus.NOT_FOUND, "로그인 정보가 없습니다.");
        }

        // JWT 검증
        Long memberId;
        try {
        	memberId = Long.parseLong(jwtTokenProvider.getClaims(token).getSubject());
        } catch (Exception e) {
        	throw new CommonException(ApiStatus.UNAUTHORIZED, "토큰 검증 실패");
        }

        // 회원 탈퇴 처리 로직
        memberService.withdraw(memberId);
        
        // 세션 삭제
        request.getSession().invalidate();
        
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
        return ResponseMapper.successOf("회원 탈퇴 완료");
    }


    // 로그인 후 현재 인증된 사용자의 기본 정보를 반환하는 API
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> getMemberInfo(@AuthenticationPrincipal MemberDetails user){

    	// 비회원이면 바로 실패 응답
        if (user == null) {
        	throw new CommonException(ApiStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    	
    	Long memberId = user.getMemberId();
        Member member = memberService.findById(memberId);  // 회원 조회

        // storeId를 담을 변수
        Long partnerStoreId = null;

        // 점주 회원(STORE)인 경우에만 제휴매장 조회
        if (MemberType.STORE.equals(member.getMemberType())) {
            Store store = memberService.findByStoreId(member.getMemberId());  // 제휴매장 조회
            if(store != null){
                partnerStoreId = store.getPartnerStoreId();
            }
        }

        MemberDto dto = MemberDto.builder()
        	    .memberId(member.getMemberId())
        	    .email(member.getEmail())
        	    .tel(member.getTel())
        	    .gender(member.getGender())
        	    .name(member.getName())
        	    .memberType(member.getMemberType())
        	    .activeStatus(member.getActiveStatus())
                .partnerStoreId(partnerStoreId)
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

    // 일반회원/점주 회원 정보 수정
    // 이름, 전화번호 수정 가능
    @PatchMapping("/memberInfo/update")
    public ResponseEntity<ApiResponse<?>> memberInfoUpdate(@AuthenticationPrincipal MemberDetails user,
                                                            @RequestBody MemberDto memberDto){

        // 비회원이면 바로 실패 응답
        if (user == null) {
            throw new CommonException(ApiStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        Member member = memberRepository.findById(user.getMemberId())
                .orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
        
        member.setName(memberDto.getName());  // 이름
        member.setTel(memberDto.getTel());    // 전화번호

        // 변경사항 저장
        Member updateMember = memberRepository.save(member);
        MemberDto dto = new MemberDto(updateMember);

        return ResponseMapper.successOf(Map.of(
                "message", "회원 정보가 수정되었습니다.",
                "dto", dto
        ));
    }

    private String buildUrl() {
        String portPart = (frontPort == 443) ? "" : ":" + frontPort;
        return host + portPart;
    }
}