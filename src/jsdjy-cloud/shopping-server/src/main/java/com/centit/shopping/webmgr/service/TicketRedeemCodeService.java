package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>兑换码<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2022-07-18
 **/
public interface TicketRedeemCodeService {

    /**
     * 查询兑换码创建批次列表
     */
    JSONObject queryBatchList(JSONObject reqJson);

    /**
     * 创建指定数量的兑换码
     */
    JSONObject createCode(JSONObject reqJson);

    /**
     * 按批次直接删除兑换码
     */
    JSONObject deleteBatch(JSONObject reqJson);

    /**
     * 查询已创建的发卡单位列表
     */
    JSONObject queryCompanyList(JSONObject reqJson);

    /**
     * 创建单位
     */
    JSONObject createCompany(JSONObject reqJson);

    /**
     * 编辑单位
     */
    JSONObject editCompany(JSONObject reqJson);

    /**
     * 删除单位
     */
    JSONObject delCompany(JSONObject reqJson);

    /**
     * 查询演出列表
     */
    JSONObject queryProjectList(JSONObject reqJson);

    /**
     * 项目详情
     */
    JSONObject projectDetail(JSONObject reqJson);

    /**
     * 获取场馆列表
     */
    JSONObject queryVenueList(JSONObject reqJson);

    /**
     * 新增项目
     */
    JSONObject addProject(JSONObject reqJson);

    /**
     * 编辑项目
     */
    JSONObject editProject(JSONObject reqJson);

    /**
     * 删除项目
     */
    JSONObject delProject(JSONObject reqJson);

    /**
     * 获取活动可用优惠码的起始编码
     */
    JSONObject getStartCode(JSONObject reqJson);

    /**
     * 创建活动
     */
    JSONObject createActivity(JSONObject reqJson);

    /**
     * 查询兑换码活动列表
     */
    JSONObject queryActivityList(JSONObject reqJson);

    /**
     * 获取兑换码活动详情
     */
    JSONObject queryActivityDetail(JSONObject reqJson);

    /**
     * 编辑活动信息
     */
    JSONObject editActivity(JSONObject reqJson);

    /**
     * 查询活动绑卡记录
     */
    JSONObject queryActivityBindList(JSONObject reqJson);

    /**
     * 按绑定记录删除兑换码
     */
    JSONObject delCodeByBindId(JSONObject reqJson);

    /**
     * 活动追加绑卡
     */
    JSONObject acticityAddCodes(JSONObject reqJson);

    /**
     * 上/下架活动
     */
    JSONObject pubActivity(JSONObject reqJson);

    /**
     * 删除活动
     */
    JSONObject delActivity(JSONObject reqJson);

    /**
     * 查询兑换码列表
     */
    JSONObject queryCodePageList(JSONObject reqJson);

    /**
     * 批量删除兑换码
     */
    JSONObject delCodes(JSONObject reqJson);

    /**
     * 导出兑换码文件
     */
    JSONObject exportCodeFile(JSONObject reqJson);
}
