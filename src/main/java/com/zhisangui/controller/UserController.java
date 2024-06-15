package com.zhisangui.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhisangui.model.domain.User;
import com.zhisangui.model.domain.request.UserLoginRequest;
import com.zhisangui.model.domain.request.UserRegisterRequest;
import com.zhisangui.common.BaseResponse;
import com.zhisangui.common.ResponseUtil;
import com.zhisangui.exception.BusinessException;
import com.zhisangui.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.zhisangui.common.ResultCode.*;
import static com.zhisangui.constant.UserConstant.ADMIN_ROLE;
import static com.zhisangui.constant.UserConstant.USER_LOGIN_STATE;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(PARAMS_ERROR);
            }
        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {
            throw new BusinessException(PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, password, checkPassword);
        return ResponseUtil.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null)
            throw new BusinessException(NOT_LOGIN);
        User currentUser = userService.getById(user.getId());
        User safetyUser = userService.getSafetyUser(currentUser);
        return ResponseUtil.success(safetyUser);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(PARAMS_ERROR);
        }
        User loginedUser = userService.userLogin(userAccount, password, request);
        return ResponseUtil.success(loginedUser);
    }

    @PostMapping("logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null)
            throw new BusinessException(PARAMS_ERROR);
        Integer result = userService.userLogout(request);
        return ResponseUtil.success(result);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        log.info("searching...");
        if (isNotAdmin(request))
            throw new BusinessException(NO_AUTH);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper).stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResponseUtil.success(userList);
    }

    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteUser(long id, HttpServletRequest request) {
        if (isNotAdmin(request))
            throw new BusinessException(NO_AUTH);
        if (id <= 0)
            throw new BusinessException(PARAMS_ERROR);
        boolean result = userService.removeById(id);
        return ResponseUtil.success(result);
    }

    private boolean isNotAdmin(HttpServletRequest request) {
        Object object = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User)object;
        return user == null || user.getUserRole() != ADMIN_ROLE;
    }
}
