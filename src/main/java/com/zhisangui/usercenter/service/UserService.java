package com.zhisangui.usercenter.service;

import com.zhisangui.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author zsg
* @description 针对表【user】的数据库操作Service
* @createDate 2024-05-07 19:45:13
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount 用户名
     * @param userPassword 密码
     * @param checkPassword 二次密码
     * @return 用户ID
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount 用户名
     * @param userPassword 密码
     * @param request 请求对象
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     *
     * @param user 需要脱敏的用户
     * @return 脱敏后的用户
     */

    User getSafetyUser(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    Integer userLogout(HttpServletRequest request);
}
