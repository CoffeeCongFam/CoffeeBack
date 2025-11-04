package com.ucamp.coffee.domain.sms;

import org.springframework.stereotype.Service;

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

	/**
	 * 메시지 전송
	 * 
	 * @param tel
	 * @param msg
	 */
	public void sendMessage(String tel, String msg) {

		//전화번호 하이픈 및 공백 제거
		String cleanedTel = tel.replaceAll("[^0-9]", "");
		smsUtil.sendOne(cleanedTel, msg);
	}
}
