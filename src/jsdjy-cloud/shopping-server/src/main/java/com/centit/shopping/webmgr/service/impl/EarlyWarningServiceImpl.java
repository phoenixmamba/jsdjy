package com.centit.shopping.webmgr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.ShoppingEarlyWarningAdminDao;
import com.centit.shopping.dao.ShoppingEarlyWarningConfigDao;
import com.centit.shopping.po.ShoppingEarlyWarningAdmin;
import com.centit.shopping.po.ShoppingEarlyWarningConfig;
import com.centit.shopping.webmgr.service.EarlyWarningService;
import com.centit.shopping.webmgr.service.MovieService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Service
public class EarlyWarningServiceImpl implements EarlyWarningService {
    public static final Log log = LogFactory.getLog(EarlyWarningService.class);

    @Resource
    private ShoppingEarlyWarningAdminDao warningAdminDao;
    @Resource
    private ShoppingEarlyWarningConfigDao warningConfigDao;

    @Override
    public JSONObject adminList(JSONObject reqJSON) {
        JSONObject result = new JSONObject();
        JSONObject bizData = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            HashMap<String, Object> map = JSON.parseObject(reqJSON.toJSONString(), HashMap.class);
            int pageNo = reqJSON.getInteger("pageNo") == null ? 0 : reqJSON.getInteger("pageNo");
            int pageSize = reqJSON.getInteger("pageSize") == null ? 10 : reqJSON.getInteger("pageSize");
            Page<ShoppingEarlyWarningAdmin> page = PageHelper.startPage(pageNo, pageSize);
            List<ShoppingEarlyWarningAdmin> shoppingEarlyWarningAdmins = warningAdminDao.queryList(map);
            bizData.put("total", page.getTotal());
            bizData.put("objList", shoppingEarlyWarningAdmins);
            result.put("bizData", bizData);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    @Override
    public JSONObject addAdmin(JSONObject req) {
        JSONObject result = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            ShoppingEarlyWarningAdmin earlyWarningAdmin = JSON.parseObject(req.toJSONString(), ShoppingEarlyWarningAdmin.class);
            warningAdminDao.insert(earlyWarningAdmin);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    @Override
    public JSONObject removeAdmin(String id) {
        JSONObject result = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            ShoppingEarlyWarningAdmin earlyWarningAdmin = new ShoppingEarlyWarningAdmin();
            earlyWarningAdmin.setId(id);
            warningAdminDao.delete(earlyWarningAdmin);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    @Override
    public JSONObject adminDetail(String id) {
        JSONObject result = new JSONObject();
        JSONObject bizData = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            ShoppingEarlyWarningAdmin earlyWarningAdmin = new ShoppingEarlyWarningAdmin();
            earlyWarningAdmin.setId(id);
            earlyWarningAdmin = warningAdminDao.queryDetail(earlyWarningAdmin);
            bizData.put("data", earlyWarningAdmin);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("bizData", bizData);
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    @Override
    public JSONObject modifyAdmin(JSONObject req) {
        JSONObject result = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            ShoppingEarlyWarningAdmin earlyWarningAdmin = JSON.parseObject(req.toJSONString(), ShoppingEarlyWarningAdmin.class);
            warningAdminDao.update(earlyWarningAdmin);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    // 配置管理

    @Override
    public JSONObject configList(JSONObject reqJSON) {
        JSONObject result = new JSONObject();
        JSONObject bizData = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            HashMap<String, Object> map = JSON.parseObject(reqJSON.toJSONString(), HashMap.class);
            int pageNo = reqJSON.getInteger("pageNo") == null ? 0 : reqJSON.getInteger("pageNo");
            int pageSize = reqJSON.getInteger("pageSize") == null ? 10 : reqJSON.getInteger("pageSize");
            Page<ShoppingEarlyWarningAdmin> page = PageHelper.startPage(pageNo, pageSize);
            List<ShoppingEarlyWarningConfig> shoppingEarlyWarningConfigs = warningConfigDao.queryList(map);
            bizData.put("total", page.getTotal());
            bizData.put("objList", shoppingEarlyWarningConfigs);
            result.put("bizData", bizData);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    @Override
    public JSONObject addConfig(JSONObject req) {
        JSONObject result = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            ShoppingEarlyWarningConfig earlyWarningConfig = JSON.parseObject(req.toJSONString(), ShoppingEarlyWarningConfig.class);
            if (isIp(earlyWarningConfig.getServer())) {

                ShoppingEarlyWarningConfig dbData = warningConfigDao.queryDetailByIP(null,earlyWarningConfig.getServer());
                if (dbData != null) {
                    retMsg = "该服务器已存在！";
                } else {
                    warningConfigDao.insert(earlyWarningConfig);
                    retCode = "0";
                    retMsg = "操作成功！";
                }
            } else {
                retMsg = "IP不合法！";
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    @Override
    public JSONObject removeConfig(String id) {
        JSONObject result = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            ShoppingEarlyWarningConfig earlyWarningConfig = new ShoppingEarlyWarningConfig();
            earlyWarningConfig.setId(id);
            warningConfigDao.delete(earlyWarningConfig);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    @Override
    public JSONObject configDetail(String id) {
        JSONObject result = new JSONObject();
        JSONObject bizData = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            ShoppingEarlyWarningConfig earlyWarningConfig = new ShoppingEarlyWarningConfig();
            earlyWarningConfig.setId(id);
            earlyWarningConfig = warningConfigDao.queryDetail(earlyWarningConfig);
            bizData.put("data", earlyWarningConfig);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("bizData", bizData);
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    @Override
    public JSONObject modifyConfig(JSONObject req) {
        JSONObject result = new JSONObject();

        String retCode = "1";
        String retMsg = "操作失败！";
        try {
            ShoppingEarlyWarningConfig earlyWarningConfig = JSON.parseObject(req.toJSONString(), ShoppingEarlyWarningConfig.class);

            if (isIp(earlyWarningConfig.getServer())) {

                ShoppingEarlyWarningConfig dbData = warningConfigDao.queryDetailByIP(earlyWarningConfig.getId(),earlyWarningConfig.getServer());
                if (dbData != null) {
                    retMsg = "该服务器已存在！";
                } else {
                    warningConfigDao.update(earlyWarningConfig);
                    retCode = "0";
                    retMsg = "操作成功！";
                }
            } else {
                retMsg = "IP不合法！";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        result.put("retCode", retCode);
        result.put("retMsg", retMsg);
        return result;
    }

    private boolean isIp(String ip) {
        return ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
    }
}
