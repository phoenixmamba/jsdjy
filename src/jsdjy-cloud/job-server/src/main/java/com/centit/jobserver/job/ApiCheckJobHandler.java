package com.centit.jobserver.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import com.centit.core.service.third.MzService;
import com.centit.core.service.third.ParkService;
import com.centit.jobserver.dao.ApiCheckMsgLogDao;
import com.centit.jobserver.dao.EarlyWarningAdminDao;
import com.centit.jobserver.enums.ApiTypeEnum;
import com.centit.jobserver.po.ApiCheckMsgLogPo;
import com.centit.jobserver.po.EarlyWarningAdminPo;
import com.centit.jobserver.threadPool.ThreadPoolExecutorFactory;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 21:31
 **/
@Slf4j
@Component
public class ApiCheckJobHandler {
    @Resource
    private EarlyWarningAdminDao earlyWarningAdminDao;
    @Resource
    private ApiCheckMsgLogDao apiCheckMsgLogDao;
    @Resource
    private MzService mzService;
    @Resource
    private CrmService crmService;
    @Resource
    private ParkService parkService;

//    private final static int THREAD_POOL_SIZE = 3;
//    private final static ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, 0, TimeUnit.MILLISECONDS,
//            new LinkedBlockingQueue<>());

    private final static String PLATE_NO="苏A2P6P8";
    private final static String SMS_TEMP_ID = "JSM41400-0093";

    @XxlJob("apiCheckJobHandler")
    public void apiCheckJobHandler() throws Exception {
        log.info("第三方接口健康检查定时任务开始执行...");
        ExecutorService executorService = ThreadPoolExecutorFactory.createThreadPoolExecutor();

        CompletableFuture<Void> mzFuture = CompletableFuture.runAsync(() -> checkApi(mzService::getAssetRule, ApiTypeEnum.MZ), executorService)
                .exceptionally(e -> handleException(ApiTypeEnum.MZ, e));

        CompletableFuture<Void> crmFuture = CompletableFuture.runAsync(() -> checkApi(crmService::getCouponList, ApiTypeEnum.CRM), executorService)
                .exceptionally(e -> handleException(ApiTypeEnum.CRM, e));

        CompletableFuture<Void> parkFuture = CompletableFuture.runAsync(() -> checkApi(() -> parkService.checkApi(PLATE_NO), ApiTypeEnum.PARK), executorService)
                .exceptionally(e -> handleException(ApiTypeEnum.PARK, e));

        CompletableFuture.allOf(mzFuture, crmFuture, parkFuture).join();
        log.info("第三方接口健康检查定时任务执行完成");
    }

    private void checkApi(Runnable apiCheck, ApiTypeEnum apiTypeEnum) {
        try {
            apiCheck.run();
        } catch (Exception e) {
            throw new RuntimeException(apiTypeEnum.getApiName() + "接口校验异常", e);
        }
    }

    /**
     * 接口校验抛出异常时进行处理
     * @param apiTypeEnum api信息
     * @param throwable 异常信息
     * @return
     */
    private Void handleException(ApiTypeEnum apiTypeEnum, Throwable throwable) {
        log.error(apiTypeEnum.getApiName() + "接口校验异常：", throwable);
        sendWarningMessage(apiTypeEnum.getApiType(), apiTypeEnum.getApiName());
        return null;
    }

    /**
     * 发送提醒短信
     * @param apiType api类型
     * @param apiName api名称
     */
    public void sendWarningMessage(int apiType, String apiName) {
        //如果一小时内未发过短信，则发送提醒短信
        if (apiCheckMsgLogDao.selectInHourData(apiType).isEmpty()) {
            log.info("一小时内未发过短信，向负责人发送提醒短信");
            List<EarlyWarningAdminPo> admins = earlyWarningAdminDao.selectAll();
            //短信内容
            String content = "@1@=" + apiName + "(ip:" + NetUtil.getLocalhostStr() + ")";
            //发送短信
            admins.forEach(po -> SmsUtil.sendTemplateSMS(po.getPhone(), content, SMS_TEMP_ID));
            //发送记录入库
            recordSmsSend(apiType);
        }
    }

    /**
     * 提醒记录入库
     * @param apiType api类型
     */
    private void recordSmsSend(int apiType) {
        ApiCheckMsgLogPo apiCheckMsgLogPo = new ApiCheckMsgLogPo();
        apiCheckMsgLogPo.setType(apiType);
        apiCheckMsgLogPo.setSendTime(DateUtil.now());
        apiCheckMsgLogDao.insert(apiCheckMsgLogPo);
    }
}
