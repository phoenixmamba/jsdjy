package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>充值<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-24
 **/
public interface WalletService {

    /**
     * 获取用户当前账户积分与余额
     */
    JSONObject queryAccountInfo(JSONObject reqJson);

    /**
     * 获取用户会员资产变更记录
     */
    JSONObject queryAssetRecord(JSONObject reqJson);

    /**
     * 领取账户资产
     */
    JSONObject addAsset(JSONObject reqJson);

    /**
     * 充值待支付金额计算
     */
    JSONObject renderRechargeOrder(JSONObject reqJson, HttpServletRequest request);

    /**
     * 下单
     */
    JSONObject addRechargeOrder(JSONObject reqJson, HttpServletRequest request);

    JSONObject myRechargeList(JSONObject reqJson);
}
