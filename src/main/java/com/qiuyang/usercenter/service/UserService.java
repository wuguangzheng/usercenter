package com.qiuyang.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiuyang.usercenter.model.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
* @author 秋阳
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2022-05-07 22:26:10
*/

public interface UserService extends IService<User> {



    /**
     * 用户注册
     * @param userAccount 账号
     * @param userPassword 密码
     * @param checkPassword 二次校验密码
     * @param wechatCode  微信朋友编号
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String wechatCode);

    /**
     * 用户登录
     * @param userAccount 账号
     * @param userPassword 密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
