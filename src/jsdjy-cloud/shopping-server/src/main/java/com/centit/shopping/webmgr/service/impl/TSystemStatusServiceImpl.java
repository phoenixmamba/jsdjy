package com.centit.shopping.webmgr.service.impl;


import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.centit.shopping.dao.TSystemStatusDao;
import com.centit.shopping.webmgr.service.TSystemStatusService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-05-14
 **/
@Transactional
@Service
public class TSystemStatusServiceImpl implements TSystemStatusService {
    public static final Log log = LogFactory.getLog(TSystemStatusService.class);

    @Resource
    private TSystemStatusDao systemStatusDao;


    /**
     * 查询列表
     */
    @Override
    public JSONObject queryList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            Page<Object> page = PageHelper.startPage(pageNo, pageSize);
            HashMap map = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            List list = systemStatusDao.queryList(map);

            bizDataJson.put("total", page.getTotal());
            bizDataJson.put("objList", list);
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
