package com.qiuyang.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 *
 * @author   qiuyang
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 8680454374611972021L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String wechatCode;
}

