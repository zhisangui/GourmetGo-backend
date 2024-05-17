package com.zhisangui.usercenter.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhisangui.usercenter.exception.BusinessException;
import com.zhisangui.usercenter.model.domain.User;
import com.zhisangui.usercenter.service.UserService;
import com.zhisangui.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zhisangui.usercenter.common.ResultCode.*;
import static com.zhisangui.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author zsg
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-05-07 19:45:13
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;
    private static final String SLAT = "ZSG666";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 判断账号,密码,二次密码非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(PARAMS_ERROR, "账号或密码或二次密码为空");
        }

        // 2. 判断账号,密码长度符合要求
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(PARAMS_ERROR, "账号或密码长度不符合要求");
        }

        // 3. 判断账号不含非法字符
        String regex = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(PARAMS_ERROR, "账号含有非法字符");
        }

        //4. 判断两次密码一致
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(PARAMS_ERROR, "两次密码不一致");
        }

        // 5. 判断账号没有重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long l = userMapper.selectCount(queryWrapper);
        if (l > 0) {
            throw new BusinessException(PARAMS_ERROR, "账号已经存在了");
        }

        // 进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SLAT + userPassword).getBytes());

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(SYSTEM_ERROR);
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 判断账号,密码,二次密码非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(PARAMS_ERROR, "账号或密码为空");
        }

        // 2. 判断账号,密码长度符合要求
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(PARAMS_ERROR, "账号或密码长度不符合要求");
        }

        // 3. 判断账号不含非法字符
        String regex = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(PARAMS_ERROR, "无效账号");
        }

        // 4. 检验账号、密码是否存在
        String encryptPassword = DigestUtils.md5DigestAsHex((SLAT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user is not exists");
            throw new BusinessException(NULL_ERROR, "账号或密码错误");

        }

        // 5. 使用session对用户登录态进行记录
        User safetyUser = getSafetyUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

//        log.info(safetyUser.getUserAccount() + "      " + safetyUser.getUsername());
        // 6. 返回脱敏后的用户信息
        return safetyUser;
    }

    @Override
    public User getSafetyUser(User user) {
        if (user == null)
            return null;
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }

    @Override
    public Integer userLogout(HttpServletRequest request) {
        if (request == null)
            throw new BusinessException(PARAMS_ERROR);
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




