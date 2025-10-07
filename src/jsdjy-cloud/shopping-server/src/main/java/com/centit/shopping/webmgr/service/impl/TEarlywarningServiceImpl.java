package com.centit.shopping.webmgr.service.impl;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.centit.shopping.po.ShoppingActivity;
import com.centit.shopping.po.TEarlywarning;
import com.centit.shopping.dao.TEarlywarningDao;
import com.centit.shopping.webmgr.service.TEarlywarningService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p>预警任务<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2022-06-28
 **/
@Transactional
@Service
public class TEarlywarningServiceImpl implements TEarlywarningService {
    public static final Log log = LogFactory.getLog(TEarlywarningService.class);

    @Resource
    private TEarlywarningDao tEarlywarningDao;


    /**
     * 查询预警任务列表
     */
    @Override
    public JSONObject queryTaskList(JSONObject reqJson) {
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
            reqMap.put("isDelete","0");

            bizDataJson.put("total",tEarlywarningDao.queryTotalCount(reqMap));
            List<TEarlywarning> taskList=tEarlywarningDao.queryList(reqMap);
            bizDataJson.put("objList",taskList);
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
     * 新增预警任务
     */
    @Override
    public JSONObject addWarningTask(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TEarlywarning tEarlywarning = JSONObject.parseObject(reqJson.toJSONString(), TEarlywarning.class);
            tEarlywarningDao.insert(tEarlywarning);

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
     * 编辑预警任务
     */
    @Override
    public JSONObject editWarningTask(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TEarlywarning tEarlywarning = JSONObject.parseObject(reqJson.toJSONString(), TEarlywarning.class);
            tEarlywarningDao.update(tEarlywarning);

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
     * 停止本轮预警
     */
    @Override
    public JSONObject stopThisTurnWarningTask(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = reqJson.getString("id");
            TEarlywarning tEarlywarning =new TEarlywarning();
            tEarlywarning.setId(id);
            tEarlywarning = tEarlywarningDao.queryDetail(tEarlywarning);

            if(tEarlywarning.getTaskType()==1){  //单轮预警
                //直接关闭该预警任务
                tEarlywarning.setTaskSwitch("off");
            }else if(tEarlywarning.getTaskType()==2){   //每年循环
                //停止当前年份预警
                String deadline = tEarlywarning.getDeadline();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c = Calendar.getInstance();
                c.setTime(format.parse(deadline));
                c.add(Calendar.YEAR, 1);
                Date start = c.getTime();
                String startDay = format.format(start);
                tEarlywarning.setDeadline(startDay);
            }
            tEarlywarningDao.update(tEarlywarning);

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
     * 开启/关闭预警任务
     */
    @Override
    public JSONObject closeWarningTask(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = reqJson.getString("id");
            String taskSwitch = reqJson.getString("taskSwitch");
            TEarlywarning tEarlywarning =new TEarlywarning();
            tEarlywarning.setId(id);
            tEarlywarning.setTaskSwitch(taskSwitch);
            tEarlywarningDao.update(tEarlywarning);

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
     * 删除预警任务
     */
    @Override
    public JSONObject delWarningTask(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = reqJson.getString("id");

            TEarlywarning tEarlywarning =new TEarlywarning();
            tEarlywarning.setId(id);
            tEarlywarning.setIsDelete("1");
            tEarlywarningDao.update(tEarlywarning);

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
