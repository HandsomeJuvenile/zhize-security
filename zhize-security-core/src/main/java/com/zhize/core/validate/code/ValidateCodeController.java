package com.zhize.core.validate.code;

import com.zhize.core.properties.ImageCodeProperties;
import com.zhize.core.properties.SecurityConstants;
import com.zhize.core.properties.SecurityProperties;
import com.zhize.core.validate.code.sms.SmsCodeSender;
import com.zhize.core.validate.code.sms.ValidateCodeProcessorHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@RestController
public class ValidateCodeController {

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    public static final String SESSION_KEY = "SESSION_KEY_IMAGE_CODE";

    @Autowired
    ValidateCodeGenerator imageValidateCodeGenerator;
    @Autowired
    ValidateCodeGenerator smsValidateCodeGenerator;
    @Autowired
    SmsCodeSender defaultSmsCodeSender;
    @Autowired
    ValidateCodeProcessorHolder validateCodeProcessorHolder;

    /**
     * 创建验证码，根据验证码类型不同，调用不同的 {@link ValidateCodeProcessor}接口实现
     *
     * @param request
     * @param response
     * @param type
     * @throws Exception
     */
    @GetMapping(SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX + "/{type}")
    public void createCode(HttpServletRequest request, HttpServletResponse response, @PathVariable String type)
            throws Exception {
        validateCodeProcessorHolder.findValidateCodeProcessor(type).create(new ServletWebRequest(request, response));
    }

//    /**
//     * 返回验证码图片
//     * @param request
//     * @param response
//     */
//    @GetMapping("/code/image")
//    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        // 1。 创建验证码图片
//        ImageCode imageCode = (ImageCode) createImageCode(request);
//        sessionStrategy.setAttribute(new ServletWebRequest(request),SESSION_KEY,imageCode);
//        ImageIO.write(imageCode.getImage(),"JPEG",response.getOutputStream());
//    }
//
//    /**
//     * 返回短信图片
//     * @param request
//     * @param response
//     */
//    @GetMapping("/code/sms")
//    public void createSmsCode(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletRequestBindingException {
//        ValidateCode smsCode = createSmsCode(request);
//        sessionStrategy.setAttribute(new ServletWebRequest(request),SESSION_KEY,smsCode);
//        //发送短信
//        defaultSmsCodeSender.send(ServletRequestUtils.getRequiredStringParameter(request,"mobile"),
//                smsCode.getCode());
//    }

    private ValidateCode createSmsCode(HttpServletRequest request) {
        return smsValidateCodeGenerator.generate(new ServletWebRequest(request));
    }


    private ValidateCode createImageCode(HttpServletRequest request) {
        return imageValidateCodeGenerator.generate(new ServletWebRequest(request));
    }


}
