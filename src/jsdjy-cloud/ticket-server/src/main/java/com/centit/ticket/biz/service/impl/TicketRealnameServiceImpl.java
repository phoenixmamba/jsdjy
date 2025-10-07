package com.centit.ticket.biz.service.impl;


import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.centit.ticket.biz.service.TicketRealnameService;
import com.centit.ticket.dao.TicketRealnameDao;
import com.centit.ticket.po.TicketRealname;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-04-20
 **/
@Transactional
@Service
public class TicketRealnameServiceImpl implements TicketRealnameService {
    public static final Log log = LogFactory.getLog(TicketRealnameService.class);

    @Resource
    private TicketRealnameDao ticketRealnameDao;


    /**
     * 查询列表
     */
    @Override
    public JSONObject queryRealNameList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId= reqJson.getString("userId");
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("userId",userId);
            List<TicketRealname> objList =  ticketRealnameDao.queryList(reqMap);

            bizDataJson.put("objList",objList);
            retCode = "0";
            retMsg = "操作成功！";
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
    public JSONObject queryRealNameDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRealname ticketRealname = JSON.parseObject(reqJson.toJSONString(), TicketRealname.class);
            ticketRealname = ticketRealnameDao.queryDetail(ticketRealname);
            bizDataJson.put("data",ticketRealname);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 新增实名认证信息
     */
    @Override
    public JSONObject addRealName(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRealname ticketRealname = JSON.parseObject(reqJson.toJSONString(), TicketRealname.class);
            ticketRealnameDao.insert(ticketRealname);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 编辑实名认证信息
     */
    @Override
    public JSONObject editRealName(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRealname ticketRealname = JSON.parseObject(reqJson.toJSONString(), TicketRealname.class);
            ticketRealnameDao.update(ticketRealname);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 删除实名认证信息
     */
    @Override
    public JSONObject delRealName(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketRealname ticketRealname = JSON.parseObject(reqJson.toJSONString(), TicketRealname.class);
            ticketRealnameDao.delete(ticketRealname);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }
}
