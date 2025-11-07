package com.ucamp.coffee.domain.member.controller;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.common.security.JwtTokenProvider;
import com.ucamp.coffee.domain.member.dto.KakaoUserDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.member.service.KakaoService;
import com.ucamp.coffee.domain.member.type.ActiveStatusType;
import com.ucamp.coffee.domain.member.type.MemberType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoController {

    @Value("${server.host}")
    private String host;

    @Value("${server.backend-port}")
    private int backPort;

    @Value("${server.frontend-port}")
    private int frontPort;

    private final KakaoService kakaoService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    // 카카오톡 간편 로그인
    @GetMapping("/callback")
    public void kakaoLogin(@RequestParam String code,
                           @RequestParam(required = false) String state,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        String redirectUrl = buildUrl() + "/";
        log.info("==================================");
        log.info("KC 1: {}", redirectUrl);
        log.info("==================================");

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

                // 비회원이 카카오톡 간편 로그인으로 바로 접근했을 때
                if(kakaoUser.getRole() == null){
                    response.sendRedirect(redirectUrl+"SignUp?from-purpose=kakao");
                    return;
                }

                // TODO
                String tempJwt = jwtTokenProvider.generateTempToken(email);

                String baseUrl = buildUrl() + "/";
                String page = "member".equals(state) ? "MemberSignup" : "CustomerSignUp";

                redirectUrl = baseUrl + page + "?token=" + tempJwt;
                log.info("==================================");
                log.info("KC 2: {}", redirectUrl);
                log.info("==================================");
            }
            else {
                // 회원이면 로그인 성공 -> 서비스 JWT 발급
                Member member = memberOptional.get();

                // 탈퇴 회원인지 확인
                if(member.getActiveStatus() == ActiveStatusType.WITHDRAW){
                    LocalDateTime deleteAt = member.getDeletedAt();

                    // 탈퇴 후, 90일 이내이면 ActiveStatus를 ACTIVE로 변경
                    LocalDateTime rejoinDeadline = deleteAt.plusDays(90);
                    if(LocalDateTime.now().isBefore(rejoinDeadline)){
                        member.setDeletedAt(null);
                        member.setActiveStatus(ActiveStatusType.ACTIVE);
                        memberRepository.save(member);
                    }else{
                        // 90일 경과 시, 로그인 차단
                        throw new CommonException(ApiStatus.FORBIDDEN, "탈퇴 후 90일이 지나 로그인이 불가합니다.");
                    }
                }
                
                // 로그인 성공 -> jwt 발급
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
                String baseUrl = buildUrl();
                String page = MemberType.GENERAL.equals(member.getMemberType()) ? "me" : "store";
                redirectUrl = baseUrl + "/" + page;
                log.info("==================================");
                log.info("KC 3: {}", redirectUrl);
                log.info("==================================");
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

    private String buildUrl() {
        String portPart = (frontPort == 443) ? "" : ":" + frontPort;
        return host + portPart;
    }
}