package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>兑换码<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2022-07-18
 **/
public interface TicketRedeemService {

    /**
     * 查询演出列表
     */
    JSONObject queryProjectList(JSONObject reqJson);

    /**
     * 项目详情
     */
    JSONObject projectDetail(JSONObject reqJson);

    /**
     * 兑换项目
     */
    JSONObject exchangeProject(JSONObject reqJson);

    /**
     * 查询我的兑换记录
     */
    JSONObject queryMyExchangeList(JSONObject reqJson);

    /**
     * 获取兑换详情
     */
    JSONObject queryMyExchangeDetail(JSONObject reqJson);

    /**
     * 刷新核销码
     */
    JSONObject refreshOffCode(JSONObject reqJson);
}
