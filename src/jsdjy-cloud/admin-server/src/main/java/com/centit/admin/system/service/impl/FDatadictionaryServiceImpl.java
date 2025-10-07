package com.centit.admin.system.service.impl;


import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.centit.admin.system.po.FDatadictionary;
import com.centit.admin.system.service.FDatadictionaryService;
import com.centit.admin.system.dao.FDatacatalogDao;
import com.centit.admin.system.dao.FDatadictionaryDao;
import com.centit.admin.system.po.FDatacatalog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p>数据字典：存放一些常量数据 比如出物提示信息，还有一些 代码与名称的对应表，比如 状态，角色名，头衔 等等<p>
 * @version : 1.0
 * @Author : li_hao
 * @Description : 服务实现类
 * @Date : 2020-06-30
 **/
@Transactional
@Service
public class FDatadictionaryServiceImpl implements FDatadictionaryService {
    public static final Log log = LogFactory.getLog(FDatadictionaryService.class);

    @Resource
    private FDatadictionaryDao fDatadictionaryDao;

    @Resource
    private FDatacatalogDao fDatacatalogDao;
    /**
     * 查询数据字典类型
     */
    @Override
    public JSONObject catalogStyle(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("catalogCode","CatalogStyle");
            List<FDatadictionary> catalogStyles=fDatadictionaryDao.queryList(reqMap);
            bizDataJson.put("objList",catalogStyles);
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
     * 查询字典列表
     */
    @Override
    public JSONObject queryDictionaryPageList(JSONObject reqJson) {
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

            bizDataJson.put("total",fDatacatalogDao.queryDictionaryPageListCount(reqMap));
            List<FDatacatalog> dictionaryList = fDatacatalogDao.queryDictionaryPageList(reqMap);
            bizDataJson.put("objList",dictionaryList);
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
            String catalogCode = reqJson.getString("catalogCode");
            FDatacatalog fDatacatalog= new FDatacatalog();
            fDatacatalog.setCatalogCode(catalogCode);
            fDatacatalogDao.delete(fDatacatalog);
            FDatadictionary fDatadictionary = new FDatadictionary();
            fDatadictionary.setCatalogCode(catalogCode);
            fDatadictionaryDao.delete(fDatadictionary);
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
     * 新增字典
     */
    @Override
    public JSONObject addDictionary(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FDatacatalog fDatacatalog = JSON.parseObject(reqJson.toJSONString(), FDatacatalog.class);
            fDatacatalogDao.insert(fDatacatalog);
            List<FDatadictionary> dataDictionaries = fDatacatalog.getDataDictionaries();
            for(FDatadictionary fDatadictionary:dataDictionaries){
                fDatadictionaryDao.insert(fDatadictionary);
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
     * 获取字典详情
     */
    @Override
    public JSONObject queryDictionaryDetail(String catalogCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            FDatacatalog fDatacatalog = new FDatacatalog();
            fDatacatalog.setCatalogCode(catalogCode);
            fDatacatalog = fDatacatalogDao.queryDetail(fDatacatalog);

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("catalogCode",catalogCode);
            fDatacatalog.setDataDictionaries(fDatadictionaryDao.queryList(reqMap));

            bizDataJson.put("data",fDatacatalog);
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
     * 编辑字典
     */
    @Override
    public JSONObject editDictionary(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //更新字典基本信息
            FDatacatalog fDatacatalog = JSON.parseObject(reqJson.toJSONString(), FDatacatalog.class);
            fDatacatalogDao.update(fDatacatalog);
            //先删除已有的字典项
            FDatadictionary fd = new FDatadictionary();
            fd.setCatalogCode(fDatacatalog.getCatalogCode());
            fDatadictionaryDao.delete(fd);
            //保存新的字典项
            List<FDatadictionary> dataDictionaries = fDatacatalog.getDataDictionaries();
            for(FDatadictionary fDatadictionary:dataDictionaries){
                fDatadictionaryDao.insert(fDatadictionary);
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
     * 检查字典编码是否可用
     */
    @Override
    public JSONObject notexists(String catalogCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("catalogCode",catalogCode);
            List<FDatacatalog> dictionaryList = fDatacatalogDao.queryList(reqMap);
            if(dictionaryList.isEmpty()){
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
     * 获取字典项
     */
    @Override
    public JSONObject queryDictionarys(String catalogCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("catalogCode",catalogCode);

            bizDataJson.put("data",fDatadictionaryDao.queryList(reqMap));
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
