package com.centit.jobserver.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.exp.ThirdApiException;
import com.centit.core.result.ResultCodeEnum;
import com.centit.core.service.third.ParkService;
import com.centit.jobserver.config.db.DS;
import com.centit.jobserver.dao.ParkDataHourlyFlowDao;
import com.centit.jobserver.po.ParkDataHourlyFlowPo;
import com.centit.jobserver.service.SlaveDataSourceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 21:31
 **/
@Slf4j
@Component
public class ParkHourlyFlowJobHandler {

    @Resource
    private ParkService parkService;

    @Resource
    private ParkDataHourlyFlowDao parkDataHourlyFlowDao;

    @Resource
    private SlaveDataSourceService slaveDataSourceService;

    @DS("slaveDataSource")
    @XxlJob("parkHourlyFlowJobHandler")
//    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> parkHourlyFlowJobHandler() {
        log.info("定时任务-获取车流量数据开始执行...");
        JSONObject dataObj= fetchParkingReport();
        JSONArray statList =parseStatList(dataObj);
        saveData(statList);
        log.info("定时任务-获取车流量数据执行完成");
        return ReturnT.SUCCESS;
    }

    private JSONObject fetchParkingReport() {
        try {
            return parkService.getParkingReport();
        } catch (Exception e) {
            log.error("定时任务-获取车流量数据执行异常：", e);
            throw e;
        }
    }

    private JSONArray parseStatList(JSONObject dataObj) {
        try {
            return dataObj.getJSONArray("statList");
        } catch (JSONException e) {
            log.error("定时任务-获取车流量数据解析数据时发生JSON异常，数据对象：{}", dataObj.toJSONString());
            throw new ThirdApiException(ResultCodeEnum.PARK_REQUEST_ERROR,"获取车流量接口数据异常");
        }
    }


    public void saveData(JSONArray statList){
        List<ParkDataHourlyFlowPo> flowPoList = new ArrayList<>();
        for(int i=0;i<statList.size();i++){
            try{
                JSONObject statObj  = statList.getJSONObject(i);
                ParkDataHourlyFlowPo parkDataHourlyFlow= new ParkDataHourlyFlowPo();
                parkDataHourlyFlow.setStatTime(statObj.getString("statTime"));
                parkDataHourlyFlow.setTotalTraffic(statObj.getInteger("totalTraffic"));
                parkDataHourlyFlow.setInNum(statObj.getInteger("inNum"));
                parkDataHourlyFlow.setOutNum(statObj.getInteger("outNum"));
                parkDataHourlyFlow.setAbnormalNum(statObj.getInteger("abnormalNum"));
                flowPoList.add(parkDataHourlyFlow);
            }catch (Exception ignored) {}
        }
        parkDataHourlyFlowDao.mergeIntoDatas(flowPoList);
    }
}
