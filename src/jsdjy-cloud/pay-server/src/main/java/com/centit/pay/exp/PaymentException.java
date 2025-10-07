package com.centit.pay.exp;

import com.centit.core.result.ResultCodeEnum;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 支付相关异常
 * @Date : 2024/10/18 9:51
 **/
public class PaymentException extends RuntimeException{
    private String code;

    public PaymentException(String message) {
        super(message);
        this.code = ResultCodeEnum.ORDER_PAY_FAIL.getCode();
    }

    public PaymentException(String code, String message) {
        super(message);
        this.code = code;
    }

    public PaymentException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    public PaymentException(ResultCodeEnum resultCodeEnum, String message) {
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
