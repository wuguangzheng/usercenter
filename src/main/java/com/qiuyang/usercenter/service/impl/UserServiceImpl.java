package com.qiuyang.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiuyang.usercenter.common.ErrorCode;
import com.qiuyang.usercenter.exception.BusinessException;
import com.qiuyang.usercenter.model.domain.User;
import com.qiuyang.usercenter.service.UserService;
import com.qiuyang.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qiuyang.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author 秋阳
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2022-05-07 22:26:10
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    private static final String SALT = "qiuyang";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String wechatCode) {
        // 1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,wechatCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if(wechatCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"微信朋友编号过长");
        }
        // 账户不能包含特殊字符，正则表达式
        String validPattern = "[`~!@#$%^&()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {

            return -1;

        }
        // 密码和校验密码相同
        if(!userPassword.equals(checkPassword)){
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 在数据库查询是否已经存在
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }
        // 微信朋友编号不能重复
        queryWrapper = new QueryWrapper<>();
        // 在数据库查询是否已经存在
        queryWrapper.eq("wechatCode", wechatCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"微信朋友编号已存在");
        }
        // 2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3、插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setWechatCode(wechatCode);
        boolean saveResult = this.save(user);
        // 因为方法返回为long，而User类定义id为Long，如果id为null则会出现拆箱错误，所以要判断
        if(!saveResult){
            return  -1;
        }
        return user.getId();
    }


    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8 ) {
            return null;
        }
        // 账户不能包含特殊字符，正则表达式
        String validPattern = "[`~!@#$%^&()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 在数据库查询是否已经存在
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if(user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }

        // 3、用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4、记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setWechatCode(originUser.getWechatCode());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


}




