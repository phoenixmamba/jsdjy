package com.centit.core.exp;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.result.ResultCodeEnum;
import com.centit.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 全局异常处理
 * @Date : 2024/10/18 8:55
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 系统异常
     * @param e 异常信息
     * @return Result
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    public Result exceptionHandler(Exception e){
        log.error("系统内部异常:",e);
        return Result.error(ResultCodeEnum.SYSTEM_EXCEPTION,e.getMessage());
    }

    /**
     * 处理空指针的异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, NullPointerException e) {
        log.error("发生空指针异常！原因是:", e);
        return Result.error(ResultCodeEnum.SYSTEM_EXCEPTION);
    }


    /**
     * 请求参数校验异常处理
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        JSONObject dataObj = new JSONObject();
        bindingResult.getFieldErrors().forEach(error->{
            dataObj.put(error.getField(),error.getDefaultMessage());
        });
        return Result.error(ResultCodeEnum.PARAMS_CHECK_ERROR,dataObj);
    }

    /**
     * 处理运行时异常
     *
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletRequest req, RuntimeException e) {
        log.error("捕获到运行时异常:", e);
        return Result.error(ResultCodeEnum.SYSTEM_EXCEPTION);
    }

//    @ExceptionHandler(value = ServiceException.class)
//    @ResponseBody
//    public Result exceptionHandler(ServiceException e){
//        return Result.error(ErrorCode.SYSTEM_EXCEPTION);
//    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public Result exceptionHandler(BusinessException businessException, Exception e) {
        log.info("捕获到业务异常:",businessException);
        return Result.result(businessException.getCode(),businessException.getMessage());
    }

    @ExceptionHandler(value = ThirdApiException.class)
    @ResponseBody
    public Result exceptionHandler(ThirdApiException thirdApiException, Exception e) {
        log.info("捕获到第三方接口异常:",thirdApiException);
        return Result.result(thirdApiException.getCode(),thirdApiException.getMessage());
    }
}
