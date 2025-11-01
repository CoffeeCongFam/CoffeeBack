package com.ucamp.coffee.domain.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Component
public class SmsUtil {

    private final DefaultMessageService messageService;

    public SmsUtil(
        @Value("${coolsms.api.key}") String apiKey,
        @Value("${coolsms.api.secret}") String apiSecretKey
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "http://api.coolsms.co.kr");
    }

    public void sendOne(String to, String text) {
        Message message = new Message();
        message.setFrom("01091205456");
        message.setTo(to);
        message.setText(text);

        try {
            SingleMessageSentResponse response =
                this.messageService.sendOne(new SingleMessageSendingRequest(message));
            System.out.println("문자 전송 성공: " + response);
        } catch (Exception e) {
            System.out.println("문자 전송 실패: " + e.getMessage());
        }
    }
}
