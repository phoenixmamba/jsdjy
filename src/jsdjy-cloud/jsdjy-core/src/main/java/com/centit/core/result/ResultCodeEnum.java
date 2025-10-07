package com.centit.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 错误码为字符串类型，共 5 位，分成两个部分：错误产生来源+四位数字编号。错误产生来源分为 1/2/3，
 * 1 表示错误来源于用户，比如参数错误，用户安装版本过低，用户支付超时等问题；
 * 2 表示错误来源于当前系统，往往是业务逻辑出错，或程序健壮性差等问题；
 * 3 表示错误来源于第三方服务，比如 麦座、CRM、速停车、短信平台等；
 * 四位数字编号从 0001 到 9999，大类之间的步长间距预留 100
 * @Date : 2024/10/18 9:48
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ResultCodeEnum {

    /**
     * 用户不存在
     */
    USER_NOT_EXIST("10002","用户不存在"),
    /**
     * 请求参数校验失败
     */
    PARAMS_CHECK_ERROR("10003","参数校验失败"),
    /**
     * 订单渲染失败
     */
    ORDER_RENDER_FAIL("10101","订单渲染失败"),
    /**
     * 订单提交失败
     */
    ORDER_ADD_FAIL("10102","订单提交失败"),
    /**
     * 订单支付异常
     */
    ORDER_PAY_FAIL("10103","订单支付异常"),
    /**
     * 管理后台登录失败
     */
    ADMIN_LOGIN_FAIL("10201","管理后台登录失败"),
    /**
     * 系统内部异常
     */
    SYSTEM_EXCEPTION("20001", "系统内部异常"),
    /**
     * 数据查询异常
     */
    QUERY_EXCEPTION("20002", "数据查询失败"),
    /**
     * 数据更新异常
     */
    UPDATE_EXCEPTION("20003", "数据更新失败"),
    /**
     * 获取分布式锁失败
     */
    LOCK_UNGET_EXCEPTION("20004", "当前访问人数太多，请稍后再试"),
    /**
     * 定时任务异常
     */
    JOB_EXCEPTION("20102", "定时任务异常"),

    /**
     * 第三方接口异常
     */
    THIRD_API_ERROR("30001", "第三方接口异常"),

    /**
     * 调用麦座接口异常
     */
    MZ_REQUEST_ERROR("30101", "调用麦座接口异常"),
    /**
     * 调用CRM接口异常
     */
    CRM_REQUEST_ERROR("30102", "调用CRM接口异常"),

    /**
     * 调用速停车接口异常
     */
    PARK_REQUEST_ERROR("30103", "调用速停车接口异常"),
    /**
     * 短信发送失败
     */
    SMS_FAILED("30201", "短信发送失败"),

    /**
     * 微信支付接口超时
     */
    WEIXIN_PAY_TIMEOUT("30003", "微信支付相关接口超时"),
    ;
    private String code;
    private String message;
}

