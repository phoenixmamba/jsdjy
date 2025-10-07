package com.centit.ticket.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>会员地址管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-04-08
 **/
public interface AddressService {

    /**
     * 查询列表
     */
    JSONObject queryAddressList(JSONObject reqJson);

    JSONObject queryAddressDetail(JSONObject reqJson);

    JSONObject setDefaultDetail(JSONObject reqJson);

    JSONObject addAddress(JSONObject reqJson);

    JSONObject editAddress(JSONObject reqJson);

    JSONObject removeAddress(JSONObject reqJson);

    JSONObject queryAreaCode(JSONObject reqJson);
}
