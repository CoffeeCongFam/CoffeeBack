package com.ucamp.coffee.domain.purchase.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ucamp.coffee.common.exception.CommonException;
import com.ucamp.coffee.common.response.ApiStatus;
import com.ucamp.coffee.domain.purchase.dto.PortOnePaymentResponseDTO;
import com.ucamp.coffee.domain.purchase.dto.PortOneRequestDTO;
import com.ucamp.coffee.domain.purchase.dto.PortOneTokenResponseDTO;
import com.ucamp.coffee.domain.purchase.entity.Purchase;
import com.ucamp.coffee.domain.purchase.event.GiftReceiveEvent;
import com.ucamp.coffee.domain.purchase.repository.PurchaseRepository;
import com.ucamp.coffee.domain.purchase.type.PaymentStatus;
import com.ucamp.coffee.domain.subscription.service.MemberSubscriptionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	@Value("${portone.imp_key}")
	private String apiKey;

	@Value("${portone.imp_secret}")
	private String apiSecret;

	// 나중에 Bean으로 변경
	private final RestTemplate restTemplate = new RestTemplate();

	private final PurchaseRepository purchaseRepository;
	private final MemberSubscriptionService memberSubscriptionService;
	private final ApplicationEventPublisher publisher;

	/**
	 * 포트원 api 토큰 발급
	 * 
	 * @return
	 */
	private String getAccessToken() {

		// 포트원 토큰 발급 API URL
		String url = "http://api.iamport.kr/users/getToken";

		Map<String, String> body = Map.of("imp_key", apiKey, "imp_secret", apiSecret);

		// http 요청 body에 json을 보내기 위해 Java 객체를 JSON 문자열로 변환
		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody;
		try {
			requestBody = objectMapper.writeValueAsString(body);
		} catch (Exception e) {
			throw new CommonException(ApiStatus.INTERNAL_SERVER_ERROR, "결제 요청 데이터 변환 중 오류가 발생했습니다.");
		}

		// 응답 Header 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

		// URL로 요청 보내기
		ResponseEntity<PortOneTokenResponseDTO> response = restTemplate.postForEntity(url, entity,
				PortOneTokenResponseDTO.class);

		PortOneTokenResponseDTO responseBody = response.getBody();
		if (responseBody == null || responseBody.getCode() != 0) {
			throw new CommonException(ApiStatus.INTERNAL_SERVER_ERROR, "결제 요청 데이터 변환 중 오류가 발생했습니다.");

		}

		return response.getBody().getResponse().getAccess_token();
	}

	/**
	 * 결제 검증
	 * 
	 * @param request
	 */
	@Transactional
	public void verifyPaymentDetail(PortOneRequestDTO request) {

		String token = getAccessToken();

		String PORTONE_PAYMENT_LOOKUP_URL = "http://api.iamport.kr/payments/";
		String url = PORTONE_PAYMENT_LOOKUP_URL + request.getImpUid();

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<PortOnePaymentResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity,
				PortOnePaymentResponseDTO.class);

		PortOnePaymentResponseDTO.Response paymentData = response.getBody().getResponse();

		// 주문 조회
		Purchase purchase = purchaseRepository.findById(request.getPurchaseId())
				.orElseThrow(() -> new CommonException(ApiStatus.NOT_FOUND, "구매 정보가 존재하지 않습니다"));

		// 금액 검증
		if (!paymentData.getAmount().equals(purchase.getPaymentAmount())) {
			purchase.changePaymentStatus(PaymentStatus.DENIED);
			purchaseRepository.save(purchase);
			throw new CommonException(ApiStatus.BAD_REQUEST, "결제 금액 불일치");
		}

		// 검증 성공하면
		if ("paid".equalsIgnoreCase(paymentData.getStatus())) {
			purchase.handlePaymentSuccess(paymentData.getPaid_at(), paymentData.getImp_uid(),
					paymentData.getPg_provider());
			purchaseRepository.save(purchase);

			// 결제 확정 후 구독권 생성
			Long memberSubscriptionId = memberSubscriptionService.createMemberSubscription(purchase);

			// 선물이면 수신자한테 알림 전송
			if ("Y".equals(purchase.getIsGift())) {
				publisher.publishEvent(new GiftReceiveEvent(purchase.getPurchaseId(), memberSubscriptionId));
			}
		} else {
			purchase.changePaymentStatus(PaymentStatus.DENIED);
			throw new CommonException(ApiStatus.BAD_REQUEST, "결제가 승인되지 않았습니다.");
		}
	}

	/**
	 * 실제 환불 처리
	 * 
	 * @param purchase
	 */
	@Transactional
	public void cancelPayment(Purchase purchase) {

		// 포트원 토큰 발급
		String token = getAccessToken();

		// 취소 요청 URL
		String cancelUrl = "http://api.iamport.kr/payments/cancel";

		// 요청 Body 구성
		Map<String, Object> body = Map.of("imp_uid", purchase.getImpUid());

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		try {
			// 환불 요청
			ResponseEntity<Map> response = restTemplate.exchange(cancelUrl, HttpMethod.POST, entity, Map.class);
			Map<String, Object> responseBody = response.getBody();

			if (responseBody == null || (Integer) responseBody.get("code") != 0) {
				throw new CommonException(ApiStatus.INTERNAL_SERVER_ERROR,
						"포트원 환불 요청 실패: " + responseBody.get("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CommonException(ApiStatus.INTERNAL_SERVER_ERROR, "결제 취소 중 오류가 발생했습니다.");
		}

	}

}
