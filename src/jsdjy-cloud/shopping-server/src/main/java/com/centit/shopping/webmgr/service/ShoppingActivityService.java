package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-05-27
 **/
public interface ShoppingActivityService {

    /**
     * 查询活动分页列表
     */
    JSONObject queryPageList(JSONObject reqJson);

    /**
     * 查询活动详情
     */
    JSONObject queryActicityDetail(String acId);

    /**
     * 创建活动
     */
    JSONObject addActivity(JSONObject reqJson);

    /**
     * 编辑活动
     */
    JSONObject editActivity(JSONObject reqJson);

    /**
     * 上/下架活动
     */
    JSONObject pubActivity(JSONObject reqJson);

    /**
     * 删除活动
     */
    JSONObject delActivity(JSONObject reqJson);

    /**
     * 查询生日活动已关联优惠券
     */
    JSONObject queryBirthCoupons(JSONObject reqJson);

    /**
     * 编辑生日活动关联优惠券
     */
    JSONObject editBirthCoupons(JSONObject reqJson);

    /**
     * 查询新人活动已关联优惠券
     */
    JSONObject queryNewCoupons(JSONObject reqJson);

    /**
     * 编辑新人活动关联优惠券
     */
    JSONObject editNewCoupons(JSONObject reqJson);
}
