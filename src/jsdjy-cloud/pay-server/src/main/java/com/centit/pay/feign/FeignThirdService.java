package com.centit.pay.feign;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.dto.SmsEntity;
import com.centit.core.model.OrderPayInfo;
import com.centit.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 第三方服务模块接口
 * @Date : 2025/1/3 17:24
 **/
@Component
@FeignClient(value = "third")
public interface FeignThirdService {
    /**
     * 确认麦座订单
     * @param orderPayInfo 订单支付信息
     * @return 麦座订单id
     */
    @RequestMapping(value = "/mz/confirmOrder",method = RequestMethod.POST)
    Result<String> addOrder(@Valid @RequestBody OrderPayInfo orderPayInfo);

    /**
     * 扣减余额
     * @param orderPayInfo 订单支付信息
     * @return
     */
    @RequestMapping(value = "/mz/cutMoney",method = RequestMethod.POST)
    Result<JSONObject> cutMoney(@Valid @RequestBody OrderPayInfo orderPayInfo);

    /**
     * 扣减积分
     * @param orderPayInfo 订单支付信息
     * @return
     */
    @RequestMapping(value = "/mz/cutPoint",method = RequestMethod.POST)
    Result<JSONObject> cutPoint(@Valid @RequestBody OrderPayInfo orderPayInfo);

    /**
     * 获取资产验证码
     * @param mzUserId 用户ID
     * @param verifyCode 验证码
     * @return
     */
    @RequestMapping(value = "/mz/checkVerifyCode",method = RequestMethod.GET)
    Result<JSONObject> checkVerifyCode(@RequestParam("mzUserId") String mzUserId,@RequestParam("verifyCode") String verifyCode);

    /**
     * 核销优惠券
     * @param cid 优惠券id
     * @return 发送结果
     */
    @RequestMapping(value = "/crm/writeoffCoupon",method = RequestMethod.POST)
    Result<String> writeoffCoupon(@Valid @RequestBody String cid);

    /**
     * 发送模板短信
     * @param smsEntity 模板短信内容
     * @return 发送结果
     */
    @RequestMapping(value = "/sms/sendTemplateSMS",method = RequestMethod.POST)
    Result sendTemplateSMS(@Valid @RequestBody SmsEntity smsEntity);
}
