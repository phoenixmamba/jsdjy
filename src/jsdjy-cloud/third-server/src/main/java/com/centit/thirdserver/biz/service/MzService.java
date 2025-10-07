package com.centit.thirdserver.biz.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.model.OrderPayInfo;
import com.centit.core.result.Result;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 16:42
 **/
public interface MzService {
    /**
     * 获取麦座限额信息
     * @return
     */
    Result<JSONObject> getAssetRule();

    /**
     * 获取会员账户资产信息
     * @return
     */
    Result<JSONObject> getUserAccountInfo(String mzUserId);

    /**
     * 获取会员收货地址详情
     * @return
     */
    Result<JSONObject> getUserAddressDetail(String mzUserId,String addressId);

    /**
     * 获取会员收货地址列表
     * @return
     */
    Result<JSONObject> getUserAddressList(String mzUserId, int pageSize, int page);

    /**
     * 确认订单
     * @return
     */
    Result<String> confirmOrder(OrderPayInfo orderPayInfo);

    /**
     * 扣减余额
     * @return
     */
    Result<JSONObject> cutMoney(OrderPayInfo orderPayInfo);

    /**
     * 扣减积分
     * @return
     */
    Result<JSONObject> cutPoint(OrderPayInfo orderPayInfo);

    /**
     * 获取资产业务key
     * @return
     */
    Result<JSONObject> checkVerifyCode(String mzUserId,String verifyCode);
}
