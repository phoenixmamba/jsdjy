package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.ShoppingTransportDao;
import com.centit.shopping.po.ShoppingTransport;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.webmgr.service.SellerTransportsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-21
 **/
@Transactional
@Service
public class SellerTransportsServiceImpl implements SellerTransportsService {
    public static final Log log = LogFactory.getLog(SellerTransportsService.class);

    @Resource
    private ShoppingTransportDao shoppingTransportDao;


    /**
     * 获取商户可用的运费模板
     */
    @Override
    public JSONObject queryStoreTransports(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("storeId",CommonUtil.getSystemStore().getId());
            reqMap.put("deleteStatus","0");
            bizDataJson.put("objList",shoppingTransportDao.queryList(reqMap));

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
     * 查询商户运费模板分页列表
     */
    @Override
    public JSONObject queryPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            reqMap.put("deleteStatus","0");
            //所属商户，默认官方商户
            reqMap.put("storeId", CommonUtil.getSystemStore().getId());

            bizDataJson.put("total",shoppingTransportDao.queryTotalCount(reqMap));
            List<ShoppingTransport> goodsLiat = shoppingTransportDao.queryList(reqMap);
            bizDataJson.put("objList",goodsLiat);

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
     * @Description 新增运费模板
     **/
    @Override
    public JSONObject addTransport(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingTransport shoppingTransport = JSON.parseObject(reqJson.toJSONString(), ShoppingTransport.class);
            shoppingTransport.setStoreId(CommonUtil.getSystemStore().getId());
            shoppingTransportDao.insert(shoppingTransport);
            bizDataJson.put("id",shoppingTransport.getId());
        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 获取运费模板详情
     **/
    @Override
    public JSONObject transportDetail(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingTransport shoppingTransport = new ShoppingTransport();
            shoppingTransport.setId(id);

            bizDataJson.put("data",shoppingTransportDao.queryDetail(shoppingTransport));
        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 编辑运费模板
     **/
    @Override
    public JSONObject editTransport(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingTransport shoppingTransport = JSON.parseObject(reqJson.toJSONString(), ShoppingTransport.class);
            shoppingTransport.setStoreId(CommonUtil.getSystemStore().getId());
            shoppingTransportDao.update(shoppingTransport);
        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 删除运费模板
     **/
    @Override
    public JSONObject delTransport(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingTransport shoppingTransport = JSON.parseObject(reqJson.toJSONString(), ShoppingTransport.class);
            shoppingTransport.setDeleteStatus("1");
            shoppingTransportDao.update(shoppingTransport);
        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }
}
