package com.zhize.core.properties;

import lombok.Data;

@Data
public class ImageCodeProperties {

    private int width = 70;
    private int height = 20;
    private int length = 4;
    private int expire = 60;
    private String urls; // 需要验证码的请求

}
