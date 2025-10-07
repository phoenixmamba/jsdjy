package com.centit.pay.biz.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>银联支付<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-01-11
 **/
public interface UnionPayService {

    /**
     * 测试订单提交
     */
    JSONObject testPay(JSONObject reqJson);

}
