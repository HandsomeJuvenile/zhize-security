package com.zhize.browser.authencation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhize.core.properties.LoginResponseType;
import com.zhize.core.properties.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("zhizeAuthenticationSuccessHandler")
public class ZhizeAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SecurityProperties securityProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(ZhizeAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        LOGGER.info("登陆成功");
        LoginResponseType loginResponseType = securityProperties.getBrowser().getLoginType();
        if (LoginResponseType.JSON.equals(loginResponseType)) {
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(authentication));
        }else {
            super.onAuthenticationSuccess(httpServletRequest,httpServletResponse,authentication);
        }
    }
}
