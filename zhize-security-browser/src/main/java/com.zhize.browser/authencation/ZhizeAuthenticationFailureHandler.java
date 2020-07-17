package com.zhize.browser.authencation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhize.core.properties.LoginResponseType;
import com.zhize.core.properties.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("zhizeAuthenticationFailureHandler")
public class ZhizeAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZhizeAuthenticationFailureHandler.class);

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    SecurityProperties securityProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse, AuthenticationException exception) throws IOException, ServletException {
        LOGGER.info("登陆失败");
        LoginResponseType loginResponseType = securityProperties.getBrowser().getLoginType();
        if (LoginResponseType.JSON.equals(loginResponseType)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(exception.getMessage()));
        }else {
            super.onAuthenticationFailure(httpServletRequest,httpServletResponse,exception);
        }
    }

}
