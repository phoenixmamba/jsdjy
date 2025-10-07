package com.centit.core.exp;

import com.centit.core.result.ResultCodeEnum;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 第三方接口异常
 * @Date : 2024/12/6 10:19
 **/
public class ThirdApiException extends RuntimeException{
    private String code;
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ThirdApiException(){}

    public ThirdApiException(String message){
        super(message);
        this.code= ResultCodeEnum.THIRD_API_ERROR.getCode();
    }

    public ThirdApiException(String code,String message){
        super(message);
        this.code= code;
    }

    public ThirdApiException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code= resultCodeEnum.getCode();
    }

    public ThirdApiException(ResultCodeEnum resultCodeEnum,String message){
        super(message);
        this.code= resultCodeEnum.getCode();
    }
}
