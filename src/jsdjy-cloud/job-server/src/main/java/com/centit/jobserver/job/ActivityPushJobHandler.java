package com.centit.jobserver.job;

import cn.hutool.core.date.DateUtil;
import com.centit.jobserver.dao.ArtPushDao;
import com.centit.jobserver.po.ArtPushPo;
import com.centit.jobserver.service.SlaveDataSourceService;
import com.centit.jobserver.threadPool.ThreadPoolExecutorFactory;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 21:31
 **/
@Slf4j
@Component
public class ActivityPushJobHandler {
    private final static int PUSH_TIME_RANGE = 2;
    private final static String SMS_TEMP_ID = "JSM41400-0095";

    private final static int PUSH_TYPE = 2;
    private final static int PUSH_STATUS_UNDONE = 1;
    private final static int PUSH_STATUS_DONE = 2;
    private final static int PUSH_STATUS_FAIL = -1;

    private final static int CART_TYPE_ACTIVITY = 3;
    private final static int CART_TYPE_PLAN = 9;

    @Resource
    private ArtPushDao artPushDao;
    @Resource
    private SlaveDataSourceService slaveDataSourceService;

    @XxlJob("activityPushJobHandler")
    public void activityPushJobHandler() throws Exception {
        log.info("活动消息推送定时任务开始执行...");
        List<ArtPushPo> todoList = slaveDataSourceService.selectToPushActivitys(buildParams());
        dealPushList(todoList, CART_TYPE_ACTIVITY);
        log.info("活动消息推送定时任务执行完成");
    }

    @XxlJob("planPushJobHandler")
    public void planPushJobHandler() throws Exception {
        log.info("爱艺计划消息推送定时任务开始执行...");
        List<ArtPushPo> todoList = slaveDataSourceService.selectToPushPlans(buildParams());
        dealPushList(todoList, CART_TYPE_PLAN);
        log.info("爱艺计划消息推送定时任务执行完成");
    }

    public HashMap<String, Object> buildParams() {
        HashMap<String, Object> reqMap = new HashMap<>(4);
        reqMap.put("pushType", PUSH_TYPE);
        reqMap.put("pushStatus", PUSH_STATUS_UNDONE);
        reqMap.put("startTime", DateUtil.formatDateTime(DateUtil.offsetMinute(DateUtil.date(), -PUSH_TIME_RANGE)));
        reqMap.put("endTime", DateUtil.formatDateTime(DateUtil.offsetMinute(DateUtil.date(), PUSH_TIME_RANGE)));
        return reqMap;
    }

    public void dealPushList(List<ArtPushPo> todoList, int cartType) {
        for (ArtPushPo activityPush : todoList) {
            List<String> mobiles;
            //所有下单用户
            if (activityPush.getPushRange() == 1) {
                //查询该活动所有下单用户的手机号
                mobiles = slaveDataSourceService.selectArtactivityUserMobiles(activityPush.getActivityId(), cartType);
            } else {
                String mobileStr = activityPush.getPushMobiles();
                mobiles = Arrays.asList(mobileStr.split("[;；]"));
            }
            dealMobiles(mobiles, activityPush);
        }
    }

    public void dealMobiles(List<String> mobiles, ArtPushPo activityPush) {
        ExecutorService executorService = ThreadPoolExecutorFactory.createThreadPoolExecutor();
        //手机号每100条记录用逗号拼接，以便批量推送短信
        List<String> mobileStrList = mobiles.stream().collect(Collectors.groupingBy(it -> (mobiles.indexOf(it) / 100)))
                .values().stream()
                .map(strings -> String.join(",", strings))
                .collect(Collectors.toList());
        // 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(mobileStrList.stream()
                .map(mobileStr -> CompletableFuture.runAsync(() -> {
                    try {
                        sendSms(mobileStr, activityPush.getPushContent());
                    } catch (Exception e) {
                        handleException(activityPush, e);
                    }
                }, executorService)).toArray(CompletableFuture[]::new));
        allFutures.thenRun(() -> {
            if (activityPush.getPushStatus() != PUSH_STATUS_FAIL) {
                activityPush.setDoneTime(DateUtil.now());
                activityPush.setPushStatus(PUSH_STATUS_DONE);
            }
            updatePushStatus(activityPush);
        });

    }

    private void sendSms(String mobiles, String content) {
        log.info("推送活动消息,手机号：{},短信内容：{}", mobiles, content);
        SmsUtil.sendTemplateSMS(mobiles, content, SMS_TEMP_ID);
    }

    private Void handleException(ArtPushPo activityPush, Throwable throwable) {
        log.error("活动消息推送出现异常，活动名称：{},异常信息：", activityPush.getPushContent(), throwable);
        activityPush.setPushStatus(PUSH_STATUS_FAIL);
        return null;
    }

    public void updatePushStatus(ArtPushPo activityPush) {
        if(activityPush.getActivityType()==1){
            artPushDao.updateActivityPushStatus(activityPush);
        }else {
            artPushDao.updatePlanPushStatus(activityPush);
        }
    }
}
