package com.zhize.core.validate.code.sms;

import com.zhize.core.properties.SecurityProperties;
import com.zhize.core.validate.code.ImageCode;
import com.zhize.core.validate.code.ValidateCode;
import com.zhize.core.validate.code.ValidateCodeGenerator;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Component("smsCodeGenerator")
public class SmsCodeGenerator implements ValidateCodeGenerator {

    @Autowired
    SecurityProperties securityProperties;

    @Override
    public ValidateCode generate(ServletWebRequest request) {
        String code = RandomStringUtils.randomNumeric(securityProperties.getCode().getSms().getLength());
        return new ValidateCode(code, securityProperties.getCode().getSms().getExpire());
    }


    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public SmsCodeGenerator setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        return this;
    }
}
