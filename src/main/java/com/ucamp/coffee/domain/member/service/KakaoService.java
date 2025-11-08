package com.ucamp.coffee.domain.member.service;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucamp.coffee.domain.member.dto.KakaoUserDto;
import com.ucamp.coffee.domain.member.entity.Member;
import com.ucamp.coffee.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MemberRepository memberRepository;

    @Value("${server.host}")
    private String host;

    @Value("${server.backend-port}")
    private int backPort;

    @Value("${server.frontend-port}")
    private int frontPort;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-endpoint}")
    private String redirectEndPoint;

    @Value("${kakao.token-url}")
    private String tokenUrl;

    @Value("${kakao.user-info-url}")
    private String userInfoUrl;

    public String getKakaoAccessToken(String code) {
        String redirectUrl;
        if ("443".equals(String.valueOf(backPort).trim())) {
            redirectUrl = host + redirectEndPoint;
        } else {
            redirectUrl = host + ":" + backPort + redirectEndPoint;
        }
        log.info("==================================");
        log.info("KS 1: {}", redirectUrl);
        log.info("==================================");

        // Access Token 요청
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> httpBodies = new LinkedMultiValueMap<>();
        httpBodies.add("grant_type", "authorization_code");
        httpBodies.add("client_id", clientId);
        httpBodies.add("redirect_uri", redirectUrl);
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
            e.printStackTrace();
            throw new RuntimeException("카카오 토큰 발급 실패", e);
        }
    }

    // 사용자 정보 조회
    public KakaoUserDto getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // HttpHeaders
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

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

    // 이메일로 회원 조회
	public Optional<Member> findByEmail(String email) {
		return memberRepository.findByEmail(email);
	}

    private String buildUrl() {
        String portPart = (frontPort == 443) ? "" : ":" + frontPort;
        return host + portPart;
    }
}
