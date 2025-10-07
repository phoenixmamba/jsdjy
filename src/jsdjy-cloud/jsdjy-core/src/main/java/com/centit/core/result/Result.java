package com.centit.core.result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/10/18 9:00
 **/
@Data
public class Result<T> {
    public String retCode;

    public String retMsg;

    public T bizData;

    public static Result defaultSuccess() {
        Result result = new Result();
        result.setRetCode("0");
        result.setRetMsg("success");
        result.setBizData(null);
        return result;
    }

    public static <T> Result defaultSuccess(T data) {
        Result result = new Result();
        result.setRetCode("0");
        result.setRetMsg("success");
        result.setBizData(data);
        return result;
    }

    public static Result<JSONObject> jsonObjectResult(JSONObject data) {
        Result<JSONObject> result = new Result<>();
        result.setRetCode("0");
        result.setRetMsg("success");
        result.setBizData(data);
        return result;
    }

    public static Result<JSONArray> jsonArrayResult(JSONArray data) {
        Result<JSONArray> result = new Result<>();
        result.setRetCode("0");
        result.setRetMsg("success");
        result.setBizData(data);
        return result;
    }

    public static Result error(ResultCodeEnum errorCode) {
        Result result = new Result();
        result.setRetCode(errorCode.getCode());
        result.setRetMsg(errorCode.getMessage());
        return result;
    }
    public static <T> Result error(ResultCodeEnum errorCode, T data) {
        Result result = new Result();
        result.setRetCode(errorCode.getCode());
        result.setRetMsg(errorCode.getMessage());
        result.setBizData(data);
        return result;
    }

    public static Result result(String code, String message) {
        Result result = new Result();
        result.setRetCode(code);
        result.setRetMsg(message);
        return result;
    }

    public static <T> Result result(String code, String message, T data) {
        Result result = new Result();
        result.setRetCode(code);
        result.setRetMsg(message);
        result.setBizData(data);
        return result;
    }


}
