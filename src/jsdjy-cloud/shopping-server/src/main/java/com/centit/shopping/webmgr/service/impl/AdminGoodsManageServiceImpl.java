package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.webmgr.service.AdminGoodsManageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>商品管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-21
 **/
@Transactional
@Service
public class AdminGoodsManageServiceImpl implements AdminGoodsManageService {
    public static final Log log = LogFactory.getLog(AdminGoodsManageService.class);

    @Resource
    private ShoppingGoodsclassDao shoppingGoodsclassDao;
    @Resource
    private ShoppingGoodstypeSpecDao shoppingGoodstypeSpecDao;

    @Resource
    private ShoppingGoodstypeDao shoppingGoodstypeDao;

    @Resource
    private ShoppingGoodsspecificationDao shoppingGoodsspecificationDao;

    @Resource
    private ShoppingGoodsspecpropertyDao shoppingGoodsspecpropertyDao;

    /**
     * 查询商品分类列表
     */
    @Override
    public JSONObject queryClassPageList(JSONObject reqJson) {
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

            bizDataJson.put("total",shoppingGoodsclassDao.queryTotalCount(reqMap));
            bizDataJson.put("objList",shoppingGoodsclassDao.queryPageList(reqMap));

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
     * 获取下级分类
     */
    @Override
    public JSONObject queryChildClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("deleteStatus","0");
            bizDataJson.put("objList",shoppingGoodsclassDao.queryList(reqMap));

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
     * 查询商品类型
     */
    @Override
    public JSONObject queryGoodstypes(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("deleteStatus","0");
            bizDataJson.put("objList",shoppingGoodstypeDao.queryList(reqMap));

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
     * @Description 新增商品分类
     **/
    @Override
    public JSONObject addGoodsclass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodsclass shoppingGoodsclass = JSON.parseObject(reqJson.toJSONString(), ShoppingGoodsclass.class);

            shoppingGoodsclassDao.insert(shoppingGoodsclass);
            bizDataJson.put("id",shoppingGoodsclass.getId());
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
     * 查询商品分类详情
     */
    @Override
    public JSONObject queryGoodsclassDetail(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodsclass shoppingGoodsclass =new  ShoppingGoodsclass();
            shoppingGoodsclass.setId(id);
            bizDataJson.put("data",shoppingGoodsclassDao.queryDetail(shoppingGoodsclass));

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
     * @Description 编辑商品分类
     **/
    @Override
    public JSONObject editGoodsclass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodsclass shoppingGoodsclass = JSON.parseObject(reqJson.toJSONString(), ShoppingGoodsclass.class);
            shoppingGoodsclassDao.update(shoppingGoodsclass);

            //类型关联到所有下级分类
            if(reqJson.getBoolean("tochild")&&null !=shoppingGoodsclass.getGoodstypeId()&&!"".equals(shoppingGoodsclass.getGoodstypeId())){
                shoppingGoodsclassDao.updateChildClassType(shoppingGoodsclass);
            }
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
     * @Description 删除商品分类
     **/
    @Override
    public JSONObject delGoodsclass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id=reqJson.getString("id");
            String[] ids = id.split(",");
            for(int i=0;i<ids.length;i++){
                ShoppingGoodsclass shoppingGoodsclass = new ShoppingGoodsclass();
                shoppingGoodsclass.setId(ids[i]);
                shoppingGoodsclass.setDeleteStatus(true);
                shoppingGoodsclassDao.update(shoppingGoodsclass);
            }

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
     * 查询商品规格列表
     */
    @Override
    public JSONObject querySpecsPageList(JSONObject reqJson) {
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
            bizDataJson.put("total",shoppingGoodsspecificationDao.queryTotalCount(reqMap));
            List<ShoppingGoodsspecification> shoppingGoodsspecificationList = shoppingGoodsspecificationDao.queryList(reqMap);
            for(ShoppingGoodsspecification shoppingGoodsspecification:shoppingGoodsspecificationList){
                shoppingGoodsspecification.setPropertys(CommonUtil.getPropertys(shoppingGoodsspecification.getId()));
            }
            bizDataJson.put("objList",shoppingGoodsspecificationList);

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
     * @Description 新增商品规格
     **/
    @Override
    public JSONObject addGoodsspecification(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodsspecification shoppingGoodsspecification = JSON.parseObject(reqJson.toJSONString(), ShoppingGoodsspecification.class);
            shoppingGoodsspecificationDao.insert(shoppingGoodsspecification);
            JSONArray propertysArray= reqJson.getJSONArray("propertys");
            for(int i=0;i<propertysArray.size();i++){
                ShoppingGoodsspecproperty shoppingGoodsspecproperty = JSON.parseObject(propertysArray.getJSONObject(i).toJSONString(), ShoppingGoodsspecproperty.class);
                shoppingGoodsspecproperty.setSpecId(shoppingGoodsspecification.getId());
                shoppingGoodsspecpropertyDao.insert(shoppingGoodsspecproperty);
            }
            bizDataJson.put("id",shoppingGoodsspecification.getId());
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
     * 查询商品规格详情
     */
    @Override
    public JSONObject queryGoodsspecificationDetail(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodsspecification shoppingGoodsspecification =new  ShoppingGoodsspecification();
            shoppingGoodsspecification.setId(id);
            shoppingGoodsspecification = shoppingGoodsspecificationDao.queryDetail(shoppingGoodsspecification);
//            HashMap<String, Object> reqMap = new HashMap<>();
//            reqMap.put("specId",shoppingGoodsspecification.getId());
//            reqMap.put("deleteStatus","0");
//            shoppingGoodsspecification.setPropertys(shoppingGoodsspecpropertyDao.queryList(reqMap));
            shoppingGoodsspecification.setPropertys(CommonUtil.getPropertys(shoppingGoodsspecification.getId()));

            bizDataJson.put("data",shoppingGoodsspecification);

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
     * @Description 编辑商品规格
     **/
    @Override
    public JSONObject editGoodsspecification(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodsspecification shoppingGoodsspecification = JSON.parseObject(reqJson.toJSONString(), ShoppingGoodsspecification.class);
            shoppingGoodsspecificationDao.update(shoppingGoodsspecification);

            //先删除已配置的规格值
            ShoppingGoodsspecproperty goodsspecproperty = new ShoppingGoodsspecproperty();
            goodsspecproperty.setSpecId(shoppingGoodsspecification.getId());
            shoppingGoodsspecpropertyDao.delete(goodsspecproperty);

            JSONArray propertysArray= reqJson.getJSONArray("propertys");
            for(int i=0;i<propertysArray.size();i++){
                ShoppingGoodsspecproperty shoppingGoodsspecproperty = JSON.parseObject(propertysArray.getJSONObject(i).toJSONString(), ShoppingGoodsspecproperty.class);
                shoppingGoodsspecproperty.setSpecId(shoppingGoodsspecification.getId());
                shoppingGoodsspecpropertyDao.insert(shoppingGoodsspecproperty);
            }
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
     * @Description 删除规格
     **/
    @Override
    public JSONObject delGoodsspecification(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id=reqJson.getString("id");
            String[] ids = id.split(",");
            for(int i=0;i<ids.length;i++){
                ShoppingGoodsspecification shoppingGoodsspecification = new ShoppingGoodsspecification();
                shoppingGoodsspecification.setId(ids[i]);
                shoppingGoodsspecification.setDeleteStatus(true);
                shoppingGoodsspecificationDao.update(shoppingGoodsspecification);
            }

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
     * 查询商品类型列表
     */
    @Override
    public JSONObject queryTypePageList(JSONObject reqJson) {
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
            bizDataJson.put("total",shoppingGoodstypeDao.queryTotalCount(reqMap));
            bizDataJson.put("objList",shoppingGoodstypeDao.queryList(reqMap));

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
     * 获取初始商品规格列表
     */
    @Override
    public JSONObject querySpecifications(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("deleteStatus","0");
            bizDataJson.put("objList",shoppingGoodsspecificationDao.queryList(reqMap));

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
     * @Description 新增商品类型
     **/
    @Override
    public JSONObject addGoodstype(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodstype shoppingGoodstype = JSON.parseObject(reqJson.toJSONString(), ShoppingGoodstype.class);
            shoppingGoodstypeDao.insert(shoppingGoodstype);
            if(null !=reqJson.get("typeSpecs")){
                JSONArray typeSpecsArray= reqJson.getJSONArray("typeSpecs");
                for(int i=0;i<typeSpecsArray.size();i++){
                    ShoppingGoodstypeSpec shoppingGoodstypeSpec = JSON.parseObject(typeSpecsArray.getJSONObject(i).toJSONString(), ShoppingGoodstypeSpec.class);
                    shoppingGoodstypeSpec.setTypeId(shoppingGoodstype.getId());
                    shoppingGoodstypeSpecDao.insert(shoppingGoodstypeSpec);
                }
            }

            bizDataJson.put("id",shoppingGoodstype.getId());
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
     * 查询商品类型详情
     */
    @Override
    public JSONObject queryGoodstypeDetail(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodstype shoppingGoodstype =new  ShoppingGoodstype();
            shoppingGoodstype.setId(id);
            shoppingGoodstype = shoppingGoodstypeDao.queryDetail(shoppingGoodstype);
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("typeId",id);
            List<ShoppingGoodstypeSpec> typeSpecs = shoppingGoodstypeSpecDao.queryList(reqMap);
            List<String> specsList = new ArrayList<>();
            for(ShoppingGoodstypeSpec shoppingGoodstypeSpec:typeSpecs){
                specsList.add(shoppingGoodstypeSpec.getSpecId());
            }
            reqMap.clear();
            reqMap.put("deleteStatus","0");
            List<ShoppingGoodsspecification> specs = shoppingGoodsspecificationDao.queryList(reqMap);
            for(ShoppingGoodsspecification shoppingGoodsspecification:specs){
                shoppingGoodsspecification.setPropertys(CommonUtil.getPropertys(shoppingGoodsspecification.getId()));

                if(specsList.contains(shoppingGoodsspecification.getId())){
                    shoppingGoodsspecification.setHasChosen(true);
                }
            }

            bizDataJson.put("goodstype",shoppingGoodstype);
            bizDataJson.put("specifications",specs);

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
     * @Description 编辑商品类型
     **/
    @Override
    public JSONObject editGoodstype(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodstype shoppingGoodstype = JSON.parseObject(reqJson.toJSONString(), ShoppingGoodstype.class);
            shoppingGoodstypeDao.update(shoppingGoodstype);
            ShoppingGoodstypeSpec goodstypeSpec = new ShoppingGoodstypeSpec();
            goodstypeSpec.setTypeId(shoppingGoodstype.getId());
            shoppingGoodstypeSpecDao.delete(goodstypeSpec);
            if(null !=reqJson.get("typeSpecs")){
                JSONArray typeSpecsArray= reqJson.getJSONArray("typeSpecs");
                for(int i=0;i<typeSpecsArray.size();i++){
                    ShoppingGoodstypeSpec shoppingGoodstypeSpec = JSON.parseObject(typeSpecsArray.getJSONObject(i).toJSONString(), ShoppingGoodstypeSpec.class);
                    shoppingGoodstypeSpec.setTypeId(shoppingGoodstype.getId());
                    shoppingGoodstypeSpecDao.insert(shoppingGoodstypeSpec);
                }
            }

            bizDataJson.put("id",shoppingGoodstype.getId());
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
     * @Description 删除商品类型
     **/
    @Override
    public JSONObject delGoodstype(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id=reqJson.getString("id");
            String[] ids = id.split(",");
            for(int i=0;i<ids.length;i++){
                ShoppingGoodstype shoppingGoodstype = new ShoppingGoodstype();
                shoppingGoodstype.setId(ids[i]);
                shoppingGoodstype.setDeleteStatus(true);
                shoppingGoodstypeDao.update(shoppingGoodstype);
            }

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
