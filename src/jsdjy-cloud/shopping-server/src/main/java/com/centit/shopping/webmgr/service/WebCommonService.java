package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-21
 **/
public interface WebCommonService {

    /**
     * 查询会员权益设置
     */
    JSONObject queryMemberShipDetail(JSONObject reqJson);

    /**
     * 保存会员权益设置
     */
    JSONObject saveMemnerShip(JSONObject reqJson);

    /**
     * 商城商品推送
     */
    JSONObject pushGoodsMsg(JSONObject reqJson);

    /**
     * 全员推送
     */
    JSONObject pushAllMsg(JSONObject reqJson);

    /**
     * 获取具有核销权限的账户列表
     */

    JSONObject allWriteOffCounts(JSONObject reqJson);

    /**
     * 查询积分赠送设置设置
     */
    JSONObject queryIntegralSetDetail(JSONObject reqJson);

    /**
     * 保存积分赠送每日总上限
     */
    JSONObject saveIntegralDailyTotal(JSONObject reqJson);

    /**
     * 判断积分赠送项名称是否可用
     */
    JSONObject checkIntegralSetName(JSONObject reqJson);

    /**
     * 新增积分赠送设置
     */
    JSONObject addIntegralSet(JSONObject reqJson);

    /**
     * 修改积分赠送配置项
     */
    JSONObject editIntegralSet(JSONObject reqJson);

    /**
     * 删除积分赠送配置项
     */
    JSONObject delIntegralSet(JSONObject reqJson);

    /**
     * 党建预约列表
     */
    JSONObject appointmentList(JSONObject reqJson);

    JSONObject testInt(JSONObject reqJson);

    /**
     * 图文内容列表
     */
    JSONObject imgtextList(JSONObject reqJson);

    /**
     * 获取图文内容可选类型
     */
    JSONObject imgtextTypes(JSONObject reqJson);
//
//    /**
//     * 判断编码是否可用
//     */
//    JSONObject checkImgtextCode(JSONObject reqJson);

    /**
     * 新增图文内容
     */
    JSONObject saveImgtext(JSONObject reqJson);

    /**
     * 编辑图文内容
     */
    JSONObject updateImgtext(JSONObject reqJson);

    /**
     * 删除图文内容
     */
    JSONObject delImgtext(JSONObject reqJson);

    /**
     * 充值活动列表
     */
    JSONObject rechargeActivityList(JSONObject reqJson);

    /**
     * 新增充值活动
     */
    JSONObject addRechargeActivity(JSONObject reqJson);

    /**
     * 编辑充值活动
     */
    JSONObject editRechargeActivity(JSONObject reqJson);

    /**
     * 上/下架充值活动
     */
    JSONObject pubRechargeActivity(JSONObject reqJson);

    /**
     * 删除充值活动
     */
    JSONObject delRechargeActivity(JSONObject reqJson);

    /**
     * 充值活动已参加用户列表
     */
    JSONObject rechargeActivityUserList(JSONObject reqJson);

    /**
     * 获取支付限额配置
     */
    JSONObject getPayLimit(JSONObject reqJson);

    /**
     * 编辑支付限额
     */
    JSONObject editPayLimit(JSONObject reqJson);

    /**
     * 获取会员资产限额规则
     */
    JSONObject getAssetRule(JSONObject reqJson);

    /**
     * 同步会员资产限额规则
     */
    JSONObject syncAssetRule(JSONObject reqJson);

    /**
     * 绑定麦座优惠码
     */
    JSONObject bindMZCoupon(JSONObject reqJson);

    /**
     * 优惠码绑定记录
     */
    JSONObject couponBindList(JSONObject reqJson);

    JSONObject couponUnBindUserList(JSONObject reqJson);

    JSONObject bindMZCouponByAddTime(JSONObject reqJson);
    /**
     * 导出注册用户
     */
    JSONObject exportRegUserList(JSONObject reqJson, HttpServletResponse response);
}
