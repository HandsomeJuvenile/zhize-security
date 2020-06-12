package com.zhize.browser;

import com.zhize.core.properties.SecurityProperties;
import com.zhize.core.validate.code.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;


/**
 * 配置Spring Security
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    AuthenticationSuccessHandler zhizeAuthenticationSuccessHandler;
    @Autowired
    AuthenticationFailureHandler zhizeAuthenticationFailureHandler;
    @Autowired
    ValidateCodeFilter validateCodeFilter;
    @Autowired
    DataSource dataSource;
    @Autowired
    UserDetailsService userDetailsService;

    /**
     * 设置表单登录然后拦截所有请求并认证
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        validateCodeFilter.setAuthenticationFailureHandler(zhizeAuthenticationFailureHandler);
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                    .loginPage("/authentication/require")
                    .loginProcessingUrl("/authentication/form")
                    .successHandler(zhizeAuthenticationSuccessHandler)
                    .failureHandler(zhizeAuthenticationFailureHandler)
                    .and()
                .rememberMe()
                    .tokenRepository(persistentTokenRepository())
                    .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
                    .userDetailsService(userDetailsService)
                .and()
                .authorizeRequests()
                .antMatchers("/authentication/require",
                                                            "/authentication/form",
                                                            "/code/image","/code/sms",
                        securityProperties.getBrowser().getLoginPage()).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();
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
