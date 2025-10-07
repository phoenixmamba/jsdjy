package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>商城首页<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-25
 **/
public interface HomeService {

    /**
     * 查询商城首页推荐商品列表
     */
    JSONObject homeGoodsList(JSONObject reqJson);

    /**
     * 收藏
     */
    JSONObject addFavorite(JSONObject reqJson, HttpServletRequest request);

    /**
     * 取消收藏
     */
    JSONObject cancelFavorite(JSONObject reqJson, HttpServletRequest request);

    /**
     * 我的收藏列表
     */
    JSONObject myFavList(JSONObject reqJson);

    /**
     * 我的浏览轨迹列表
     */
    JSONObject myHistoryList(JSONObject reqJson);

    /**
     * 清除历史轨迹
     */
    JSONObject clearHistory(JSONObject reqJson, HttpServletRequest request);

    /**
     * 全局搜素
     */
    JSONObject allSearch(JSONObject reqJson);

    /**
     * 获取搜索热词
     */
    JSONObject hotSearchWords(JSONObject reqJson);

    /**
     * 领取优惠券
     */
    JSONObject grantCoupon(JSONObject reqJson, HttpServletRequest request);

    /**
     * 首页活动弹框
     */
    JSONObject homeActivity(JSONObject reqJson);

    /**
     * 一键领取活动优惠券
     */
    JSONObject grantActivityCoupon(JSONObject reqJson, HttpServletRequest request);

    /**
     * 活动列表
     */
    JSONObject activityList(JSONObject reqJson);

    /**
     * 我的优惠券
     */
     JSONObject userCouponList(JSONObject reqJson);

    /**
     * 我的商城优惠券
     */
    JSONObject myShoppingCouponList(JSONObject reqJson);

    /**
     * 我的停车券
     */
    JSONObject myParkCouponList(JSONObject reqJson);

    /**
     * 我的演出券
     */
    JSONObject myTicketCouponList(JSONObject reqJson);

    /**
     * 我的消费券
     */
    JSONObject myConsumeCouponList(JSONObject reqJson);

    /**
     * 领券中心
     */
    JSONObject couponCenter(JSONObject reqJson);

    /**
     * 每日签到
     */
    JSONObject dailySign(JSONObject reqJson);

    /**
     * 赠送积分
     */
    JSONObject addIntegral(JSONObject reqJson);

    /**
     * 提交党建预约
     */
    JSONObject addAppointment(JSONObject reqJson);

    /**
     * 我的党建预约列表
     */
    JSONObject myAppointmentList(JSONObject reqJson);

    /**
     * 获取图文内容
     */
    JSONObject getImgtext(JSONObject reqJson);

    /**
     * 兑换优惠码
     */
    JSONObject exchangeCouponCode(JSONObject reqJson);

    /**
     * 获取我的兑换码记录
     */
    JSONObject getMyExchangeCodeRecords(JSONObject reqJson);

    /**
     * 注销用户
     */
    JSONObject userCancellation(JSONObject reqJson);
}
