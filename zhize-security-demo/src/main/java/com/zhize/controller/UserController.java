package com.zhize.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.zhize.dto.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

public class UserController {

    @GetMapping(value = "/user")
    @JsonView(User.UserSimpleView.class)
    public User queryUser(String userName) {
        User user = new User();
        user.setUsername(userName);
        user.setPasssword("111111");
        return user;
    }

    @GetMapping(value = "one")
    public User findUser(String id){
       /* throw new UserException(id);*/
        User user = new User();
        user.setUsername("who");
        user.setPasssword("111111");
        return user;
    }

}
