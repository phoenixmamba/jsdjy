package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-21
 **/
public interface AdminCouponService {

    /**
     * 同步优惠券
     **/
    JSONObject syncCoupons(JSONObject reqJson);

    /**
     * 创建优惠券
     */
    JSONObject createCoupon(JSONObject reqJson);

    /**
     * 创建兑换券券
     */
    JSONObject createWriteOffCoupon(JSONObject reqJson);

    /**
     * 编辑优惠券
     */
    JSONObject editCoupon(JSONObject reqJson);

    /**
     * 查询优惠券列表（全部）
     */
    JSONObject queryAllCouponPageList(JSONObject reqJson);

    /**
     * 查询线上可发放的优惠券列表
     */
    JSONObject queryOnlineCouponPageList(JSONObject reqJson);


    /**
     * 查询优惠券列表（过滤兑换券）
     */
    JSONObject queryCouponPageList(JSONObject reqJson);

    /**
     * 查询兑换券列表
     */
    JSONObject queryWriteOffCouponPageList(JSONObject reqJson);

    /**
     * 设置兑换券的核销账户
     */
    JSONObject editWriteOffCount(JSONObject reqJson);

    /**
     * 删除优惠券
     */
    JSONObject delCoupon(JSONObject reqJson);

    /**
     * 上/下架优惠券
     */
    JSONObject pubCoupon(JSONObject reqJson);

    /**
     * 设置优惠券单人限领数量
     */
    JSONObject setCouponLimit(JSONObject reqJson);


    /**
     * 获取优惠券详情
     */
    JSONObject queryCouponDetail(String right_No);


    /**
     * 获取优惠券已关联的商品/分类
     */
    JSONObject queryCouponRelation(String right_No);

    /**
     * 保存优惠券关联商品/分类
     */
    JSONObject saveCouponRelation(JSONObject reqJson);

    /**
     * 删除优惠券关联商品/分类
     */
    JSONObject delCouponRelation(JSONObject reqJson);

    /**
     * 查询优惠券指定优惠券发放记录
     */
    JSONObject queryCouponGrantPageList(JSONObject reqJson);

    /**
     * 查询优惠券指定优惠券消费记录
     */
    JSONObject queryCouponRecordPageList(JSONObject reqJson);

    /**
     * 查询已配置的可直接发放的优惠券列表
     */
    JSONObject queryDirectgrantCoupons(JSONObject reqJson);

    /**
     * 添加直接发放的优惠券
     */
    JSONObject addDirectgrantCoupon(JSONObject reqJson);

    /**
     * 删除直接发放的优惠券
     */
    JSONObject delDirectgrantCoupon(JSONObject reqJson);

    /**
     * 直接发放优惠码
     */
    JSONObject directGrantCoupon(JSONObject reqJson);
}
