package com.centit.jobserver.job;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.centit.jobserver.dao.EarlywarningDao;
import com.centit.jobserver.po.EarlywarningPo;
import com.centit.jobserver.service.SlaveDataSourceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 定时预警任务
 * @Date : 2024/11/21 21:31
 **/
@Slf4j
@Component
public class EarlyWarningJobHandler {
    @Resource
    private EarlywarningDao earlywarningDao;
    @Resource
    private SlaveDataSourceService slaveDataSourceService;

    private final static int TASK_TYPE_ONCE=1;
    private final static int TASK_TYPE_YEARLY=2;
    private final static String TASK_SWITCH_OFF="off";
    private final static int TASK_WARNING_TYPE_ONCE=1;
    private final static int TASK_WARNING_TYPE_EVERYDAY=2;

    private final static String SMS_TEMP_ID = "JSM41400-0095";

    @XxlJob("earlyWarningJobHandler")
    public ReturnT<String> earlyWarningJobHandler() {
        log.info("定时预警任务开始执行...");
        HashMap<String, Object> reqMap =new HashMap<>(2);
        reqMap.put("isDelete","0");
        reqMap.put("taskSwitch","on");
        List<EarlywarningPo> taskList = slaveDataSourceService.selectList(reqMap);
        taskList.forEach(t->dealTask(t));
        log.info("定时预警任务执行完成");
        return ReturnT.SUCCESS;
    }

    public void dealTask(EarlywarningPo task) {
        Date deadLineTime = DateUtil.parseDateTime(task.getDeadline());
        //当前时间是否已超过任务截止时间
        boolean isAfterDeadline = deadLineTime.before(DateUtil.date());
        if (isTaskTypeOnce(task, deadLineTime, isAfterDeadline)) {
            //处理单论预警任务
            handleOnceTypeTask(task, deadLineTime,isAfterDeadline);
        } else if (isTaskTypeYearly(task, deadLineTime, isAfterDeadline)) {
            //处理每年预警任务
            handleYearlyTypeTask(task, deadLineTime,isAfterDeadline);
        }
    }

    /**
     * 判断单轮预警任务是否需要处理
     * 只有已经超时需要关闭，或者处于预警日期范围内的任务需要处理
     * @param task 任务信息
     * @param deadLineTime 任务截止时间
     * @param isAfterDeadline 是否超时
     * @return boolean
     */
    private boolean isTaskTypeOnce(EarlywarningPo task, Date deadLineTime, boolean isAfterDeadline) {
        return task.getTaskType() == TASK_TYPE_ONCE &&
                (isAfterDeadline || DateUtil.offsetDay(deadLineTime, -task.getAdvanceDay()).before(DateUtil.date()));
    }

    /**
     * 判断每年循环任务是否需要处理
     * @param task 任务信息
     * @param deadLineTime 任务截止时间
     * @param isAfterDeadline 是否超时
     * @return boolean
     */
    private boolean isTaskTypeYearly(EarlywarningPo task, Date deadLineTime, boolean isAfterDeadline) {
        return task.getTaskType() == TASK_TYPE_YEARLY &&
                DateUtil.year(DateUtil.date()) == DateUtil.year(deadLineTime) &&
                (isAfterDeadline || DateUtil.offsetDay(deadLineTime, -task.getAdvanceDay()).before(DateUtil.date()));
    }

    private void handleOnceTypeTask(EarlywarningPo task, Date deadLineTime,boolean isAfterDeadline) {
        //超时任务直接关闭
        //如果该任务是需要每天推送，或者只推送一次并且之前没有推送过，则需要推送消息
        if(isAfterDeadline){
            task.setTaskSwitch(TASK_SWITCH_OFF);
        } else if (task.getWarningType() == TASK_WARNING_TYPE_EVERYDAY || task.getLastSendTime() == null) {
            //推送消息
            sendSms(task);
            //只推送一次的任务，发完即关闭任务
            //对于每天推送的任务，如果当前时间向后推一天大于任务截止时间，则关闭任务
            if (task.getWarningType() == TASK_WARNING_TYPE_ONCE || DateUtil.offsetDay(DateUtil.date(), 1).after(deadLineTime)) {
                task.setTaskSwitch(TASK_SWITCH_OFF);
            }
        }
        updateTask(task);
    }

    private void handleYearlyTypeTask(EarlywarningPo task, Date deadLineTime, boolean isAfterDeadline) {
        boolean shouldSendSms = task.getWarningType() == TASK_WARNING_TYPE_EVERYDAY || (task.getLastSendTime() == null||
                DateUtil.year(DateUtil.parseDateTime(task.getLastSendTime()))==DateUtil.year(deadLineTime)-1);
        //如果任务已经超时，直接将截止时间往后推一年
        //如果该任务是需要每天推送，或者只推送一次但是之前没有推送过或者推送时间是去年，则需要推送消息
        if(isAfterDeadline){
            task.setDeadline(DateUtil.format(DateUtil.offset(deadLineTime, DateField.YEAR, 1), DatePattern.NORM_DATETIME_MS_PATTERN));
        }else if(shouldSendSms){
            sendSms(task);
            if (task.getWarningType() == TASK_WARNING_TYPE_ONCE || DateUtil.offsetDay(DateUtil.date(), 1).after(deadLineTime)) {
                task.setDeadline(DateUtil.format(DateUtil.offset(deadLineTime, DateField.YEAR, 1), DatePattern.NORM_DATETIME_MS_PATTERN));
            }
        }
        updateTask(task);
    }

    private void sendSms(EarlywarningPo task) {
        log.info("发送定时预警任务短信,手机号：{},短信内容：{}",task.getMobiles(),task.getSmsContent());
        SmsUtil.sendTemplateSMS(task.getMobiles(), task.getSmsContent(), SMS_TEMP_ID);
        task.setLastSendTime(DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_MS_PATTERN));
    }

    private void updateTask(EarlywarningPo task) {
        earlywarningDao.updateByPrimaryKeySelective(task);
    }
}
