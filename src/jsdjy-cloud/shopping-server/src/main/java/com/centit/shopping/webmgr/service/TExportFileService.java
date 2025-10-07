package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2022-07-24
 **/
public interface TExportFileService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

    /**
     * 导出文件
     */
    void exportFile(JSONObject reqJson, HttpServletResponse response);
}
