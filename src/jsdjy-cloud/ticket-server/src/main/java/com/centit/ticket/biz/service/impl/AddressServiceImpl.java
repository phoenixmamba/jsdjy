package com.centit.ticket.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.biz.service.AddressService;
import com.centit.ticket.biz.service.TicketProjectService;
import com.centit.ticket.common.enums.Const;
import com.centit.ticket.dao.*;
import com.centit.ticket.po.*;
import com.centit.ticket.utils.*;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaDamaiMzOrderCreateRequest;
import com.taobao.api.request.AlibabaDamaiMzOrderRenderRequest;
import com.taobao.api.response.AlibabaDamaiMzOrderCreateResponse;
import com.taobao.api.response.AlibabaDamaiMzOrderRenderResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * <p>收货地址管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-04-08
 **/
@Transactional
@Service
public class AddressServiceImpl implements AddressService {
    public static final Log log = LogFactory.getLog(AddressService.class);

    @Resource
    private TicketAreacodeDao ticketAreacodeDao;

    /**
     * 查询列表
     */
    @Override
    public JSONObject queryAddressList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            String userId = reqJson.getString("userId");
            String mzUserId= CommonUtil.getMzUserId(userId);
            JSONObject dataObj = MZService.getUserAddress(mzUserId,pageSize,pageNo);
            if(null !=dataObj){
                bizDataJson.put("total",dataObj.getInteger("total_row"));
                bizDataJson.put("objList",dataObj.getJSONObject("data_list").get("user_address_detail_v_o"));
                retCode = "0";
                retMsg = "操作成功！";
            }else{
                retMsg = "从麦座获取会员地址信息失败！";
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询详情
     */
    @Override
    public JSONObject queryAddressDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String address_id = reqJson.getString("address_id");
            String mzUserId= CommonUtil.getMzUserId(userId);
            JSONObject dataObj = MZService.getAddressDetail(mzUserId,address_id);
            if(null !=dataObj){
                bizDataJson.put("data",dataObj);
                retCode = "0";
                retMsg = "操作成功！";
            }else{
                retMsg = "从麦座获取会员地址详情失败！";
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 设置默认地址
     */
    @Override
    public JSONObject setDefaultDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String address_id = reqJson.getString("address_id");
            String mzUserId= CommonUtil.getMzUserId(userId);
            if(MZService.defaultAddress(mzUserId,address_id)){
                retCode = "0";
                retMsg = "操作成功！";
            }
            else{
                retMsg = "设置会员默认地址失败！";
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取省/市/区编码
     */
    @Override
    public JSONObject queryAreaCode(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String,Object> reqMap = new HashMap<>();
            List<TicketAreacode> objList= ticketAreacodeDao.queryList(reqMap);
            for(TicketAreacode ticketAreacode:objList){
                reqMap.put("parentCode",ticketAreacode.getCode());
                List<TicketAreacode> cList= ticketAreacodeDao.queryList(reqMap);
                for(TicketAreacode cTicketAreacode:cList){
                    reqMap.put("parentCode",cTicketAreacode.getCode());
                    List<TicketAreacode> qList= ticketAreacodeDao.queryList(reqMap);
                    cTicketAreacode.setChildList(qList);
                }
                ticketAreacode.setChildList(cList);
            }
            bizDataJson.put("objList",objList);


        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 新增地址
     */
    @Override
    public JSONObject addAddress(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId= CommonUtil.getMzUserId(userId);
            String receiver_name = reqJson.getString("receiver_name");
            String receiver_phone = reqJson.getString("receiver_phone");
            String province_code = reqJson.getString("province_code");
            String city_code = reqJson.getString("city_code");
            String area_code = reqJson.getString("area_code");
            String address = reqJson.getString("address");
            String post_code = reqJson.get("post_code")==null?null:reqJson.getString("post_code");

            if(MZService.addAddress(mzUserId,receiver_name,receiver_phone,province_code,city_code,area_code,address,post_code)){
                retCode = "0";
                retMsg = "操作成功！";
            }
            else{
                retMsg = "新增收货地址失败！";
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 修改地址
     */
    @Override
    public JSONObject editAddress(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId= CommonUtil.getMzUserId(userId);
            String address_id = reqJson.getString("address_id");
            String receiver_name = reqJson.getString("receiver_name");
            String receiver_phone = reqJson.getString("receiver_phone");
            String province_code = reqJson.getString("province_code");
            String city_code = reqJson.getString("city_code");
            String area_code = reqJson.getString("area_code");
            String address = reqJson.getString("address");
            String post_code = reqJson.get("post_code")==null?null:reqJson.getString("post_code");

            if(MZService.editAddress(mzUserId,address_id,receiver_name,receiver_phone,province_code,city_code,area_code,address,post_code)){
                retCode = "0";
                retMsg = "操作成功！";
            }
            else{
                retMsg = "修改收货地址失败！";
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 删除地址
     */
    @Override
    public JSONObject removeAddress(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId= CommonUtil.getMzUserId(userId);
            String address_id = reqJson.getString("address_id");

            if(MZService.removeAddress(mzUserId,address_id)){
                retCode = "0";
                retMsg = "操作成功！";
            }
            else{
                retMsg = "删除收货地址失败！";
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }
}
