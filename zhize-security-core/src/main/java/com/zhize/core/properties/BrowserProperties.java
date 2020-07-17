package com.zhize.core.properties;

import lombok.Data;

@Data
public class BrowserProperties {

    private String loginPage = "/zhize-sigIn.html";

    private LoginResponseType loginType  = LoginResponseType.JSON;
    private int rememberMeSeconds = 3600;

    private SessionProperties session = new SessionProperties();

    public String getLoginPage() {
        return loginPage;
    }

    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
    }

    public LoginResponseType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginResponseType loginType) {
        this.loginType = loginType;
    }

    public int getRememberMeSeconds() {
        return rememberMeSeconds;
    }

    public void setRememberMeSeconds(int rememberMeSeconds) {
        this.rememberMeSeconds = rememberMeSeconds;
    }



}
