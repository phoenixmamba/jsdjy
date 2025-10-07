package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2022-07-04
 **/
public interface TicketCouponExchangeService {

    /**
     * 查询列表
     */
    JSONObject queryCouponActivityList(JSONObject reqJson);

    /**
     * 查询已创建的兑换码列表
     */
    JSONObject queryCouponCodeList(JSONObject reqJson);

    /**
     * 导出兑换码文件
     */
    void exportCouponCodeList(JSONObject reqJson, HttpServletResponse response);

    /**
     * 新增兑换码活动
     */
    JSONObject addTicketCouponActivity(JSONObject reqJson);

    /**
     * 编辑兑换码活动
     */
    JSONObject editTicketCouponActivity(JSONObject reqJson);

    /**
     * 查询麦座优惠码的优惠详情
     */
    JSONObject queryPromotionDetail(JSONObject reqJson);

    /**
     * 追加指定数量的兑换码
     */
    JSONObject addCouponCode(JSONObject reqJson);

    /**
     * 上/下架兑换码活动
     */
    JSONObject pubTicketCouponActivity(JSONObject reqJson);

    /**
     * 删除兑换码活动
     */
    JSONObject delTicketCouponActivity(JSONObject reqJson);
}
