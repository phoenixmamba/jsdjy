package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>商户物流<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-21
 **/
public interface SellerTransportsService {

    /**
     * 获取商户可用运费模板
     */
    JSONObject queryStoreTransports(JSONObject reqJson);

    /**
     * 获取商户运费模板分页列表
     */
    JSONObject queryPageList(JSONObject reqJson);

    /**
     * 新增运费模板
     */
    JSONObject addTransport(JSONObject reqJson);

    /**
     * 获取运费模板详情
     */
    JSONObject transportDetail(String id);

    /**
     * 编辑运费模板
     */
    JSONObject editTransport(JSONObject reqJson);

    /**
     * 删除运费模板
     */
    JSONObject delTransport(JSONObject reqJson);
}
