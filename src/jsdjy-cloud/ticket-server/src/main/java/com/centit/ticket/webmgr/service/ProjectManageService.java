package com.centit.ticket.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>演出管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-21
 **/
public interface ProjectManageService {

    /**
     * 查询演出分类
     */
    JSONObject queryTicketClass(JSONObject reqJson);

    /**
     * 编辑演出分类
     */
    JSONObject editTicketClass(JSONObject reqJson);

    /**
     * 查询演出列表
     */
    JSONObject queryProjectList(JSONObject reqJson);

    /**
     * 项目详情
     */
    JSONObject projectDetail(JSONObject reqJson);

    /**
     * 查询项目关联文创与积分商品列表
     */
    JSONObject projectGoodsList(JSONObject reqJson);

    /**
     * 新增项目关联文创与积分商品
     */
    JSONObject addProjectGoods(JSONObject reqJson);

    /**
     * 删除项目关联文创与积分商品
     */
    JSONObject delProjectGoods(JSONObject reqJson);

    /**
     * 修改项目关联文创与积分商品
     */
    JSONObject editProjectGoods(JSONObject reqJson);

//    /**
//     * 保存项目关联文创与积分商品列表
//     */
//    JSONObject saveProjectGoodsList(JSONObject reqJson);

    /**
     * 设置演出项目电子节目单
     */
    JSONObject saveProjectGuide(JSONObject reqJson);

    /**
     * 查询已设置为推荐的演出列表
     */
    JSONObject queryRecommondProjectList(JSONObject reqJson);

    /**
     * 新增推荐演出
     */
    JSONObject addRecProject(JSONObject reqJson);

    /**
     * 删除推荐演出
     */
    JSONObject delRecProject(JSONObject reqJson);

    /**
     * 修改推荐演出
     */
    JSONObject editRecProject(JSONObject reqJson);

    /**
     * 从麦座同步数据
     */
    JSONObject syncData(JSONObject reqJson);

    /**
     * 演出消息推送
     */
    JSONObject pushProjectMsg(JSONObject reqJson);
}
