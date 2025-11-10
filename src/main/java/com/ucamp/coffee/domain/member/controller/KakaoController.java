package com.ucamp.coffee.domain.member.controller;

import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.common.response.ResponseMapper;
import com.ucamp.coffee.common.security.JwtTokenProvider;
import com.ucamp.coffee.domain.member.dto.KakaoUserDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;
import com.ucamp.coffee.domain.member.service.KakaoService;
import com.ucamp.coffee.domain.member.type.ActiveStatusType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    public ResponseEntity<Map<String, Object>> kakaoLogin(@RequestParam String code,
                                                          @RequestParam(required = false) String state,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) {

        Map<String, Object> result = new HashMap<>();
        String redirectUrl = buildUrl() + "/";

        log.info("==================================");
        log.info("KC 1: callback 호출, redirectUrl 초기값: {}", redirectUrl);
        log.info("state: {}", state);
        log.info("==================================");

        try {
            // 카카오 토큰 발급
            String accessToken = kakaoService.getKakaoAccessToken(code);
            log.info("KS 1: 카카오 accessToken 발급 완료");

            // 카카오 사용자 정보 조회
            KakaoUserDto kakaoUser = kakaoService.getUserInfo(accessToken);
            String email = kakaoUser.getEmail();
            kakaoUser.setRole(state);
            log.info("KS 2: 카카오 사용자 정보 조회 완료, email={}, role={}", email, state);

            // DB에 회원 존재 여부 확인
            Optional<Member> memberOptional = kakaoService.findByEmail(email);

            if(memberOptional.isEmpty()) {
                log.info("KC 2: 회원 미존재, 임시 토큰 발급");

                // 비회원 처리
                String tempJwt = jwtTokenProvider.generateTempToken(email);
                result.put("accessToken", tempJwt);
                result.put("isMember", false);
                result.put("memberType", null);
                result.put("state", state);

                log.info("KC 2: 임시 토큰 발급 완료");

            } else {
                // 회원이면 로그인 처리
                boolean isActiveStatus = false;
                Member member = memberOptional.get();
                log.info("KC 3: 회원 존재, memberId={}, memberType={}", member.getMemberId(), member.getMemberType());

                // 탈퇴 회원 확인
                if(member.getActiveStatus() == ActiveStatusType.WITHDRAW){
                    LocalDateTime deleteAt = member.getDeletedAt();
                    LocalDateTime rejoinDeadline = deleteAt.plusDays(90);

                    // 90일이 지나지 않았을 때
                    if(LocalDateTime.now().isBefore(rejoinDeadline)){
                        isActiveStatus = true;
                        log.info("KC 3: 탈퇴 후 90일 이내, 재활성화 완료");

                        result.put("isActiveStatus", isActiveStatus);
                        result.put("memberId", member.getMemberId());
                    } else {
                        throw new CommonException(ApiStatus.FORBIDDEN, "탈퇴 후 90일이 지나 로그인이 불가합니다.");
                    }
                    return ResponseEntity.ok(result);
                }

                // JWT 발급 및 쿠키 설정
                String jwt = jwtTokenProvider.generateToken(member.getMemberId());
                Cookie cookie = new Cookie("accessToken", jwt);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(-1);
                response.addCookie(cookie);
                log.info("KC 3: 서비스 JWT 발급 및 쿠키 추가 완료");

                // 세션 등록
                HttpSession session = request.getSession();
                session.setAttribute("memberId", member.getMemberId());
                log.info("KC 3: 세션 등록 완료, memberId={}", member.getMemberId());

                // JSON 반환용 데이터
                result.put("accessToken", jwt);
                result.put("isMember", true);
                result.put("memberType", member.getMemberType().name());
                result.put("state", state);
                result.put("kakaoToken", accessToken);
            }

            log.info("KC 4: JSON 반환 준비 완료, result={}", result);

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 예외 발생");
            throw new CommonException(ApiStatus.UNAUTHORIZED, "카카오 로그인 실패");
        }

        return ResponseEntity.ok(result);
    }

    private String buildUrl() {
        String portPart = (frontPort == 443) ? "" : ":" + frontPort;
        return host + portPart;
    }
}