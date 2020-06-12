package com.zhize.core.validate.code.sms;

import org.springframework.stereotype.Component;

@Component
public class DefaultSmsCodeSender implements SmsCodeSender{


    @Override
    public void send(String phone, String code) {
        System.out.println("向手机号："+phone+"发送验证码"+code);
    }

}
