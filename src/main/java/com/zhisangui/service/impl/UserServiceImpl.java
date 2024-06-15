package com.zhisangui.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhisangui.common.ResultCode;
import com.zhisangui.constant.UserConstant;
import com.zhisangui.exception.BusinessException;
import com.zhisangui.model.domain.User;
import com.zhisangui.service.UserService;
import com.zhisangui.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zsg
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-05-07 19:45:13
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    private static final String SLAT = "ZSG666";

    @Override
    public long userRegister(String userAccount, String password, String checkPassword) {
        // 1. 判断账号,密码,二次密码非空
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "账号或密码或二次密码为空");
        }

        // 2. 判断账号,密码长度符合要求
        if (userAccount.length() < 4 || password.length() < 8) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "账号或密码长度不符合要求");
        }

        // 3. 判断账号不含非法字符
        String regex = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "账号含有非法字符");
        }

        //4. 判断两次密码一致
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "两次密码不一致");
        }

        // 5. 判断账号没有重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long l = userMapper.selectCount(queryWrapper);
        if (l > 0) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "账号已经存在了");
        }

        // 进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SLAT + password).getBytes());

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPassword(encryptPassword);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        // 1. 判断账号,密码,二次密码非空
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "账号或密码为空");
        }

        // 2. 判断账号,密码长度符合要求
        if (userAccount.length() < 4 || password.length() < 8) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "账号或密码长度不符合要求");
        }

        // 3. 判断账号不含非法字符
        String regex = "[`~!@#$%^&*()+=|{}':;',\\\\\\\\[\\\\\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
        Matcher matcher = Pattern.compile(regex).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ResultCode.PARAMS_ERROR, "无效账号");
        }

        // 4. 检验账号、密码是否存在
        String encryptPassword = DigestUtils.md5DigestAsHex((SLAT + password).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user is not exists");
            throw new BusinessException(ResultCode.NULL_ERROR, "账号或密码错误");
        }
        // 5. 使用session对用户登录态进行记录
        User safetyUser = getSafetyUser(user);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safetyUser);

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
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }

    @Override
    public Integer userLogout(HttpServletRequest request) {
        if (request == null)
            throw new BusinessException(ResultCode.PARAMS_ERROR);
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }
}




