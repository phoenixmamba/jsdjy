package com.centit.admin.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/3 17:28
 **/
@Data
public class LoginInfo {
    @NotBlank
    private String loginName;

    @NotBlank
    private String password;
}
