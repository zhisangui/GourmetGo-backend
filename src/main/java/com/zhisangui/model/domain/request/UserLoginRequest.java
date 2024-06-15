package com.zhisangui.model.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 4390672516715736503L;
    private String userAccount;
    private String password;
}
