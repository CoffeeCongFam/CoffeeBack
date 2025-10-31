package com.ucamp.coffee.domain.sms;

import org.springframework.stereotype.Service;

import com.ucamp.coffee.domain.orders.dto.OrdersMessageDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsService {

	private final SmsUtil smsUtil;

	public void sendVerifyNum(String phoneNum) {
		int verificationCode = (int) (Math.random() * 900000) + 100000;
		String verificationCodeStr = String.valueOf(verificationCode);

		smsUtil.sendOne(phoneNum, "[테스트] 인증번호는 " + verificationCodeStr + " 입니다.");
		System.out.println("발송 완료 코드: " + verificationCodeStr);
	}

	// 주문 성공시 메시지 전송
	public void sendCustomerOrderCompletedMessage(OrdersMessageDTO message) {

		StringBuilder msg = new StringBuilder();
		msg.append("[").append(message.getStoreName()).append("]").append(message.getName())
				.append("고객님! 고객님의 주문이 정상적으로 접수되었습니다.\n")
				.append("주문번호: #").append(message.getOrderNumber()).append("\n")
				.append("잠시 후 준비가 완료되면 알려드릴게요");

		smsUtil.sendOne(message.getTel(), String.valueOf(msg));
	}
}
