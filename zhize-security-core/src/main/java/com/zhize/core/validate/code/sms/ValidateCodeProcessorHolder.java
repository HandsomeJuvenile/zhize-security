package com.zhize.core.validate.code.sms;

import com.zhize.core.validate.code.ValidateCodeProcessor;
import com.zhize.core.validate.code.ValidateCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 根据类型去找到我们需要的ValidateCodeProcessor
 */
@Component
public class ValidateCodeProcessorHolder {

    @Autowired
    private Map<String,ValidateCodeProcessor> validateCodeProcessors;

    public ValidateCodeProcessor findValidateCodeProcessor(String type){
        String name = type.toLowerCase() + ValidateCodeProcessor.class.getSimpleName();
        ValidateCodeProcessor processor = validateCodeProcessors.get(name);
        if (processor == null) {
            throw new ValidateCodeException("验证码处理器"+name+"为空");
        }
        return processor;
    }


}
