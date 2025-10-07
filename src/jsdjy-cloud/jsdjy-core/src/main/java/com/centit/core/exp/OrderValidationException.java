package com.centit.core.exp;

import com.centit.core.result.ResultCodeEnum;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 默认异常
 * @Date : 2024/10/18 9:51
 **/
public class OrderValidationException extends RuntimeException{
    private String code;

    public OrderValidationException(String message) {
        super(message);
        this.code = ResultCodeEnum.ORDER_ADD_FAIL.getCode();
    }

    public OrderValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public OrderValidationException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    public OrderValidationException(ResultCodeEnum resultCodeEnum, String message) {
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
