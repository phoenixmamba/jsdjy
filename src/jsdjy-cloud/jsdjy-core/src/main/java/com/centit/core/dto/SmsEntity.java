package com.centit.core.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 10:05
 **/
@Data
public class SmsEntity {

    @NotBlank
    private String mobile;

    @NotNull
    private String content;

    @NotBlank
    private String tempId;
}
