package com.zhize.core.validate.code;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletRequest;
import java.io.IOException;

public interface ValidateCodeProcessor {

    String SESSION_KEY_PREFIX = "SESSION_KEY_FOR_CODE_";

    /**
     * 创建验证码
     * @param request
     */
    void create(ServletWebRequest request) throws IOException, ServletRequestBindingException;

    /**
     * 校验验证码
     * @param request
     */
    void validate(ServletWebRequest request);

}
