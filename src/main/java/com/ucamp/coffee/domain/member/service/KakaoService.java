package com.ucamp.coffee.domain.member.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucamp.coffee.domain.member.dto.KakaoUserDto;
import com.ucamp.coffee.domain.member.type.GenderType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    @Value("${kakao.user-info-url}")
    private String userInfoUrl;

    public String getKakaoAccessToken(String code) {
        // Access Token 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> httpBodies = new LinkedMultiValueMap<>();
        httpBodies.add("grant_type", "authorization_code");
        httpBodies.add("client_id", clientId);
        httpBodies.add("redirect_uri", redirectUri);
        httpBodies.add("code", code);

        // HttpHeader + HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpBodies, httpHeaders);

        // Http 요청 : exchange 함수는 HttpEntity 오브젝트를 넣게 되어있음
        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                request,
                String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 발급 실패", e);
        }
    }

    // 사용자 정보 조회
    public KakaoUserDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // HttpHeaders
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        httpHeaders.setBearerAuth(accessToken); // Authorization: Bearer 토큰

        // HttpEntity
        HttpEntity<Void> request = new HttpEntity<>(httpHeaders);

        // 요청
        ResponseEntity<String> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                String.class
        );

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            JsonNode kakaoAccount = jsonNode.path("kakao_account");

            KakaoUserDto userDto = new KakaoUserDto();
            String email = kakaoAccount.path("email").asText(null);
            userDto.setEmail(email);

            return userDto;

        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 조회 실패", e);
        }
    }
}
