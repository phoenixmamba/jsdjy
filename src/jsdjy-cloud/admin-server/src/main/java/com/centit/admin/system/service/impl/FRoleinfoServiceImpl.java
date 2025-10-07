package com.centit.admin.system.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.admin.system.po.*;
import com.centit.admin.system.service.FRoleinfoService;
import com.centit.admin.system.dao.*;
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
 * @Date : 2020-06-23
 **/
@Transactional
@Service
public class FRoleinfoServiceImpl implements FRoleinfoService {
    public static final Log log = LogFactory.getLog(FRoleinfoService.class);

    @Resource
    private FRoleinfoDao fRoleinfoDao;

    @Resource
    private FUnitroleDao fUnitroleDao;

    @Resource
    private FUserroleDao fUserroleDao;

    @Resource
    private FRolepowerDao fRolepowerDao;

    @Resource
    private FUserinfoDao fUserinfoDao;

    @Resource
    private FUnitinfoDao fUnitinfoDao;

    /**
     * 查询角色列表
     */
    @Override
    public JSONObject queryRolePageList(String userCode, JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            bizDataJson.put("total",fRoleinfoDao.queryRolePageListCount(reqMap));
            List<FRoleinfo> roleList = fRoleinfoDao.queryRolePageList(reqMap);
            bizDataJson.put("objList",roleList);

//            //运营支撑平台查询所有类型为“G”的角色
//            if(reqJson.getString("roleType").equals("G")){
//                bizDataJson.put("total",fRoleinfoDao.queryRolePageListCount(reqMap));
//                List<FRoleinfo> roleList = fRoleinfoDao.queryRolePageList(reqMap);
//                bizDataJson.put("objList",roleList);
//            }else{
//                if(reqMap.get("type").equals("1")){ //上级管理员赋予我的
//                    FUserinfo fUserinfo=new FUserinfo();
//                    fUserinfo.setUserCode(userCode);
//                    fUserinfo = fUserinfoDao.queryDetail(fUserinfo);
//                    reqMap.put("userCode",fUserinfo.getUserCode());
//                    reqMap.put("unitCode", CommonUtil.getUserPrimaryUnit(userCode));
////                    reqMap.put("unitCode",fUserinfo.getPrimaryUnit());
//
//                    bizDataJson.put("total",fRoleinfoDao.queryToMeRolePageListCount(reqMap));
//                    List<FRoleinfo> roleList = fRoleinfoDao.queryToMeRolePageList(reqMap);
//                    bizDataJson.put("objList",roleList);
//
//                }else{  //我创建的
//                    if(!userCode.equals("u0000000")){
//                        reqMap.put("creator", userCode);
//                    }
//
//                    bizDataJson.put("total",fRoleinfoDao.queryRolePageListCount(reqMap));
//                    List<FRoleinfo> roleList = fRoleinfoDao.queryRolePageList(reqMap);
//                    bizDataJson.put("objList",roleList);
//                }
//
//            }


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
     * 查询角色列表
     */
    @Override
    public JSONObject querySysRoles(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("isValid", "T");
            reqMap.put("roleType", "G");
            bizDataJson.put("objList",fRoleinfoDao.queryList(reqMap));
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
     * 新增角色
     */
    @Override
    public JSONObject addRole(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FRoleinfo fRoleinfo = JSON.parseObject(reqJson.toJSONString(), FRoleinfo.class);
            fRoleinfo.setRoleType("G");
            fRoleinfoDao.insert(fRoleinfo);
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
     * 获取角色详情
     */
    @Override
    public JSONObject queryRoleDetail(String roleCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FRoleinfo fRoleinfo = new FRoleinfo();
            fRoleinfo.setRoleCode(roleCode);

            bizDataJson.put("data",fRoleinfoDao.queryDetail(fRoleinfo));
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
     * 编辑角色
     */
    @Override
    public JSONObject editRole(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FRoleinfo fRoleinfo = JSON.parseObject(reqJson.toJSONString(), FRoleinfo.class);
            fRoleinfo.setRoleType("G");
            fRoleinfoDao.update(fRoleinfo);
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
     * 删除字典
     */
    @Override
    public JSONObject delete(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String roleCode = reqJson.getString("roleCode");
            FRoleinfo fRoleinfo = new FRoleinfo();
            fRoleinfo.setRoleCode(roleCode);
            fRoleinfoDao.delete(fRoleinfo);
            FUnitrole fUnitrole = new FUnitrole();
            fUnitrole.setRoleCode(roleCode);
            fUnitroleDao.delete(fUnitrole);
            FUserrole fUserrole=new FUserrole();
            fUserrole.setRoleCode(roleCode);
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
     * 检查角色编码是否可用
     */
    @Override
    public JSONObject codenotexists(String roleCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("roleCode",roleCode);
            List<FRoleinfo> roleList = fRoleinfoDao.queryList(reqMap);
            if(roleList.isEmpty()){
                bizDataJson.put("result",true);
            }else{
                bizDataJson.put("result",false);
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
     * 检查角色名是否可用
     */
    @Override
    public JSONObject namenotexists(String roleName) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("roleName",roleName);
            List<FRoleinfo> roleList = fRoleinfoDao.checkName(reqMap);
            if(roleList.isEmpty()){
                bizDataJson.put("result",true);
            }else{
                bizDataJson.put("result",false);
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
     * 查询角色已有权限
     */
    @Override
    public JSONObject power(String roleCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap reqMap = new HashMap();
            reqMap.put("roleCode",roleCode);
            List<FRolepower> powerList= fRolepowerDao.queryList(reqMap);

            bizDataJson.put("objList",powerList);
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
     * 保存角色-菜单权限
     */
    @Override
    public JSONObject savePower(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String roleCode = reqJson.getString("roleCode");


            //保存之前先删除该部门角色已有的权限
            FRolepower fr=new FRolepower();
            fr.setRoleCode(roleCode);
            fRolepowerDao.delete(fr);
            JSONArray array=reqJson.getJSONArray("rolePowers");
            for(int i=0;i<array.size();i++){
                FRolepower fRolepower=new FRolepower();
                fRolepower.setRoleCode(roleCode);
                fRolepower.setOptCode(array.getJSONObject(i).getString("optCode"));

                fRolepowerDao.insert(fRolepower);
            }
            FRoleinfo fri = new FRoleinfo();
            fri.setRoleCode(roleCode);
            fRoleinfoDao.update(fri);


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
     * 获取角色用户
     */
    @Override
    public JSONObject roleusers(String roleCode, JSONObject reqJson) {
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

            bizDataJson.put("total",fUserroleDao.queryPageListCount(reqMap));
            List<FUserrole> roleList = fUserroleDao.queryPageList(reqMap);
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
     * 获取角色机构
     */
    @Override
    public JSONObject roleunits(String roleCode, JSONObject reqJson) {
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

            bizDataJson.put("total",fUnitroleDao.queryPageListCount(reqMap));
            List<FUnitrole> unitList = fUnitroleDao.queryPageList(reqMap);
            bizDataJson.put("objList",unitList);
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
     * 删除角色用户
     */
    @Override
    public JSONObject deleteUserRole(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUserrole fUserrole = JSON.parseObject(reqJson.toJSONString(), FUserrole.class);
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

//    /**
//     * 新增角色用户
//     */
//    @Override
//    public JSONObject addUserRole(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            FUserrole fUserrole = JSON.parseObject(reqJson.toJSONString(), FUserrole.class);
//            fUserroleDao.insert(fUserrole);
//            retCode = "0";
//            retMsg = "操作成功！";
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    /**
     * 新增角色用户
     */
    @Override
    public JSONObject addUserRole(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            JSONArray array= reqJson.getJSONArray("roleusers");
            for(int i=0;i<array.size();i++){


                FUserrole fUserrole = JSON.parseObject(array.getJSONObject(i).toJSONString(), FUserrole.class);
                if(null ==fUserroleDao.queryDetail(fUserrole)){
                    fUserroleDao.insert(fUserrole);
                }

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
     * 删除角色机构
     */
    @Override
    public JSONObject deleteUnitRole(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FUnitrole fUnitrole = JSON.parseObject(reqJson.toJSONString(), FUnitrole.class);
            fUnitroleDao.delete(fUnitrole);
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

//    /**
//     * 新增角色机构
//     */
//    @Override
//    public JSONObject addUnitRole(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            FUnitrole fUnitrole = JSON.parseObject(reqJson.toJSONString(), FUnitrole.class);
//            fUnitroleDao.insert(fUnitrole);
//            retCode = "0";
//            retMsg = "操作成功！";
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    /**
     * 新增角色机构
     */
    @Override
    public JSONObject addUnitRole(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            JSONArray array= reqJson.getJSONArray("roleunits");
            for(int i=0;i<array.size();i++){
                FUnitrole fUnitrole = JSON.parseObject(array.getJSONObject(i).toJSONString(), FUnitrole.class);
                fUnitroleDao.insert(fUnitrole);
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
     * 获取子部门列表
     */
    @Override
    public JSONObject getChildDeptList(String parentUnit) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("parentUnit", parentUnit);
            reqMap.put("isValid", "T");
            List<FUnitinfo> unitList = fUnitinfoDao.queryList(reqMap);

            //查询是否为叶子结点
            JSONArray unitArray = new JSONArray();
            if (unitList != null) {
                for (FUnitinfo unitinfo : unitList) {
                    FUnitinfo unit = new FUnitinfo();
                    unit.setParentUnit(unitinfo.getUnitCode());
                    unit.setIsValid("T");
                    int count = fUnitinfoDao.queryChildUnitCount(unit); //查询子部门数量

                    JSONObject unitinfoJson = JSONObject.parseObject(JSON.toJSONString(unitinfo));
                    unitinfoJson.put("isLeaf", (count > 0) ? "F" : "T");
                    unitArray.add(unitinfoJson);
                }
            }

//            bizDataJson.put("unitList", unitList);
            bizDataJson.put("unitList", unitArray);
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
     * 查询部门用户列表
     */
    @Override
    public JSONObject getDeptUserList(String unitCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("unitCode", unitCode);
            reqMap.put("isValid", "T");

            List<FUserinfo> userinfoList = fUserinfoDao.getDeptUserList(reqMap);
            bizDataJson.put("userinfoList", userinfoList);

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
