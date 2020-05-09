package com.zhize.browser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


/**
 * 自定义认证
 */
@Component
public class MyUserDetailService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyUserDetailService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("登录用户名："+username);
        // 下面的用户名和密码 都是从用户里面查出来的
        return new User(username,"111111",
                // UserDetails 还有四个方法
                true,// 判断账号是否存在
                true,//
                true,//
                true, // 判断账号是否被锁住
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
