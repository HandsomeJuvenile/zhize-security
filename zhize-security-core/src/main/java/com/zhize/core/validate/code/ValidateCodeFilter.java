package com.zhize.core.validate.code;

import com.zhize.core.properties.SecurityConstants;
import com.zhize.core.properties.SecurityProperties;
import com.zhize.core.validate.code.sms.ValidateCodeProcessorHolder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    private Map<String,ValidateCodeType> urlMap = new HashMap<>();

    private AntPathMatcher antPathMatcher = new AntPathMatcher(); // Spring 自带的url校验
    private static final String SESSION_CODE_KEY = "SESSION_KEY_FORM_CODE_";
    @Autowired
    private ValidateCodeProcessorHolder validateCodeProcessorHolder;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();

        urlMap.put(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM,ValidateCodeType.IMAGE);
        addUrlToMap(securityProperties.getCode().getImage().getUrls(),ValidateCodeType.IMAGE);

        urlMap.put(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE,ValidateCodeType.SMS);
        addUrlToMap(securityProperties.getCode().getSms().getUrls(),ValidateCodeType.SMS);

    }

    public void addUrlToMap (String urlString,ValidateCodeType type){

        if (StringUtils.isNotBlank(urlString)) {
            String[] congfigUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlString,",");
            for (String url : congfigUrls) {
                urlMap.put(url, type);
            }
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 仅拦截提交登录的请求并且是post请求
        ValidateCodeType validateCodeType = getValidateCodeType(request);
        if (validateCodeType != null) {
            try {
                validateCodeProcessorHolder.findValidateCodeProcessor(validateCodeType).validate(new ServletWebRequest(request, response));
            }catch (ValidateCodeException validateCodeException){
                authenticationFailureHandler.onAuthenticationFailure(request,
                        response,
                        validateCodeException);
                return;
            }
        }

        filterChain.doFilter(request,response);
    }

    private ValidateCodeType getValidateCodeType(HttpServletRequest request) {
        ValidateCodeType validateCodeType = null;
        if (!StringUtils.equalsIgnoreCase(request.getMethod(),"GET")) {
            Set<String> urls = urlMap.keySet();
            for (String url :urls) {
                if (antPathMatcher.match(url,request.getRequestURI())) {
                    validateCodeType = urlMap.get(url);
                    break;
                }
            }
        }
        return validateCodeType;
    }

    public void validate(ServletWebRequest request) throws ServletRequestBindingException {
       String sessionKey = SESSION_CODE_KEY+"IMAGE";
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
