package com.centit.zuulgateway.service.impl;


import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;

import com.centit.zuulgateway.po.TRoleuser;
import com.centit.zuulgateway.dao.TRoleuserDao;
import com.centit.zuulgateway.service.TRoleuserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p>移动端角色-用户关联<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2020-10-14
 **/
@Transactional
@Service
public class TRoleuserServiceImpl implements TRoleuserService {
    public static final Log log = LogFactory.getLog(TRoleuserService.class);

    @Resource
    private TRoleuserDao roleuserDao;


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
