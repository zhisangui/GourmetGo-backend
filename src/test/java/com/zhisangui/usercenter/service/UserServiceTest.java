package com.zhisangui.usercenter.service;
import java.util.Date;

import com.zhisangui.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * @author zsg
 * 用户服务测试
 */

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("zhisangui");
        user.setUserAccount("zsg666");
        user.setAvatarUrl("https://www.baidu.com/img/flexible/logo/pc/result.png");
        user.setGender(1);
        user.setUserPassword("123456");
        user.setEmail("123");
        user.setPhone("456");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        boolean result = userService.save(user);
        System.out.println(user.getUserAccount() + "  " + user.getUserPassword());
        Assertions.assertTrue(result);
    }


    @Test
    void testRegister() {
        String userAccount = "", userPassword = "", checkPassword = "";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1L, result);

        userAccount = "zsg";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1L, result);

        userAccount = "zsg666";
        userPassword = "1234567";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1L, result);

        userAccount = userAccount + "+";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1L, result);

        userAccount = "xiaosaxiansheng";
        checkPassword = "1234567";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1L, result);

        userAccount = "userAccount";
        checkPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        System.out.println(result);
        Assertions.assertTrue(result >= 0);

    }
}