package com.qiuyang.usercenter.service;

import com.qiuyang.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
 *
 * @author qiuyang
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    void testAddUser(){
        User user = new User();
        user.setUsername("dogQiuyang");
        user.setUserAccount("123");
        user.setAvatarUrl("https://profile.csdnimg.cn/D/2/6/0_weixin_57534018");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "dogqiuyang";
        String userPassword = "123456789";
        String checkPassword = "123456789";
        String wechatCode = "123";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,wechatCode);
        Assertions.assertEquals(-1,result);



    }
}