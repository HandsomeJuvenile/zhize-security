package com.zhize.core.validate.code.sms;

import com.zhize.core.properties.SecurityConstants;
import com.zhize.core.validate.code.ValidateCode;
import com.zhize.core.validate.code.impl.AbstractValidateCodeProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

@Component("smsValidateCodeProcessor")
public class SmsCodeProcessor extends AbstractValidateCodeProcessor<ValidateCode>{

    @Autowired
    SmsCodeSender defaultSmsCodeSender;

    @Override
    protected void send(ServletWebRequest request, ValidateCode code) throws IOException, ServletRequestBindingException {
        String paramName = SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE;
        String mobile = ServletRequestUtils.getRequiredStringParameter(request.getRequest(), paramName);
        defaultSmsCodeSender.send(mobile, code.getCode());
    }
}
