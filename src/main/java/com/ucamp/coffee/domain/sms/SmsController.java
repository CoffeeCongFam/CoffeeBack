package com.ucamp.coffee.domain.sms;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/sms/send")
    public ResponseEntity<Object> sendVerifyNum(@RequestParam("phoneNum") String phoneNum) {
        try {
            if (phoneNum.contains("-")) {
                return new ResponseEntity<>("하이픈(-) 제거 후 입력해주세요", HttpStatus.BAD_REQUEST);
            }
            smsService.sendVerifyNum(phoneNum);
            return new ResponseEntity<>("문자 발송 완료", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("문자 발송 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
