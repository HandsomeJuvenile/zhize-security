package com.zhize.code;

import com.zhize.core.validate.code.ImageCode;
import com.zhize.core.validate.code.ValidateCodeGenerator;
import org.springframework.web.context.request.ServletWebRequest;

//@Component("imageCodeGenerator")
public class DemoImageCodeGenerator implements ValidateCodeGenerator {

    @Override
    public ImageCode generate(ServletWebRequest request) {
        System.out.println(" i an comming");
        return null;
    }

}
