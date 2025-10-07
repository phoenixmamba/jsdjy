package com.centit.admin.system.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.admin.system.dao.*;
import com.centit.admin.system.po.*;
import com.centit.admin.system.service.FUnitinfoService;
import com.centit.admin.util.CommonUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * <p>组织机构<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2020-06-23
 **/
@Transactional
@Service
public class FUnitinfoServiceImpl implements FUnitinfoService {
    public static final Log log = LogFactory.getLog(FUnitinfoService.class);

    @Resource
    private FUnitinfoDao fUnitinfoDao;

    @Resource
    private FUserunitDao fUserunitDao;

    @Resource
    private FUnitroleDao fUnitroleDao;

    @Resource
    private FRoleinfoDao fRoleinfoDao;

    @Resource
    private FRolepowerDao fRolepowerDao;

    /**
     * 查询列表
     */
    @Override
    public JSONObject queryUnits(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            List<FUnitinfo> units = fUnitinfoDao.queryList(reqMap);
            bizDataJson.put("units",units);
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
     * 查询列表
     */
    @Override
    public JSONObject queryList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            //id为父机构id
            if(null !=reqMap.get("id")&&!"".equals(reqMap.get("id"))){
                reqMap.put("parentUnit",reqMap.get("id"));
            }else if(null ==reqMap.get("unitName")){

                reqMap.put("parentUnit","0");
            }
            reqMap.put("isValid","T");
            List<FUnitinfo> unitinfoList = fUnitinfoDao.queryList(reqMap);
            if(null ==reqMap.get("unitName")||"".equals(reqMap.get("unitName"))){
                for(FUnitinfo fUnitinfo:unitinfoList){
                    //获取幅机构名称
                    FUnitinfo fu= CommonUtil.getUnitinfo(fUnitinfo.getParentUnit());
                    if(null != fu)
                        fUnitinfo.setPatentUnitName(fu.getUnitName());
                    //判断是否有下级机构
                    HashMap<String, Object> rMap =new HashMap<>();
                    rMap.put("parentUnit",fUnitinfo.getUnitCode());
                    rMap.put("isValid","T");
                    if(fUnitinfoDao.queryList(rMap).isEmpty()){
                        fUnitinfo.setState("open");
                    }else{
                        fUnitinfo.setState("closed");
                    }
                    fUnitinfo.setLastModifyDate(fUnitinfo.getUpdateDate());
                }
            }

            bizDataJson.put("unitinfoList",unitinfoList);
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
     * 查询部门下所有人员分页列表
     */
    @Override
    public JSONObject unitusers(String unitCode, JSONObject reqJson) {
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

            reqMap.put("unitCode",unitCode);
            reqMap.put("isValid", "T");

            bizDataJson.put("total",fUserunitDao.queryUnitUserPageListCount(reqMap));
            List<FUserunit> userList = fUserunitDao.queryUnitUserPageList(reqMap);
            bizDataJson.put("objList",userList);
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
    public JSONObject unitroles(String unitCode, JSONObject reqJson) {
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
            reqMap.put("unitCode",unitCode);

            bizDataJson.put("total",fUnitroleDao.queryPageListCount(reqMap));
            List<FUnitrole> roleList = fUnitroleDao.queryPageList(reqMap);
            bizDataJson.put("objList",roleList);
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
     * 新增部门角色
     */
    @Override
    public JSONObject addUnitrole(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUnitrole fUnitrole = JSON.parseObject(reqJson.toJSONString(), FUnitrole.class);
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("unitCode",fUnitrole.getUnitCode());
            reqMap.put("roleCode",fUnitrole.getRoleCode());
            if(!fUnitroleDao.queryList(reqMap).isEmpty()){
                retCode = "500";
                retMsg = "该角色已经关联此机构！";
            }else{
                fUnitroleDao.insert(fUnitrole);
                retCode = "0";
                retMsg = "操作成功！";
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
     * 删除部门角色
     */
    @Override
    public JSONObject deleteUnitrole(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUnitrole fUnitrole = JSON.parseObject(reqJson.toJSONString(), FUnitrole.class);
            if(null ==fUnitrole.getRoleCode()||null==fUnitrole.getUnitCode()){
                retCode = "1";
                retMsg = "参数有误！";
            }else{
                fUnitroleDao.delete(fUnitrole);
                retCode = "0";
                retMsg = "操作成功！";
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
     * 保存部门-菜单权限
     */
    @Override
    public JSONObject authorities(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //为每个部门新建一个专门的角色用于存储部门菜单权限
            String unitCode = reqJson.getString("unitCode");
            FRoleinfo fRoleinfo=new FRoleinfo();
            fRoleinfo.setRoleCode("G$"+unitCode);
            fRoleinfo.setRoleName("赋给部门"+unitCode+"的权限");
            fRoleinfo.setRoleType("H");
            fRoleinfo.setUnitCode(unitCode);
            fRoleinfo.setIsValid("T");
            fRoleinfo.setRoleDesc("赋给部门"+unitCode+"的权限");

            //保存之前先删除该部门角色已有的权限
            FRolepower fr=new FRolepower();
            fr.setRoleCode("G$"+unitCode);
            fRolepowerDao.delete(fr);
            JSONArray array=reqJson.getJSONArray("authorities");
            for(int i=0;i<array.size();i++){
                FRolepower fRolepower=new FRolepower();
                fRolepower.setRoleCode("G$"+unitCode);
                fRolepower.setOptCode(array.getJSONObject(i).getString("optCode"));

                fRolepowerDao.insert(fRolepower);
            }
            FRoleinfo fri = new FRoleinfo();
            fri.setRoleCode("G$"+unitCode);
            //如果之前已经存储过该部门相关权限，则更新即可
            if(null !=fRoleinfoDao.queryDetail(fri)){
                fRoleinfoDao.update(fRoleinfo);
            }else{
                fRoleinfoDao.insert(fRoleinfo);
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
     * 查询部门权限
     */
    @Override
    public JSONObject power(String unitCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap reqMap = new HashMap();
            reqMap.put("roleCode","G$"+unitCode);
            List<FRolepower> powerList= fRolepowerDao.queryList(reqMap);

            bizDataJson.put("rolePowers",powerList);
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
