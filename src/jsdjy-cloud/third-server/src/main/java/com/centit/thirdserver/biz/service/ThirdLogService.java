package com.centit.thirdserver.biz.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import com.centit.thirdserver.biz.dao.ThirdApiLogDao;
import com.centit.thirdserver.biz.po.ThirdApiLogPo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/22 1:23
 **/
@Service
public class ThirdLogService {

    public final static int STATUS_SUCCESS=0;
    public final static int STATUS_FAIL=1;

    @Resource
    private ThirdApiLogDao thirdApiLogDao;

    /**
     * 保存第三方接口日志
     * @param logInfo 接口说明
     * @param reqMethod 请求方式
     * @param serverPath 接口地址
     * @param reqTime 请求时间
     * @param reqInfo 请求参数
     * @param retInfo 返回参数
     * @param status 请求结果状态
     */
    @Async("logExecutor")
    public void addThirdLog(int logType,String logInfo,String reqMethod, String serverPath, String reqTime, String reqInfo, String retInfo, boolean status) {
        try{
            ThirdApiLogPo apiLog = new ThirdApiLogPo();
            apiLog.setLogtype(logType);
            apiLog.setLoginfo(logInfo);
            apiLog.setLogip(NetUtil.getLocalhostStr());
            apiLog.setReqmethod(reqMethod);
            apiLog.setServerpath(serverPath);
            apiLog.setReqtime(reqTime);
            apiLog.setReqinfo(reqInfo);
            apiLog.setRettime(DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_MS_PATTERN));
            apiLog.setRetinfo(retInfo);
            apiLog.setStatus(status?STATUS_SUCCESS:STATUS_FAIL);

            thirdApiLogDao.insertSelective(apiLog);
        }catch (Exception ignored){}

    }
}
