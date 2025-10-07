package com.centit.ticket.biz.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>实名认证信息管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-04-20
 **/
public interface TicketRealnameService {

    /**
     * 查询列表
     */
    JSONObject queryRealNameList(JSONObject reqJson);

    /**
     * 查询详情
     */
    JSONObject queryRealNameDetail(JSONObject reqJson);

    /**
     * 新增实名认证
     */
    JSONObject addRealName(JSONObject reqJson);

    /**
     * 编辑实名认证
     */
    JSONObject editRealName(JSONObject reqJson);

    /**
     * 删除实名认证
     */
    JSONObject delRealName(JSONObject reqJson);

}
