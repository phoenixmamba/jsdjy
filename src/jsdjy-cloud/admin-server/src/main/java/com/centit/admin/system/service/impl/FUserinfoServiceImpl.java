package com.centit.admin.system.service.impl;


import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.admin.config.SystemConfig;
import com.centit.admin.system.dao.FUserinfoDao;
import com.centit.admin.system.dao.FUserroleDao;
import com.centit.admin.system.dao.FUserunitDao;
import com.centit.admin.system.po.FUserinfo;
import com.centit.admin.system.po.FUserrole;
import com.centit.admin.system.po.FUserunit;
import com.centit.admin.system.service.FUserinfoService;
import com.centit.admin.util.CommonUtil;
import com.centit.admin.util.passwordEncoder.CentitPasswordEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2020-06-23
 **/
@Transactional
@Service
public class FUserinfoServiceImpl implements FUserinfoService {
    public static final Log log = LogFactory.getLog(FUserinfoService.class);

    @Resource
    private FUserinfoDao fUserinfoDao;

    @Resource
    private FUserunitDao fUserunitDao;

    @Resource
    private FUserroleDao fUserroleDao;

    @Resource
    private CentitPasswordEncoder centitPasswordEncoder;

    @Resource
    private SystemConfig systemConfig;

    /**
     * 查询列表
     */
    @Override
    public JSONObject queryUserPageList(JSONObject reqJson) {
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
            bizDataJson.put("total", fUserinfoDao.queryPageListCount(reqMap));
            List<FUserinfo> userList = fUserinfoDao.queryPageList(reqMap);
            bizDataJson.put("userList", userList);
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
     * 校验用户登录名是否可用
     */
    @Override
    public JSONObject checkLoginName(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            List<FUserinfo> userList = fUserinfoDao.checkLoginName(reqMap);
            if(userList.isEmpty()){
                bizDataJson.put("result", true);
            }else{
                bizDataJson.put("result", false);
            }
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
     * 新增后台用户
     */
    @Override
    public JSONObject addUser(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUserinfo fUserinfo = JSON.parseObject(reqJson.toJSONString(), FUserinfo.class);
            String userCode = IdUtil.fastSimpleUUID();
            fUserinfo.setUserCode(userCode);
            fUserinfo.setUserPwd(systemConfig.getInitalPwdFUser());
            fUserinfo.setUserPin(centitPasswordEncoder.createPassword(systemConfig.getInitalPwdFUser(), userCode));
            fUserinfoDao.insert(fUserinfo);

            FUserunit fUserunit = new FUserunit();
            fUserunit.setUserCode(userCode);
            fUserunit.setUnitCode(fUserinfo.getPrimaryUnit());
            fUserunit.setIsPrimary("T");
            fUserunitDao.insert(fUserunit);
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
     * 编辑后台用户
     */
    @Override
    public JSONObject editUser(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUserinfo fUserinfo = JSON.parseObject(reqJson.toJSONString(), FUserinfo.class);
            fUserinfoDao.update(fUserinfo);

            FUserunit fUserunit = new FUserunit();
            fUserunit.setUserCode(fUserinfo.getUserCode());
            fUserunitDao.delete(fUserunit);

            fUserunit.setUnitCode(fUserinfo.getPrimaryUnit());
            fUserunit.setIsPrimary("T");
            fUserunitDao.insert(fUserunit);
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
     * 删除后台用户
     */
    @Override
    public JSONObject deleteUser(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUserinfo fUserinfo = JSON.parseObject(reqJson.toJSONString(), FUserinfo.class);
            fUserinfoDao.delete(fUserinfo);

            FUserunit fUserunit = new FUserunit();
            fUserunit.setUserCode(fUserinfo.getUserCode());
            fUserunitDao.delete(fUserunit);

            FUserrole fUserrole = new FUserrole();
            fUserrole.setUserCode(fUserinfo.getUserCode());
            fUserroleDao.delete(fUserrole);

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
     * 获取用户信息
     */
    @Override
    public JSONObject getUserInfo(String userCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            FUserinfo fUserinfo = new FUserinfo();
            fUserinfo.setUserCode(userCode);
            fUserinfo = fUserinfoDao.queryDetail(userCode);
            bizDataJson.put("userInfo", fUserinfo);

            FUserunit fUserunit = new FUserunit();
            fUserunit.setUserCode(userCode);
            fUserunit.setUnitCode(CommonUtil.getUserPrimaryUnit(userCode));
//            fUserunit.setUnitCode(fUserinfo.getPrimaryUnit());
            bizDataJson.put("userUnit", fUserunitDao.queryDetail(fUserunit));
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
     * 查询部门下所有角色列表
     */
    @Override
    public JSONObject getUserUnitPageList(String userCode, JSONObject reqJson) {
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
            reqMap.put("userCode", userCode);

            bizDataJson.put("total", fUserunitDao.queryUnitUserPageListCount(reqMap));
            List<FUserunit> unitList = fUserunitDao.queryUnitUserPageList(reqMap);
            bizDataJson.put("unitList", unitList);
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
     * 重置密码
     */
    @Override
    public JSONObject resetPwd(String userCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUserinfo fUserinfo = new FUserinfo();
            fUserinfo.setUserCode(userCode);
            fUserinfo.setUserPwd(systemConfig.getInitalPwdFUser());
            fUserinfo.setUserPin(centitPasswordEncoder.createPassword(systemConfig.getInitalPwdFUser(), userCode));
            fUserinfoDao.update(fUserinfo);
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
