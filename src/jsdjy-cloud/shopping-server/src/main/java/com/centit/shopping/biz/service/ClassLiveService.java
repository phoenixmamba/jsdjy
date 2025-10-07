package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>艺教课程开课<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-25
 **/
public interface ClassLiveService {

    /**
     * 获取UserSig
     */
    JSONObject getUserSig(JSONObject reqJson);

    /**
     * 获取艺教课程列表
     */
    JSONObject getClassList(JSONObject reqJson);

    /**
     * 校验用户是否可进入课程
     */
    JSONObject checkUser(JSONObject reqJson);
}
