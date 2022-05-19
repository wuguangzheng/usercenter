package com.qiuyang.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author   qiuyang
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 8680454374611972021L;
    private String userAccount;
    private String userPassword;
}

