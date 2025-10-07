package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>艺教课程开课<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-25
 **/
public interface ArtClassLiveService {

    /**
     * 获取UserSig
     */
    JSONObject getUserSig(JSONObject reqJson);

    /**
     * 获取艺教课程列表
     */
    JSONObject getClassList(JSONObject reqJson);

    /**
     * 获取老师信息
     */
    JSONObject getTeacherInfo(JSONObject reqJson);

    /**
     * 开课
     */
    JSONObject openClass(JSONObject reqJson);

    /**
     * 下课
     */
    JSONObject closeClass(JSONObject reqJson);

    /**
     * 获取课程具有听课权限的用户列表
     */
    JSONObject getClassUsers(JSONObject reqJson);

    /**
     * 获取课程可选用户列表
     */
    JSONObject getClassToSelectUsers(JSONObject reqJson);

    /**
     * 删除额外用户
     */
    JSONObject delClassExtraUser(JSONObject reqJson);

    /**
     * 添加额外用户
     */
    JSONObject addClassExtraUser(JSONObject reqJson);
}
