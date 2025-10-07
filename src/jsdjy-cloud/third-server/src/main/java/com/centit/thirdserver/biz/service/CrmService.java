package com.centit.thirdserver.biz.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.result.Result;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 17:02
 **/
public interface CrmService {
    /**
     * 获取优惠券列表
     * @return
     */
    Result<JSONObject> getCouponList();

    /**
     * 获取优惠券详情
     * @param id
     * @return
     */
    JSONObject getCouponDtl(String id);

    /**
     * 获取会员优惠券
     * @param userId
     * @param regPhone
     * @param flag
     * @return
     */
    Result<JSONArray> getUserCouponList(String userId, String regPhone, String flag);

    /**
     * 优惠券核销
     * @param cid 优惠券id
     */
    void writeoffCoupon(String cid);
}
