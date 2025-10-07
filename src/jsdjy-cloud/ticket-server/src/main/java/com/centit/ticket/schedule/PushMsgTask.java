package com.centit.ticket.schedule;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.dao.*;
import com.centit.ticket.feigin.JPushFeignClient;
import com.centit.ticket.po.*;
import com.centit.ticket.utils.MZService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * 麦座演出数据同步
 *
 * @author hy
 */
@Component
public class PushMsgTask {
    public static final Log log = LogFactory.getLog(PushMsgTask.class);
    @Value("${sheduleSwitch.master}")
    private Boolean  masterSwitch;

    @Value("${sheduleSwitch.pushRemindMsg}")
    private Boolean  pushRemindMsgSwitch;

    @Value("${pushRemind_time}")
    private Integer  pushRemindTime;

    @Resource
    private TicketProjectDao ticketProjectDao;

    @Resource
    private TicketEventDao ticketEventDao;

    @Resource
    private ShoppingUserDao shoppingUserDao;

    @Resource
    private TicketRemindDao ticketRemindDao;

    @Resource
    private JPushFeignClient jPushFeignClient;

    private static boolean isSwitch=false;

    @Scheduled(cron = "0 */10 * * * ?" )
    @PostConstruct
    public void scheduledTask() {
        if (masterSwitch&&pushRemindMsgSwitch&&isSwitch) {
            log.info("开始查询演出推送");
            HashMap reqMap = new HashMap<>();
            reqMap.put("pushRemindTime",pushRemindTime);
            List<TicketEvent> events = ticketEventDao.queryToPushEvents(reqMap);
            for(TicketEvent ticketEvent:events){
                String projectId = ticketEvent.getProjectId();
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);
                String projectName = ticketProject.getProjectName();
                String event_start_time = ticketEvent.getEventStartTime();
                reqMap.clear();
                reqMap.put("event_id",ticketEvent.getEventId());
                List<ShoppingUser> users = shoppingUserDao.queryPushUserList(reqMap);
                JSONObject reqJson =new JSONObject();
                List<String> mobiles = new ArrayList<>();
                for(ShoppingUser user:users){
                    mobiles.add(user.getMobile());
                }
                reqJson.put("mobiles",mobiles);
                reqJson.put("title","演出开始提醒");
                reqJson.put("message", "您购买的演出:"+projectName+"将于"+event_start_time+"开始,请及时检票进场观看");
                reqJson.put("notification", "您购买的演出"+projectName+"将于"+event_start_time+"开始,请及时检票进场观看");
                JSONObject dataJson =new JSONObject();
                dataJson.put("code","shopping");
                dataJson.put("type",0);
                dataJson.put("title","演出开始提醒");
                reqJson.put("data",dataJson);
                jPushFeignClient.pushMsg(reqJson);

                TicketRemind ticketRemind = new TicketRemind();
                ticketRemind.setProjectId(projectId);
                ticketRemindDao.insert(ticketRemind);
            }

        }

    }


}
