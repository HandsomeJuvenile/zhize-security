package com.zhize.core.validate.code;

import com.zhize.core.properties.SecurityProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自定义验证码拦截器
 */
@Component("validateCodeFilter")
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private SecurityProperties securityProperties;
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
    private Set<String> urls = new HashSet<>();
    private AntPathMatcher antPathMatcher = new AntPathMatcher(); // Spring 自带的url校验

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        String[] congfigUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(securityProperties.getCode().getImage().getUrls(),",");
        urls = Stream.of(congfigUrls).collect(Collectors.toSet());
        urls.add("/authentication/form");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 仅拦截提交登录的请求并且是post请求
        boolean action =false;
        for (String url:urls) {
            if (antPathMatcher.match(url,request.getRequestURI())) {
                action = true;
                break;
            }
        }

        if (action) {
            try {
                validate(new ServletWebRequest(request));
            }catch (ValidateCodeException validateCodeException){
                authenticationFailureHandler.onAuthenticationFailure(request,
                        response,
                        validateCodeException);
                return;
            }
        }

        filterChain.doFilter(request,response);
    }

    public void validate(ServletWebRequest request) throws ServletRequestBindingException {
       String sessionKey = ValidateCodeController.SESSION_KEY;
       ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request,sessionKey);

       String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),"imageCode");

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException( "验证码的值不能为空");
        }

        if (codeInSession == null) {
            throw new ValidateCodeException( "验证码不存在");
        }

        if (codeInSession.isExpried()) {
            sessionStrategy.removeAttribute(request, sessionKey);
            throw new ValidateCodeException( "验证码已过期");
        }

        if (!StringUtils.equals(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException( "验证码不匹配");
        }

        sessionStrategy.removeAttribute(request, sessionKey);
    }

    public AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
}
