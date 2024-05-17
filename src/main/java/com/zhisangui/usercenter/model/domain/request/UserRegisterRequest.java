package com.zhisangui.usercenter.model.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 6055176074905834331L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
