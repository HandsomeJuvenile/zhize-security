package com.zhize.browser;

import com.zhize.browser.support.AbstractChannelSecurityConfig;
import com.zhize.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.zhize.core.properties.SecurityConstants;
import com.zhize.core.properties.SecurityProperties;
import com.zhize.core.validate.ValidateCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.sql.DataSource;


/**
 * 配置Spring Security
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class BrowserSecurityConfig extends AbstractChannelSecurityConfig {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    AuthenticationSuccessHandler zhizeAuthenticationSuccessHandler;
    @Autowired
    AuthenticationFailureHandler zhizeAuthenticationFailureHandler;
    @Autowired
    ValidateCodeSecurityConfig validateCodeSecurityConfig;
    @Autowired
    SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
    @Autowired
    DataSource dataSource;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    InvalidSessionStrategy invalidSessionStrategy;
    @Autowired
    SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    /**
     * 设置表单登录然后拦截所有请求并认证
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        applyPasswordAuthenticationConfig(http);

        http.apply(validateCodeSecurityConfig)
                    .and()
                .apply(smsCodeAuthenticationSecurityConfig)
                    .and()
                .rememberMe()
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
                    .userDetailsService(userDetailsService)
                    .and()
                .sessionManagement()
                    .invalidSessionStrategy(invalidSessionStrategy)  // session 失效的策略，跳转到新的页面或者返回json数据
                    .maximumSessions(securityProperties.getBrowser().getSession().getMaximumSessions()) // 设置一个账号最多可以几个人同时登录
                    .maxSessionsPreventsLogin(securityProperties.getBrowser().getSession().isMaxSessionsPreventsLogin()) // 是否阻止用户二次登录
                    .expiredSessionStrategy(sessionInformationExpiredStrategy) // session 失效的策略
                    .and()
                .and()
                .authorizeRequests()
                .antMatchers(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
                                                            SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM,
                                                           SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/*",
                                                    securityProperties.getBrowser().getLoginPage(),
                        securityProperties.getBrowser().getSession().getSessionInvalidUrl()
                ).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable()
                .apply(smsCodeAuthenticationSecurityConfig);
    }

    /**
     * 密码加密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 记住我
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        //tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }


}
