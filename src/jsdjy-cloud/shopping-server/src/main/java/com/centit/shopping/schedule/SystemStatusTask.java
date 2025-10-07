package com.centit.shopping.schedule;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.ShoppingEarlyWarningAdminDao;
import com.centit.shopping.dao.ShoppingEarlyWarningConfigDao;
import com.centit.shopping.dao.TSystemStatusDao;
import com.centit.shopping.po.ShoppingEarlyWarningAdmin;
import com.centit.shopping.po.ShoppingEarlyWarningConfig;
import com.centit.shopping.po.TSystemStatus;
import com.centit.shopping.utils.HttpSendUtil;
import com.centit.shopping.utils.SystemStatusUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/8/25 14:25
 * @description ：摄像头数据同步
 */
@Component
public class SystemStatusTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemStatusTask.class);

    @Resource
    private TSystemStatusDao systemStatusDao;
    @Resource
    private ShoppingEarlyWarningConfigDao warningConfigDao;
    @Resource
    private ShoppingEarlyWarningAdminDao warningAdminDao;

    private static final String url ="http://api.jsmsxx.com:8030/service/httpService/httpInterface.do";


    @Value("${sheduleSwitch.master}")
    private Boolean masterSwitch;

    @Value("${sheduleSwitch.systemStatus}")
    private Boolean systemStatus;

    @Scheduled(cron = "${systemStatusCorn}")
    public void scheduledCronDemo() {
        //定时任务开关
        if (masterSwitch && systemStatus) {
            LOGGER.info("服务器监控-定时任务开始");
            //  ip从数据库取
            List<ShoppingEarlyWarningConfig> shoppingEarlyWarningConfigs = warningConfigDao.queryList(null);
            List<ShoppingEarlyWarningAdmin> admins =warningAdminDao.queryList(new HashMap<>());
            for (ShoppingEarlyWarningConfig config : shoppingEarlyWarningConfigs) {

                TSystemStatus systemStatus = SystemStatusUtils.getSystemStatus(config.getServer());
                LOGGER.info("服务器IP:{} ,状态：{}", config.getServer(), systemStatus);

                if (StringUtils.isEmpty(systemStatus.getRemark())) {
                    // TODO 请求成功，将cpu等占用与设置阈值比较，判断是否需要发送短信给管理员
                    double cpuCombined = systemStatus.getCpuCombined();
                    BigDecimal cpuPer = ((new BigDecimal(cpuCombined)).multiply(new BigDecimal(100)));

                    BigDecimal memoryUesd = new BigDecimal(systemStatus.getMemoryUesd());
                    BigDecimal memoryTotal = new BigDecimal(systemStatus.getMemoryTotal());
                    BigDecimal memper = (memoryUesd.divide(memoryTotal,10,ROUND_HALF_DOWN)).multiply(new BigDecimal(100));

                    BigDecimal fileSystemTotal = new BigDecimal(systemStatus.getFileSystemTotal());
                    BigDecimal fileSystemUsed = new BigDecimal(systemStatus.getFileSystemTotal()-systemStatus.getFileSystemFree());
                    BigDecimal fileper = (fileSystemUsed.divide(fileSystemTotal,10,ROUND_HALF_DOWN)).multiply(new BigDecimal(100));

                    if(cpuPer.compareTo(new BigDecimal(config.getCpu()))>0||memper.compareTo(new BigDecimal(config.getMemory()))>0
                            ||fileper.compareTo(new BigDecimal(config.getDisc()))>0){
                        for(ShoppingEarlyWarningAdmin shoppingEarlyWarningAdmin:admins){
                            sendMsSingleSMS(shoppingEarlyWarningAdmin.getPhone(),config.getServer());
                        }
                    }

                } else {
                    // TODO 获取服务器信息失败，短信提示管理员


                }
                systemStatus.setIp(config.getServer());
                systemStatusDao.insert(systemStatus);
            }
            LOGGER.info("服务器监控-定时任务结束");
        }
    }

    public static JSONObject sendMsSingleSMS(String mobile, String content){
        JSONObject retJson = new JSONObject();
        try {
//            if(StringUtils.isBlank(token)){
//                TThirdAppinfo tThirdAppinfo = new TThirdAppinfo();
//                tThirdAppinfo.setUsername(CommonInit.emay_username_static);
//                tThirdAppinfo.setPassword(CommonInit.emay_password_static);
//                tThirdAppinfo.setIsValid("T");
//                tThirdAppinfo = CommonInit.tThirdAppinfoDao_static.queryDetail(tThirdAppinfo);
//                token = tThirdAppinfo!=null ? tThirdAppinfo.getToken() : token;
//            }
//            token = "Bearer " + token;

            Map<String, String> params=new HashMap<>();
            params.put("method", "sendUtf8Msg");
            params.put("username", "JSM4140009");
            params.put("password", "xr185whg");
            params.put("veryCode", "maoyvrgxx9h8");
            params.put("tempid", "JSM41400-0094");
            params.put("content", "@1@="+content);
            params.put("msgtype", "2");
            params.put("mobile", mobile);
            params.put("rt", "json");
//            params.put("code", "utf-8");
            retJson = HttpSendUtil.sendMsg(url, params);
            System.out.println("单条短信推送请求报文：--------------" + params);
//            System.out.println("单条短信推送请求token：--------------" + token);
            System.out.println("单条短信推送返回报文：--------------" + retJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retJson;
    }

    public static void main(String[] args) throws ParseException {
        BigDecimal memoryUesd = new BigDecimal(10749);
        BigDecimal memoryTotal =new BigDecimal(15886);

        BigDecimal memper = memoryUesd.divide(memoryTotal,10,ROUND_HALF_DOWN);

        System.out.println(memper);
    }

}