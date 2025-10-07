package com.centit.jobserver.job;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.exp.ThirdApiException;
import com.centit.core.result.ResultCodeEnum;
import com.centit.core.service.third.MzService;
import com.centit.jobserver.dao.MzAssetRuleDao;
import com.centit.jobserver.po.MzAssetRule;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 21:31
 **/
@Slf4j
@Component
public class MzJobHandler {
    @Resource
    private MzAssetRuleDao mzAssetRuleDao;
    @Resource
    private MzService mzService;

    @XxlJob("assetRuleJobHandler")
    public void assetRuleJobHandler() throws Exception {
        //从麦座获取免密限额配置
        JSONObject limitObj;
        try{
            limitObj = mzService.getAssetRule();
        }catch (Exception e){
            log.error("定时任务-从麦座获取免密限额配置发生异常：",e);
            throw e;
        }
        MzAssetRule shoppingAssetRule =parseAssetRule(limitObj);
        mzAssetRuleDao.update(shoppingAssetRule);
    }

    public MzAssetRule parseAssetRule(JSONObject limitObj){
        MzAssetRule shoppingAssetRule = new MzAssetRule();
        try{
            JSONObject pointRiskRuleObj = limitObj.getJSONObject("point_risk_rule");
            shoppingAssetRule.setPointAvoidPay(pointRiskRuleObj.getBoolean("point_avoid_pay")?"1":"0");
            //积分支付免密限额
            shoppingAssetRule.setPointAvoidLimit(Integer.valueOf(pointRiskRuleObj.getString("point_avoid_limit")));
            JSONObject accountRiskRuleObj = limitObj.getJSONObject("account_risk_rule");
            shoppingAssetRule.setAccountAvoidPay(accountRiskRuleObj.getBoolean("account_avoid_pay")?"1":"0");
            shoppingAssetRule.setAccountAvoidLimit(new BigDecimal(accountRiskRuleObj.getString("account_avoid_limit")));
        }catch (Exception e){
            log.error("定时任务-从麦座获取免密限额配置解析数据失败。麦座返回数据：{}",limitObj);
            throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR,"从麦座获取免密限额配置解析数据失败");
        }
        return shoppingAssetRule;
    }
}
