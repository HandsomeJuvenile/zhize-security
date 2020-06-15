package com.zhize.core.validate.code.impl;

import com.zhize.core.validate.code.ValidateCodeProcessor;
import com.zhize.core.validate.code.ValidateCode;
import com.zhize.core.validate.code.ValidateCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.ServletRequest;
import java.util.Map;

public class AbstractValidateCodeProcessor<T extends ValidateCode> implements ValidateCodeProcessor {

    @Autowired
    private Map<String,ValidateCodeGenerator> validateCodeGenerator;

    @Override
    public void create(ServletRequest request) {
        //  创建ValidateCode
        T code = generator(request);

    }

    @Override
    public void validate(ServletWebRequest request) {

    }

    public T generator(ServletRequest request) {
       // request.getAttribute()

        return null;
    }

}
