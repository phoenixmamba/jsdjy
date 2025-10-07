package com.centit.logstatistics.logstatisticsserver.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.logstatistics.logstatisticsserver.webmgr.dao.FUserinfoDao;
import com.centit.logstatistics.logstatisticsserver.webmgr.dao.ShoppingUserDao;
import com.centit.logstatistics.logstatisticsserver.webmgr.dao.TLmOperlogDao;
import com.centit.logstatistics.logstatisticsserver.webmgr.po.FUserinfo;
import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingUser;
import com.centit.logstatistics.logstatisticsserver.webmgr.po.TLmOperlog;
import com.centit.logstatistics.logstatisticsserver.webmgr.service.TLmOperlogService;
import com.centit.logstatistics.logstatisticsserver.webmgr.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p> <p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  服务实现类
 * @Date : 2020-05-28
 **/
@Transactional
@Service
public class TLmOperlogServiceImpl implements TLmOperlogService {
    public static final Log log = LogFactory.getLog(TLmOperlogService.class);

    @Resource
    private TLmOperlogDao tLmOperlogDao;

    @Resource
    private FUserinfoDao fUserinfoDao;

    @Resource
    private ShoppingUserDao shoppingUserDao;


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

    /**
     * @Description 新增日志
     **/
    @Override
    public JSONObject addOperLog(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TLmOperlog tLmOperlog = JSON.parseObject(reqJson.toJSONString(), TLmOperlog.class);
            tLmOperlogDao.insert(tLmOperlog);
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
     * 查询分页列表
     */
    @Override
    public JSONObject queryOperPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            Integer pageNo = reqJson.get("pageNo") != null ? reqJson.getInteger("pageNo") : 1;
            Integer pageSize = reqJson.get("pageSize") != null ? reqJson.getInteger("pageSize") : 10;
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);

            if(null !=reqJson.get("userName")&&!"".equals(reqJson.get("userName"))){
                String userName = reqJson.getString("userName");
                List<String> userIds = new ArrayList<>();
                //管理后台用户
                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("userName",userName);
                userMap.put("isValid","T");
                List<FUserinfo> fusers =  fUserinfoDao.queryList(userMap);
                for(FUserinfo fUserinfo:fusers){
                    userIds.add(fUserinfo.getUserCode());
                }
                //移动端用户
                userMap.clear();
                userMap.put("str",userName);
                userMap.put("deleteStatus","0");
                List<ShoppingUser> susers =shoppingUserDao.queryList(userMap);
                for(ShoppingUser shoppingUser:susers){
                    userIds.add(shoppingUser.getId());
                }
                if(!userIds.isEmpty()){
                    reqMap.put("userIds",userIds);
                }
            }

            bizDataJson.put("total", tLmOperlogDao.queryListTotal(reqMap));
            bizDataJson.put("logList", tLmOperlogDao.queryList(reqMap));
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
