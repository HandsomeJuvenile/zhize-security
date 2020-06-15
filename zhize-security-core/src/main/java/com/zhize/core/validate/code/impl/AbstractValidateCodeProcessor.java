package com.zhize.core.validate.code.impl;

import com.zhize.core.validate.code.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractValidateCodeProcessor<T extends ValidateCode> implements ValidateCodeProcessor {

    @Autowired
    private Map<String,ValidateCodeGenerator> validateCodeGenerators;
    /**
     * 操作session的工具类
     */
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Override
    public void create(ServletWebRequest request) throws IOException, ServletRequestBindingException {
        //  创建ValidateCode
        T code = generator(request);
        save( request,code);
        send(request,code);

    }

    protected abstract void send(ServletWebRequest request, T code) throws IOException, ServletRequestBindingException;

    @Override
    public void validate(ServletWebRequest request) {

    }

    public void save(ServletWebRequest request,T t){
        sessionStrategy.setAttribute(request,getSessionKey(request),t);
    }

    /**
     * 构建验证码放入session时的key
     *
     * @param request
     * @return
     */
    private String getSessionKey(ServletWebRequest request) {
        return SESSION_KEY_PREFIX + getValidateCodeType(request).toString().toUpperCase();
    }

    public T generator(ServletWebRequest request) {
        String type = getValidateCodeType(request).toString().toLowerCase();
        String generatorName = type + ValidateCodeGenerator.class.getSimpleName();
        ValidateCodeGenerator validateCodeGenerator = validateCodeGenerators.get(generatorName);
        if (validateCodeGenerator == null) {
            throw new ValidateCodeException("验证码生成器 "+generatorName+"不存在");
        }
        return (T) validateCodeGenerator.generate(request);
    }

    public ValidateCodeType getValidateCodeType(ServletWebRequest request) {
        String type = StringUtils.substringBefore(getClass().getSimpleName(),"CodeProcessor");
        return ValidateCodeType.valueOf(type.toUpperCase());
    }

    public static void main(String[] args) {
        String type = StringUtils.substringBefore("ImageCodeProcessor", "CodeProcessor");
        System.out.println(StringUtils.substringBefore("ImageCodeProcessor", "CodeProcessor"));
        System.out.println(StringUtils.substringBefore("ImageCodeProcessor","CodeProcessor"));
    }

}
