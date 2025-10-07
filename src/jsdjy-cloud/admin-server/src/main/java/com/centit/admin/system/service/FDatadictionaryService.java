package com.centit.admin.system.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-03-04
 **/
public interface FDatadictionaryService {

    /**
     * 查询数据字典类型
     */
    JSONObject catalogStyle(JSONObject reqJson);


    /**
     * 查询数据字典分页列表
     */
    JSONObject queryDictionaryPageList(JSONObject reqJson);

    /**
     * 删除数据字典
     */
    JSONObject delete(JSONObject reqJson);

    /**
     * 新增数据字典
     */
    JSONObject addDictionary(JSONObject reqJson);

    /**
     * 获取字典详情
     */
    JSONObject queryDictionaryDetail(String catalogCode);

    /**
     * 编辑数据字典
     */
    JSONObject editDictionary(JSONObject reqJson);

    /**
     * 校验字典编码是否可用
     */
    JSONObject notexists(String catalogCode);

    /**
     * 获取字典项
     */
    JSONObject queryDictionarys(String catalogCode);

}
