package com.zhize.core.properties;

import lombok.Data;

@Data
public class SmsCodeProperties {

    private int length = 6;

    private int expire = 60;

    private String urls = ""; // 需要验证码的请求

}
