package com.zhize.browser;

import com.zhize.browser.authentication.ZhizeAuthenticationSuccessHandler;
import com.zhize.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 配置Spring Security
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    AuthenticationSuccessHandler zhizeAuthenticationSuccessHandler;
    @Autowired
    AuthenticationFailureHandler zhizeAuthenticationFailureHandler;

    /**
     * 设置表单登录然后拦截所有请求并认证
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/authentication/require")
                .loginProcessingUrl("/authentication/form")
                .successHandler(zhizeAuthenticationSuccessHandler)
                .failureHandler(zhizeAuthenticationFailureHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/authentication/require","/authentication/form",securityProperties.getBrowser().getLoginPage()).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();
    }


}
