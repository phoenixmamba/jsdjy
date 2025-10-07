package com.centit.logstatistics.logstatisticsserver.webmgr.service.impl;


import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.centit.logstatistics.logstatisticsserver.webmgr.po.TLmThirdlog;
import com.centit.logstatistics.logstatisticsserver.webmgr.dao.TLmThirdlogDao;
import com.centit.logstatistics.logstatisticsserver.webmgr.service.TLmThirdlogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  服务实现类
 * @Date : 2021-07-05
 **/
@Transactional
@Service
public class TLmThirdlogServiceImpl implements TLmThirdlogService {
    public static final Log log = LogFactory.getLog(TLmThirdlogService.class);

    @Resource
    private TLmThirdlogDao tLmThirdlogDao;


    /**
     * 查询列表
     */
    @Override
    public JSONObject queryPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            Integer pageNo = reqJson.get("pageNo") != null ? reqJson.getInteger("pageNo") : 1;
            Integer pageSize = reqJson.get("pageSize") != null ? reqJson.getInteger("pageSize") : 10;
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);

            bizDataJson.put("total", tLmThirdlogDao.queryTotalCount(reqMap));
            bizDataJson.put("objList", tLmThirdlogDao.queryList(reqMap));
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
