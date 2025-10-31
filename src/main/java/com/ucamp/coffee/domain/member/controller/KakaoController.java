package com.ucamp.coffee.domain.member.controller;

import com.ucamp.coffee.domain.member.dto.KakaoUserDto;
import com.ucamp.coffee.domain.member.service.KakaoService;
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

    @GetMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code,
                                        HttpServletResponse response) {
        try {
            // 카카오 토큰 발급
            String accessToken = kakaoService.getKakaoAccessToken(code);

            // 카카오 사용자 정보 조회
            KakaoUserDto kakaoUser = kakaoService.getUserInfo(accessToken);
            if(kakaoUser.getEmail() == null){
                response.sendRedirect("/");
            }
            // 로그인 성공 응답
            return ResponseEntity.ok(
                    Map.of(
                            "accessToken", accessToken,
                            "email", kakaoUser.getEmail()));

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("카카오 로그인 실패: " + e.getMessage());
        }
    }
}
