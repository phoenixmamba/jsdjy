package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>票夹<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-25
 **/
public interface TicketHolderService {

    /**
     * 查询票夹演出票列表
     */
    JSONObject queryTicketList(JSONObject reqJson);

//    /**
//     * 获取演出票凭证
//     */
//    JSONObject ticketDetail(String id);

    /**
     * 获取演出票凭证
     */
    JSONObject ticketDetail(JSONObject reqJson,String id);

    /**
     * 查询待核销商品列表
     */
    JSONObject queryGoodsList(JSONObject reqJson);

    /**
     * 获取待核销商品详情
     */
    JSONObject goodsWriteOffDetail(String gcId);

    /**
     * 查询票夹艺教活动列表
     */
    JSONObject queryActivityList(JSONObject reqJson);

    /**
     * 查询票夹爱艺计划列表
     */
    JSONObject queryPlanList(JSONObject reqJson);

    /**
     * 获取艺术活动核销详情
     */
    JSONObject actWriteOffDetail(String gcId);

    /**
     * 获取爱艺计划核销详情
     */
    JSONObject planWriteOffDetail(String gcId);

    /**
     * 获取待核销商品详情
     **/
    JSONObject toWriteOffDetail(JSONObject reqJson,String offCode);

    /**
     * 核销
     */
    JSONObject writeOff(JSONObject reqJson);

    /**
     * 查询我的可核销商品列表
     */
    JSONObject queryMyWriteOffGoodsList(JSONObject reqJson);

    /**
     * 查询我的可核销商品详情
     */
    JSONObject queryMyWriteOffGoodsDetail(JSONObject reqJson);

    /**
     * 查询我的可核销艺术活动列表
     */
    JSONObject queryMyWriteOffActivityList(JSONObject reqJson);

    /**
     * 查询我的可核销艺术活动详情
     */
    JSONObject queryMyWriteOffActivityDetail(JSONObject reqJson);

    /**
     * 查询我的可核销爱艺计划列表
     */
    JSONObject queryMyWriteOffPlanList(JSONObject reqJson);

    /**
     * 查询我的可核销爱艺计划详情
     */
    JSONObject queryMyWriteOffPlanDetail(JSONObject reqJson);

    /**
     * 查询我的可核销优惠券列表
     */
    JSONObject queryMyWriteOffCouponList(JSONObject reqJson);

    /**
     * 查询我的优惠券核销详情
     */
    JSONObject queryMyWriteOffCouponDetail(JSONObject reqJson);

    /**
     * 查询我的可核销演出项目兑换列表
     */
    JSONObject queryMyWriteOffRedeemProjectList(JSONObject reqJson);

    /**
     * 查询我的可核销演出兑换详情
     */
    JSONObject queryMyWriteOffRedeemProjectDetail(JSONObject reqJson);
}
