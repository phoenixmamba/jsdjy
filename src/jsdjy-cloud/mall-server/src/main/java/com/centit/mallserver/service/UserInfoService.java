package com.centit.mallserver.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.mallserver.model.UserAccountInfo;
import com.centit.mallserver.model.UserInfo;
import com.centit.mallserver.po.ShoppingAssetRulePo;
import com.centit.mallserver.po.ShoppingPayLimitPo;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 用户信息服务
 * @Date : 2025/8/26 13:57
 **/
public interface UserInfoService {
    /**
     * 获取用户基本信息
     * @return
     */
    UserInfo getUserInfo(String userId);

    /**
     * 获取用户账户信息
     * @return
     */
    UserAccountInfo getUserAccountInfo(String mzUserId);

    /**
     * 获取用户资产规则
     * @return
     */
    ShoppingPayLimitPo getPayLimit();

    /**
     * 获取用户资产规则
     */
    ShoppingAssetRulePo getAssetRule();

    /**
     * 获取用户收货地址
     * @return
     */
    JSONObject getUserAddress(String addressId, String mzUserId);

    /**
     * 获取用户会员等级
     * @param userInfo 用户信息
     * @return
     */
    BigDecimal getUserMemberShip(UserInfo userInfo);
}
