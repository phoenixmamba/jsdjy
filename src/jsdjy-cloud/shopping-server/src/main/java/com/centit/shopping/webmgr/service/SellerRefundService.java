package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>退货管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-05-08
 **/
public interface SellerRefundService {

    /**
     * 查询退货分页列表
     */
    JSONObject queryPageList(JSONObject reqJson);

    /**
     * 查看退货详情
     */
    JSONObject refundDetail(String refundId);

    /**
     * 通过/不通过退货
     */
    JSONObject adminRefund(JSONObject reqJson);

}
