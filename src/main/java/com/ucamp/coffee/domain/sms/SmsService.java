package com.ucamp.coffee.domain.sms;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsUtil smsUtil;

    public void sendVerifyNum(String phoneNum) {
        int verificationCode = (int)(Math.random() * 900000) + 100000;
        String verificationCodeStr = String.valueOf(verificationCode);

        smsUtil.sendOne(phoneNum, "[테스트] 인증번호는 " + verificationCodeStr + " 입니다.");
        System.out.println("발송 완료 코드: " + verificationCodeStr);
    }
}
