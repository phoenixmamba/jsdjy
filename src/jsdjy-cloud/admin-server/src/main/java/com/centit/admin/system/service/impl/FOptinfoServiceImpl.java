package com.centit.admin.system.service.impl;


import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.admin.util.CommonUtil;
import com.centit.admin.system.dao.FUserinfoDao;
import com.centit.admin.system.po.FUserinfo;
import com.centit.admin.system.service.FOptinfoService;
import com.centit.admin.system.dao.FOptdefDao;
import com.centit.admin.system.dao.FOptinfoDao;
import com.centit.admin.system.po.FOptdef;
import com.centit.admin.system.po.FOptinfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p><p>
 * @version : 1.0
 * @Author : li_hao
 * @Description : 服务实现类
 * @Date : 2020-06-28
 **/
@Transactional
@Service
public class FOptinfoServiceImpl implements FOptinfoService {
    public static final Log log = LogFactory.getLog(FOptinfoService.class);

    @Resource
    private FOptinfoDao fOptinfoDao;

    @Resource
    private FOptdefDao fOptdefDao;

    @Resource
    private FUserinfoDao fUserinfoDao;

    /**
     * 查询列表
     */
    @Override
    public JSONObject sub(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            //顶层菜单
            if(null==reqMap.get("id")){
                reqMap.put("preOptId","0");
            }else{
                reqMap.put("preOptId",reqMap.get("id"));
            }
            List<FOptinfo> objList = fOptinfoDao.queryList(reqMap);
            JSONArray array = new JSONArray();
            for(FOptinfo fOptinfo:objList){
                JSONObject obj = new JSONObject();
                obj.put("id", fOptinfo.getOptId());
                obj.put("isInToolbar", fOptinfo.getIsInToolbar());
                obj.put("optCode", fOptinfo.getOptId());
                obj.put("optId", fOptinfo.getOptId());
                obj.put("icon", fOptinfo.getIcon());
                obj.put("pid", fOptinfo.getPreOptId());
                obj.put("text", fOptinfo.getOptName());
                obj.put("url", fOptinfo.getOptRoute());
                obj.put("attributes", fOptinfo.getAttributes());
                obj.put("orderInd", fOptinfo.getOrderInd());

                //查询这个菜单是否有下级菜单
                HashMap<String, Object> rMap = new HashMap<>();
                rMap.put("preOptId",fOptinfo.getOptId());
                List<FOptinfo> res = fOptinfoDao.queryList(rMap);
                if(!res.isEmpty()){
                    obj.put("state", "closed");
                }else{
                    obj.put("state", "open");
                }
                array.add(obj);
            }
            bizDataJson.put("objList",array);
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
     * 新增菜单
     */
    @Override
    public JSONObject addOptinfo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FOptinfo fOptinfo = JSON.parseObject(reqJson.toJSONString(), FOptinfo.class);
            fOptinfo.setOptType("O");
            fOptinfo.setPageType("D");
            fOptinfoDao.insert(fOptinfo);

            FOptdef fOptdef = new FOptdef();
            fOptdef.setOptCode(IdUtil.fastSimpleUUID());
            fOptdef.setOptId(fOptinfo.getOptId());
            fOptdef.setOptName("查看");
            fOptdef.setOptMethod("search");
            fOptdef.setOptUrl("/changeme");
            fOptdef.setOptMethod("查看（系统默认）");
            fOptdef.setOptReq("CRUD");
            fOptdefDao.insert(fOptdef);

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
     * 删除菜单
     */
    @Override
    public JSONObject deleteOptinfo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FOptinfo fOptinfo = JSON.parseObject(reqJson.toJSONString(), FOptinfo.class);
            fOptinfoDao.delete(fOptinfo);
            FOptdef fOptdef = new FOptdef();
            fOptdef.setOptId(fOptinfo.getOptId());
            fOptdefDao.delete(fOptdef);
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
     * 编辑菜单
     */
    @Override
    public JSONObject editOptinfo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FOptinfo fOptinfo = JSON.parseObject(reqJson.toJSONString(), FOptinfo.class);
            fOptinfoDao.update(fOptinfo);
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
     * 查询系统菜单树
     */
    @Override
    public JSONObject poweropts(String userCode, JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> rmap =new HashMap<>();

            FUserinfo fUserinfo=new FUserinfo();
            fUserinfo.setUserCode(userCode);
            fUserinfo = fUserinfoDao.queryDetail(userCode);
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("userCode",fUserinfo.getUserCode());
//            reqMap.put("unitCode",fUserinfo.getPrimaryUnit());
            reqMap.put("unitCode", CommonUtil.getUserPrimaryUnit(userCode));
            if(null !=reqJson.get("roleType")){
                reqMap.put("roleType",reqJson.getString("roleType"));
                reqMap.put("unitRoleType",reqJson.getString("roleType")+"H");
            }

            List<FOptinfo> opts = fOptinfoDao.queryUserOpt(reqMap);

            List<FOptinfo> res = new ArrayList<>();
            Set<String> sets = new HashSet<String>();
            for(FOptinfo fOptinfo:opts){
                reqMap.clear();
                reqMap.put("optId",fOptinfo.getOptId());
                List<FOptinfo> list=fOptinfoDao.queryUpOpt(reqMap);
                for(FOptinfo fo:list){
                    if(!sets.contains(fo.getOptId())){
                        res.add(fo);
                        sets.add(fo.getOptId());
                    }
                }
            }
            Collections.sort(res);


//            List<FOptinfo> list= fOptinfoDao.queryList(rmap);
            for(FOptinfo fOptinfo:res){

                HashMap<String, Object> map =new HashMap<>();
                map.put("optId",fOptinfo.getOptId());
                fOptinfo.setOptMethods(fOptdefDao.queryList(map));
            }
            JSONArray menu = new JSONArray();
            if(null!=reqJson.get("fid")){
                menuTree(res,menu,reqJson.getString("fid"));
            }else{
                menuTree(res,menu,"0");
            }

            bizDataJson.put("optList",menu);
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
     * 查询系统菜单树
     */
    @Override
    public JSONObject poweropts(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> rmap =new HashMap<>();
            List<FOptinfo> list= fOptinfoDao.queryList(rmap);
            for(FOptinfo fOptinfo:list){


                HashMap<String, Object> map =new HashMap<>();
                map.put("optId",fOptinfo.getOptId());
                fOptinfo.setOptMethods(fOptdefDao.queryList(map));
            }
            JSONArray menu = new JSONArray();
            if(null!=reqJson.get("fid")){
                menuTree(list,menu,reqJson.getString("fid"));
            }else{
                menuTree(list,menu,"0");
            }

            bizDataJson.put("optList",menu);
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

    private void menuTree(List<FOptinfo> list, JSONArray array, String fatherId) {

        for (FOptinfo fOptinfo : list) {
            String father = null;
            if (null != fOptinfo.getPreOptId()) {
                father = fOptinfo.getPreOptId();
            }
//            if (father .equals("0") && fatherId .equals("0")) {
            if (father!=null&&father .equals(fatherId)) {
                JSONObject obj = new JSONObject();
                obj.put("id", fOptinfo.getOptId());
                obj.put("isInToolbar", fOptinfo.getIsInToolbar());
                obj.put("optCode", fOptinfo.getOptId());
                obj.put("optId", fOptinfo.getOptId());
                obj.put("icon", fOptinfo.getIcon());
                obj.put("pid", fOptinfo.getPreOptId());
                obj.put("text", fOptinfo.getOptName());
                obj.put("url", fOptinfo.getOptRoute());
                obj.put("attributes", fOptinfo.getAttributes());
                obj.put("optMethods", fOptinfo.getOptMethods());


                JSONArray children = new JSONArray();
                menuTree(list, children, fOptinfo.getOptId());
                obj.put("children", children);
                array.add(obj);
            }
//            else if (father != null && fatherId != null) {
//                if (father.equals(fatherId)) {
//                    JSONObject obj = new JSONObject();
//                    obj.put("id", fOptinfo.getOptId());
//                    obj.put("isInToolbar", fOptinfo.getIsInToolbar());
//                    obj.put("optCode", fOptinfo.getOptId());
//                    obj.put("optId", fOptinfo.getOptId());
//                    obj.put("icon", fOptinfo.getIcon());
//                    obj.put("pid", fOptinfo.getPreOptId());
//                    obj.put("text", fOptinfo.getOptName());
//                    obj.put("url", fOptinfo.getOptRoute());
//                    obj.put("attributes", fOptinfo.getAttributes());
//                    obj.put("optMethods", fOptinfo.getOptMethods());
//
//                    JSONArray children = new JSONArray();
//                    menuTree(list, children, fOptinfo.getOptId());
//                    obj.put("children", children);
//                    array.add(obj);
//                }
//            }
        }

    }

    /**
     * 检查菜单编码是否可用
     */
    @Override
    public JSONObject notexists(String optId) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("optId",optId);
            List<FOptinfo> optList = fOptinfoDao.queryList(reqMap);
            if(optList.isEmpty()){
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
     * 查询菜单详情
     */
    @Override
    public JSONObject optinfo(String optId) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FOptinfo fOptinfo= new FOptinfo();
            fOptinfo.setOptId(optId);
            fOptinfo = fOptinfoDao.queryDetail(fOptinfo);
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("optId",optId);
            List<FOptdef> optMethods = fOptdefDao.queryList(reqMap);
            fOptinfo.setOptMethods(optMethods);
            bizDataJson.put("optinfo",fOptinfo);
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
     * 新增菜单操作
     */
    @Override
    public JSONObject addOptdef(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FOptdef fOptdef = JSON.parseObject(reqJson.toJSONString(), FOptdef.class);
            fOptdefDao.insert(fOptdef);
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
     * 检查操作编码是否可用
     */
    @Override
    public JSONObject defnotexists(String optCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("optCode",optCode);
            List<FOptdef> optList = fOptdefDao.queryList(reqMap);
            if(optList.isEmpty()){
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
     * 编辑菜单操作
     */
    @Override
    public JSONObject editOptdef(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FOptdef fOptdef = JSON.parseObject(reqJson.toJSONString(), FOptdef.class);
            fOptdefDao.update(fOptdef);
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
     * 删除菜单操作
     */
    @Override
    public JSONObject deleteOptdef(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FOptdef fOptdef = JSON.parseObject(reqJson.toJSONString(), FOptdef.class);
            fOptdefDao.delete(fOptdef);
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
