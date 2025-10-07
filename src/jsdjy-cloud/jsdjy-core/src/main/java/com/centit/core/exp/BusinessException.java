package com.centit.core.exp;

import com.centit.core.result.ResultCodeEnum;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 默认异常
 * @Date : 2024/10/18 9:51
 **/
public class BusinessException extends RuntimeException{
    private String code;

    public BusinessException(String code,String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    public BusinessException(ResultCodeEnum resultCodeEnum,String message) {
        super(message);
        this.code= resultCodeEnum.getCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
