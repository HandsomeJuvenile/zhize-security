package com.zhize.core.validate.code.sms;

public interface SmsCodeSender {

    void send(String phone,String code);
}
