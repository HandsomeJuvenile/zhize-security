package com.zhize.core.validate.code;

import org.springframework.web.context.request.ServletWebRequest;

public interface ValidateCodeGenerator {

    ImageCode generate(ServletWebRequest request);
}
