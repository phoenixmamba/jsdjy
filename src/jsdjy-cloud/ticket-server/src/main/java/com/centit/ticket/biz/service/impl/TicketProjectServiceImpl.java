package com.centit.ticket.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.biz.service.TicketProjectService;
import com.centit.ticket.common.enums.Const;
import com.centit.ticket.dao.*;
import com.centit.ticket.po.*;
import com.centit.ticket.utils.*;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaDamaiMzOrderCreateRequest;
import com.taobao.api.request.AlibabaDamaiMzOrderRenderRequest;
import com.taobao.api.response.AlibabaDamaiMzOrderCreateResponse;
import com.taobao.api.response.AlibabaDamaiMzOrderRenderResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;

/**
 * <p>麦座演出票<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-04-08
 **/
@Transactional
@Service
public class TicketProjectServiceImpl implements TicketProjectService {
    public static final Log log = LogFactory.getLog(TicketProjectService.class);

    @Resource
    private TicketClassDao ticketClassDao;
    @Resource
    private TicketProjectDao ticketProjectDao;
    @Resource
    private TicketEventDao ticketEventDao;
    @Resource
    private TicketEventPriceDao ticketEventPriceDao;
    @Resource
    private TicketProjectWatchingNoticeDao ticketProjectWatchingNoticeDao;
    @Resource
    private TicketVenueDao ticketVenueDao;
    @Resource
    private TicketExchangePlaceDao ticketExchangePlaceDao;

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;
    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;
    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;
    @Resource
    private ShoppingPaymentDao shoppingPaymentDao;
    @Resource
    private TicketFavoriteDao ticketFavoriteDao;
    @Resource
    private TicketHistoryDao ticketHistoryDao;
    @Resource
    private TicketRecommendDao ticketRecommendDao;
    @Resource
    private TicketProjectImgDao ticketProjectImgDao;

    @Resource
    private TConcurrencySwitchDao tConcurrencySwitchDao;

    @Autowired
    JedisPoolUtils jedisPoolUtils;

    @Value("${mz.sign.secret}")
    private String mzSignSecret;
    @Value("${mz.client.appkey}")
    private String mzClientAppkey;

    private static String fileDownloadUrl = "https://app.jsartcentre.org/JSDJYPlatform/fileserver/common/downloadFile/";

//    @Value("${notShowClassIds}")
//    private String notShowClassIds;

    public boolean openConcurrencySwitch(){
        try{
            TConcurrencySwitch tConcurrencySwitch= tConcurrencySwitchDao.queryDetail(null);
            if(tConcurrencySwitch !=null &&tConcurrencySwitch.getHomepageApiSwitch().equals("on")){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    /**
     * 查询首页数据
     */
    @Override
    public JSONObject queryHomeData(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //高并发模式开关打开
            if(openConcurrencySwitch()){
                String homeDataStr = null;
                try{
                    homeDataStr=jedisPoolUtils.getValue("homeData");
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(homeDataStr!=null){
                    bizDataJson = JSONObject.parseObject(homeDataStr);
                }
            }else{
                HashMap<String, Object> reqMap = new HashMap<>();
                //热门推荐
                JSONArray recList = new JSONArray();
                List<TicketClass> classList = ticketClassDao.queryList(reqMap);
                JSONArray objList = new JSONArray();
                for (TicketClass ticketClass : classList) {
                    JSONObject classObj = new JSONObject();
                    String classId = ticketClass.getClassId();
                    classObj.put("classId", classId);
                    classObj.put("className", ticketClass.getClassName());
                    reqMap.clear();
                    reqMap.put("classId", classId);
                    List<TicketEvent> eventList = ticketEventDao.queryClassEvents(reqMap);
                    JSONArray projectList = new JSONArray();
                    for (TicketEvent ticketEvent : eventList) {
                        JSONObject projectObj = new JSONObject();
                        String projectId = ticketEvent.getProjectId();  //演出项目id
                        //查询演出详情
                        TicketProject ticketProject = new TicketProject();
                        ticketProject.setProjectId(projectId);
                        ticketProject = ticketProjectDao.queryDetail(ticketProject);
                        projectObj.put("projectId", projectId);   //项目id
                        projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                        //由于ios问题，优先从本地读取图片文件
                        String projectImgUrl =ticketProject.getProjectImgUrl();
                        String imdId = ticketProjectImgDao.queryImgId(projectImgUrl);
                        if(StringUtil.isNotNull(imdId)){
                            projectObj.put("projectImgUrl",fileDownloadUrl+ imdId);
                        }else{
                            projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());
                        }
                        projectObj.put("projectName", ticketProject.getProjectName());       //名称
                        projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                        projectObj.put("venueName", ticketEvent.getVenueName());   //场馆名称
                        //查询该项目满足查询条件的所有的场次信息，按时间排序
                        reqMap.put("projectId", projectId);
                        List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                        //处理场次时间
                        if (events.size() == 1) {
                            String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                            String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                            projectObj.put("timeStr", str1+" "+str2);

                        } else if (events.size() > 1) {
                            String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                            String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                            String dayStr = str1 + "-" + str2;
                            String timeStr="";
                            List<String> times = new ArrayList<>();
                            for(TicketEvent event:events){
                                String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                                if(!times.contains(str)){
                                    timeStr=timeStr+str+"/";
                                    times.add(str);
                                }
                            }
                            if(!timeStr.equals("")){
                                timeStr=timeStr.substring(0,timeStr.length()-1);
                            }
                            projectObj.put("timeStr", dayStr + " " + timeStr);
                        }
                        //处理价格
                        List<Integer> prices = new ArrayList<>();
                        for (TicketEvent event : events) {
                            //场次下票档信息
                            HashMap<String, Object> pMap = new HashMap<>();
                            pMap.put("eventId", event.getEventId());
                            pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                            List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                            for (TicketEventPrice ticketEventPrice : priceList) {
                                int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                                prices.add(priceMoneyFen);
                            }
                        }
                        //获取最低价格
                        if(prices.size()>0){
                            int minPrice = Collections.min(prices);
                            projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格

                        }
                        projectList.add(projectObj);
                        projectObj.put("className", ticketProject.getFirstClassName());
                    }
                    classObj.put("projectList", projectList);
                    objList.add(classObj);
                }
                bizDataJson.put("dataList", objList);
                //推荐数据
                List<TicketRecommend> recommends = ticketRecommendDao.queryList(new HashMap<>());
                for (TicketRecommend ticketRecommend : recommends) {
                    JSONObject projectObj = new JSONObject();
                    String projectId = ticketRecommend.getProjectId();  //演出项目id

                    //查询演出详情
                    TicketProject ticketProject = new TicketProject();
                    ticketProject.setProjectId(projectId);
                    ticketProject = ticketProjectDao.queryDetail(ticketProject);
                    if(ticketProject.getProjectSaleState()==2){   //销售中
                        projectObj.put("projectId", projectId);   //项目id
                        projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                        //由于ios问题，优先从本地读取图片文件
                        String projectImgUrl =ticketProject.getProjectImgUrl();
                        String imdId = ticketProjectImgDao.queryImgId(projectImgUrl);
                        if(StringUtil.isNotNull(imdId)){
                            projectObj.put("projectImgUrl",fileDownloadUrl+ imdId);
                        }else{
                            projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());
                        }
                        projectObj.put("projectName", ticketProject.getProjectName());       //名称
                        projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座

                        //查询该项目满足查询条件的所有的场次信息，按时间排序
                        reqMap.put("projectId", projectId);
                        List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                        if(events.size()>0){
                            projectObj.put("venueName", events.get(0).getVenueName());   //场馆名称
                        }

                        //处理场次时间
                        if (events.size() == 1) {
                            String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                            String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                            projectObj.put("timeStr", str1+" "+str2);

                        } else if (events.size() > 1) {
                            String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                            String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                            String dayStr = str1 + "-" + str2;
                            String timeStr="";
                            List<String> times = new ArrayList<>();
                            for(TicketEvent event:events){
                                String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                                if(!times.contains(str)){
                                    timeStr=timeStr+str+"/";
                                    times.add(str);
                                }
                            }
                            if(!timeStr.equals("")){
                                timeStr=timeStr.substring(0,timeStr.length()-1);
                            }
                            projectObj.put("timeStr", dayStr + " " + timeStr);
                        }
                        //处理价格
                        List<Integer> prices = new ArrayList<>();
                        for (TicketEvent event : events) {
                            //场次下票档信息
                            HashMap<String, Object> pMap = new HashMap<>();
                            pMap.put("eventId", event.getEventId());
                            pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                            List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                            for (TicketEventPrice ticketEventPrice : priceList) {
                                int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                                prices.add(priceMoneyFen);
                            }
                        }
                        //获取最低价格
                        if(prices.size()>0){
                            int minPrice = Collections.min(prices);
                            projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格
                        }
                        recList.add(projectObj);
                    }
                }
                //根据用户标签推荐演出
                if(null !=reqJson.get("userId")&&!"".equals(reqJson.get("userId"))){
                    String userId = reqJson.getString("userId");
                    ShoppingUser user =CommonUtil.getShoppingUserByUserId(userId);
                    if(null !=user&&null !=user.getMobile()){
                        JSONArray dataArray = CRMService.getUserLabels(user.getMobile());
                        Set<String> classIds = new HashSet<>();
                        for(int i=0;i<dataArray.size();i++){
                            JSONObject dataObj = dataArray.getJSONObject(i);
                            classIds.add(dataObj.getString("classId"));
                        }
                        if(!classIds.isEmpty()){
                            reqMap.clear();
                            reqMap.put("classIds",classIds);
                            List<TicketProject> projects = ticketProjectDao.queryUserProjectList(reqMap);
                            for(TicketProject ticketProject:projects){
                                JSONObject projectObj = new JSONObject();
                                String projectId = ticketProject.getProjectId();  //演出项目id
                                projectObj.put("projectId", projectId);   //项目id
                                projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                                //由于ios问题，优先从本地读取图片文件
                                String projectImgUrl =ticketProject.getProjectImgUrl();
                                String imdId = ticketProjectImgDao.queryImgId(projectImgUrl);
                                if(StringUtil.isNotNull(imdId)){
                                    projectObj.put("projectImgUrl",fileDownloadUrl+ imdId);
                                }else{
                                    projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());
                                }
                                projectObj.put("projectName", ticketProject.getProjectName());       //名称
                                projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座

                                //查询该项目满足查询条件的所有的场次信息，按时间排序
                                reqMap.put("projectId", projectId);
                                List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                                if(events.size()>0){
                                    projectObj.put("venueName", events.get(0).getVenueName());   //场馆名称
                                }

                                //处理场次时间
                                if (events.size() == 1) {
                                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                                    String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                                    projectObj.put("timeStr", str1+" "+str2);

                                } else if (events.size() > 1) {
                                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                                    String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                                    String dayStr = str1 + "-" + str2;
                                    String timeStr="";
                                    List<String> times = new ArrayList<>();
                                    for(TicketEvent event:events){
                                        String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                                        if(!times.contains(str)){
                                            timeStr=timeStr+str+"/";
                                            times.add(str);
                                        }
                                    }
                                    if(!timeStr.equals("")){
                                        timeStr=timeStr.substring(0,timeStr.length()-1);
                                    }
                                    projectObj.put("timeStr", dayStr + " " + timeStr);
                                }
                                //处理价格
                                List<Integer> prices = new ArrayList<>();
                                for (TicketEvent event : events) {
                                    //场次下票档信息
                                    HashMap<String, Object> pMap = new HashMap<>();
                                    pMap.put("eventId", event.getEventId());
                                    pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                                    List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                                    for (TicketEventPrice ticketEventPrice : priceList) {
                                        int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                                        prices.add(priceMoneyFen);
                                    }
                                }
                                //获取最低价格
                                if(prices.size()>0){
                                    int minPrice = Collections.min(prices);
                                    projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格
                                }
                                recList.add(projectObj);
                            }
                        }
                    }
                }
                bizDataJson.put("recList", recList);
                jedisPoolUtils.setKey("homeData",bizDataJson.toJSONString());
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
     * 查询演出分类
     */
    @Override
    public JSONObject queryTicketClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("isShow","0");
            List<TicketClass> classList = ticketClassDao.queryList(reqMap);
            bizDataJson.put("objList", classList);
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
     * 查询演出列表
     */
    @Override
    public JSONObject queryProjectList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
//            JSONArray objList = new JSONArray();
//            List<TicketEvent> eventList = ticketEventDao.queryClassEvents(reqMap);
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            JSONArray objList = new JSONArray();
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            int queryType = reqJson.getInteger("queryType");
            if(queryType ==2){  //艺术电影
//                String classId = CommonUtil.getClassId("艺术电影");
//                if(null !=classId){
//                    reqMap.put("classId", classId);
//                }else{
//                    reqMap.put("classId",Const.TICKET_CLASSID_ARTMOVIE);
//                }
                reqMap.put("classId",Const.TICKET_CLASSID_ARTMOVIE);
            }else if(queryType ==3){   //爱艺活动
//                String classId = CommonUtil.getClassId("爱艺活动");
//                if(null !=classId){
//                    reqMap.put("classId", classId);
//                }else{
//                    reqMap.put("classId",Const.TICKET_CLASSID_AIYI);
//                }
                reqMap.put("classId",Const.TICKET_CLASSID_AIYI);
            }else{
                List<String> classIds = new ArrayList<>();
//                String mvId = CommonUtil.getClassId("艺术电影");
//                classIds.add(mvId==null?Const.TICKET_CLASSID_ARTMOVIE:mvId);
//                String aiyiId = CommonUtil.getClassId("爱艺活动");
//                classIds.add(aiyiId==null?Const.TICKET_CLASSID_AIYI:aiyiId);
                classIds.add(Const.TICKET_CLASSID_ARTMOVIE);
                classIds.add(Const.TICKET_CLASSID_AIYI);
                reqMap.put("classIds", classIds);
            }
            boolean isRedis = openConcurrencySwitch()?true:false;
            bizDataJson.put("total", ticketEventDao.queryTotalCount(reqMap));
            if(isRedis){
                reqMap.put("switchON",true);
            }
            List<TicketEvent> eventList = ticketEventDao.queryPageList(reqMap);
            for (TicketEvent ticketEvent : eventList) {
                JSONObject projectObj = new JSONObject();
                String projectId = ticketEvent.getProjectId();  //演出项目id
                String projectStr = null;
                if(isRedis){
                    try{
                        projectStr=jedisPoolUtils.getValue(projectId);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(projectStr!=null){
                        projectObj = JSONObject.parseObject(projectStr);
                    }
                }
                if(!isRedis||projectStr==null){
                    //查询演出详情
                    TicketProject ticketProject = new TicketProject();
                    ticketProject.setProjectId(projectId);
                    ticketProject = ticketProjectDao.queryDetail(ticketProject);
                    projectObj.put("projectId", projectId);   //项目id
                    projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                    //由于ios问题，优先从本地读取图片文件
                    String projectImgUrl =ticketProject.getProjectImgUrl();
                    String imdId = ticketProjectImgDao.queryImgId(projectImgUrl);
                    if(StringUtil.isNotNull(imdId)){
                        projectObj.put("projectImgUrl",fileDownloadUrl+ imdId);
                    }else{
                        projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());
                    }

                    projectObj.put("projectName", ticketProject.getProjectName());       //名称
                    projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                    projectObj.put("venueName", ticketEvent.getVenueName());   //场馆名称
                    //查询该项目满足查询条件的所有的场次信息，按时间排序
                    reqMap.put("projectId", projectId);
                    List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                    //处理场次时间
                    if (events.size() == 1) {
                        String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                        String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                        projectObj.put("timeStr", str1+" "+str2);

                    } else if (events.size() > 1) {
                        String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                        String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                        String dayStr = str1 + "-" + str2;
                        String timeStr="";
                        List<String> times = new ArrayList<>();
                        for(TicketEvent event:events){
                            String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                            if(!times.contains(str)){
                                timeStr=timeStr+str+"/";
                                times.add(str);
                            }
                        }
                        if(!timeStr.equals("")){
                            timeStr=timeStr.substring(0,timeStr.length()-1);
                        }
                        projectObj.put("timeStr", dayStr + " " + timeStr);
                    }
                    //处理价格
                    List<Integer> prices = new ArrayList<>();
                    for (TicketEvent event : events) {
                        //场次下票档信息
                        HashMap<String, Object> pMap = new HashMap<>();
                        pMap.put("eventId", event.getEventId());
                        pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                        List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                        for (TicketEventPrice ticketEventPrice : priceList) {
                            int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                            prices.add(priceMoneyFen);
                        }
                    }
                    //获取最低价格
                    if(prices.size()>0){
                        int minPrice = Collections.min(prices);
                        projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格

                    }
                    projectObj.put("className", ticketProject.getFirstClassName());

                    jedisPoolUtils.setKey(projectId,projectObj.toJSONString());
                }
                objList.add(projectObj);
            }
            bizDataJson.put("objList", objList);
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
     * 查询指定月份有演出场次的日期
     */
    @Override
    public JSONObject queryEventDays(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String month = reqJson.getString("month");
            String startTime = month + "-01 00:00:00";
            String endime = month + "-31 23:59:59";
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("startTime", startTime);
            reqMap.put("endime", endime);
            List<TicketEvent> eventList = ticketEventDao.queryProjectEvents(reqMap);
            List<String> dayList = new ArrayList<>();
            for (TicketEvent ticketEvent : eventList) {
                String eventStartTime = ticketEvent.getEventStartTime();
                dayList.add(StringUtil.formatDate(eventStartTime, "yyyy-MM-dd"));
            }
            bizDataJson.put("objList", dayList);
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
     * 查询列表
     */
    @Override
    public JSONObject queryList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            bizDataJson.put("total", ticketEventDao.queryTotalCount(reqMap));
            List<TicketEvent> eventList = ticketEventDao.queryPageList(reqMap);
            JSONArray objList = new JSONArray();

            for (TicketEvent ticketEvent : eventList) {
                JSONObject projectObj = new JSONObject();
                String projectId = ticketEvent.getProjectId();  //演出项目id
                //查询演出详情
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);
                projectObj.put("projectId", projectId);   //项目id
                projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                projectObj.put("projectName", ticketProject.getProjectName());       //名称
                projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                projectObj.put("venueName", ticketEvent.getVenueName());   //场馆名称
                //查询该项目满足查询条件的所有的场次信息，按时间排序
                reqMap.put("projectId", projectId);
                List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                //处理场次时间
                if (events.size() == 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                    projectObj.put("timeStr", str1+" "+str2);

                } else if (events.size() > 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                    String dayStr = str1 + "-" + str2;
                    String timeStr="";
                    List<String> times = new ArrayList<>();
                    for(TicketEvent event:events){
                        String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                        if(!times.contains(str)){
                            timeStr=timeStr+str+"/";
                            times.add(str);
                        }
                    }
                    if(!timeStr.equals("")){
                        timeStr=timeStr.substring(0,timeStr.length()-1);
                    }
                    projectObj.put("timeStr", dayStr + " " + timeStr);
                }
                //处理价格
                List<Integer> prices = new ArrayList<>();
                for (TicketEvent event : events) {
                    //场次下票档信息
                    HashMap<String, Object> pMap = new HashMap<>();
                    pMap.put("eventId", event.getEventId());
                    pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                    List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                    for (TicketEventPrice ticketEventPrice : priceList) {
                        int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                        prices.add(priceMoneyFen);
                    }
                }
                //获取最低价格
                int minPrice = Collections.min(prices);
                projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格
                objList.add(projectObj);
            }

            bizDataJson.put("objList", objList);
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
     * 项目详情
     */
    @Override
    public JSONObject projectDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String projectId = reqJson.getString("projectId");  //演出项目id
            boolean isRedis = openConcurrencySwitch()?true:false;
//            String projectStr = null;
//            if(isRedis){
//                try{
//                    projectStr=jedisPoolUtils.getValue("projectDetail_"+projectId);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                if(projectStr!=null){
//                    bizDataJson = JSONObject.parseObject(projectStr);
//                    //默认未收藏
//                    bizDataJson.put("isFav", false);   //未收藏
//                }
//            }
            //查询演出详情
            TicketProject ticketProject = new TicketProject();
            ticketProject.setProjectId(projectId);
            ticketProject = ticketProjectDao.queryDetail(ticketProject);

            if(ticketProject.getProjectSaleState()!=2){
                bizDataJson.put("isOff", true);   //已下架
            }else{
                bizDataJson.put("isOff", false);   //未下架
            }

            bizDataJson.put("projectId", projectId);
            bizDataJson.put("projectName", ticketProject.getProjectName());
            bizDataJson.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
            bizDataJson.put("projectIntroduce", ticketProject.getProjectIntroduce());   //详情介绍
            bizDataJson.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座

            //查询观演须知
            TicketProjectWatchingNotice ticketProjectWatchingNotice = new TicketProjectWatchingNotice();
            ticketProjectWatchingNotice.setProjectId(projectId);
            ticketProjectWatchingNotice = ticketProjectWatchingNoticeDao.queryDetail(ticketProjectWatchingNotice);
            bizDataJson.put("showLengthTips", ticketProjectWatchingNotice.getShowLengthTips());   //演出时长
            bizDataJson.put("watchingNotice", ticketProjectWatchingNotice);   //观演须知

            List<Integer> prices = new ArrayList<>();
            Set<String> venues = new HashSet<>();
            List<TicketEvent> events =new ArrayList<>();
            //查询该项目在售的所有的场次信息
            JSONObject resObj = MZService.getProjectEvent(projectId);
            if(null !=resObj&&null !=resObj.get("event_v_o")){
                try{
                    JSONArray eventArray = resObj.getJSONArray("event_v_o");
                    for(int i=0;i<eventArray.size();i++){
                        JSONObject eventObj = eventArray.getJSONObject(i);
                        if(projectId.equals(eventObj.getString("project_id"))){
                            String event_id = eventObj.getString("event_id");   //麦座场次id
                            String event_name = eventObj.getString("event_name");   //麦座场次名称
                            int event_sale_state = Integer.valueOf(eventObj.get("event_sale_state").toString());   //场次销售状态；2=销售中；仅返回销售中的数据
                            String event_start_time = eventObj.getString("event_start_time");   //场次演出开始时间，精确到分，格式：yyyy-MM-dd HH:mm
                            String event_end_time = eventObj.getString("event_end_time");   //场次演出结束时间，精确到分，格式：yyyy-MM-dd HH:mm
                            boolean real_name_buy_limit_boolean = eventObj.getBoolean("real_name_buy_limit_boolean");  //是否需要实名制购买；true=是，false=否

                            int single_card_limit_num = eventObj.get("single_card_limit_num")==null?0:Integer.valueOf(eventObj.get("single_card_limit_num").toString());   //单个证件累计限购数量；当前场次单个证件（一单一证或一票一证持票人）；0=不限购
                            String venue_id = eventObj.getString("venue_id");   //场次所在场馆id；一个项目下多场次允许有多个场馆
                            String venue_name = eventObj.getString("venue_name");   //场次所在场馆名称
                            String venue_basemap_svg_url = eventObj.get("venue_basemap_svg_url")==null?"":eventObj.getString("venue_basemap_svg_url");  //场馆座位底图url；项目为选座项目时非空，渲染选座时使用

                            TicketEvent ticketEvent = new TicketEvent();
                            ticketEvent.setEventId(event_id);
                            ticketEvent.setEventName(event_name);
                            ticketEvent.setProjectId(projectId);
                            ticketEvent.setEventSaleState(event_sale_state);
                            ticketEvent.setEventStartTime(event_start_time);
                            ticketEvent.setEventEndTime(event_end_time);
                            ticketEvent.setRealNameBuyLimitBoolean(real_name_buy_limit_boolean?"1":"0");
                            if(real_name_buy_limit_boolean){
                                int real_name_buy_limit_type = Integer.valueOf(eventObj.get("real_name_buy_limit_type").toString());   //实名制购买类型；1=一单一证，2=一票一证
                                ticketEvent.setRealNameBuyLimitType(real_name_buy_limit_type);
                            }

                            ticketEvent.setSingleCardLimitNum(single_card_limit_num);
                            ticketEvent.setVenueId(venue_id);
                            ticketEvent.setVenueName(venue_name);
                            ticketEvent.setVenueBasemapSvgUrl(venue_basemap_svg_url);
                            //取票方式
                            String deliveryTypeList ="";
                            if(null !=eventObj.get("delivery_type_list")){
                                try{
                                    JSONObject delivery_type_list = eventObj.getJSONObject("delivery_type_list");
                                    if(null !=delivery_type_list.get("number")){

                                        List<Integer> list = (List<Integer>) delivery_type_list.get("number");
                                        for(int num:list){
                                            deliveryTypeList += num+",";
                                        }
                                    }
                                }catch (Exception e){
                                    log.error("获取取票方式list失败:"+eventObj);
                                    e.printStackTrace();
                                }
                            }
                            ticketEvent.setDeliveryTypeList(deliveryTypeList);
                            //场次绑定的优惠id列表
                            String promotionIdList ="";
                            if(null !=eventObj.get("promotion_id_list")){
                                try{
                                    JSONObject promotion_id_list = eventObj.getJSONObject("promotion_id_list");
                                    if(null !=promotion_id_list.get("string")){
                                        List<String> list = (List<String>) promotion_id_list.get("string");
                                        for(String str:list){
                                            promotionIdList += str+",";
                                        }
                                    }
                                }catch (Exception e){
                                    log.error("获取场次绑定的优惠id列表失败:"+eventObj);
                                    e.printStackTrace();
                                }
                            }
                            ticketEvent.setPromotionIdList(promotionIdList);

                            //场馆
                            venues.add(ticketEvent.getVenueId());

                            //场次下票档信息
                            List<TicketEventPrice> priceList =new ArrayList<>();
                            if(null !=eventObj.get("price_list")){
                                try{
                                    JSONObject price_list = eventObj.getJSONObject("price_list");
                                    if(null !=price_list.get("price_v_o")){
                                        JSONArray priceArray = price_list.getJSONArray("price_v_o");
                                        for(int m=0;m<priceArray.size();m++){
                                            JSONObject priceObj = priceArray.getJSONObject(m);
                                            String price_id = priceObj.getString("price_id");  //票档id
                                            String price_name = priceObj.getString("price_name");  //票档名称
                                            int price_sale_state = priceObj.getInteger("price_sale_state");  //票档销售状态；1=可售，2=禁售
                                            int price_type = priceObj.getInteger("price_type");  //票档类型；1=单票，2=套票；暂仅支持单票返回
                                            int price_money_fen = priceObj.getInteger("price_money_fen");  //票档价格；单位：分
                                            String price_color = priceObj.getString("price_color");   //票档颜色；项目座位购买类型为 有座（自助选座）时非空；选座渲染时可使用
                                            int margin_stock_num = priceObj.get("margin_stock_num")==null?0:priceObj.getInteger("margin_stock_num");  //剩余库存数量；仅项目座位购买类型为 无座时返回

                                            TicketEventPrice ticketEventPrice = new TicketEventPrice();
                                            ticketEventPrice.setEventId(event_id);
                                            ticketEventPrice.setPriceId(price_id);
                                            ticketEventPrice.setPriceName(price_name);
                                            ticketEventPrice.setPriceSaleState(price_sale_state);
                                            ticketEventPrice.setPriceType(price_type);
                                            ticketEventPrice.setPriceMoneyFen(price_money_fen);
                                            ticketEventPrice.setPriceMoneyYuan(new BigDecimal((float) price_money_fen / 100).setScale(2, BigDecimal.ROUND_HALF_UP));
                                            ticketEventPrice.setPriceColor(price_color);
                                            ticketEventPrice.setMarginStockNum(margin_stock_num);
                                            prices.add(price_money_fen);
                                            priceList.add(ticketEventPrice);
                                        }
                                    }
                                }catch (Exception e){
                                    log.error("获取场次下票档信息失败:"+eventObj);
                                    e.printStackTrace();
                                }
                            }
                            ticketEvent.setPriceList(priceList);
                            events.add(ticketEvent);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //处理场次时间
            if (events.size() == 1) {
                String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                bizDataJson.put("timeStr", str1+" "+str2);

            } else if (events.size() > 1) {
                String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                String dayStr = str1 + "-" + str2;
                String timeStr="";
                List<String> times = new ArrayList<>();
                for(TicketEvent event:events){
                    String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                    if(!times.contains(str)){
                        timeStr=timeStr+str+"/";
                        times.add(str);
                    }
                }
                if(!timeStr.equals("")){
                    timeStr=timeStr.substring(0,timeStr.length()-1);
                }
                bizDataJson.put("timeStr", dayStr + " " + timeStr);
            }

            //获取最低价格
            if(prices.size()>0){
                int minPrice = Collections.min(prices);
                bizDataJson.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }else{
                bizDataJson.put("minPrice",0);
            }

            bizDataJson.put("events", events);
            //场馆信息
            List<TicketVenue> venueList = new ArrayList<>();
            for (String venueId : venues) {
                TicketVenue ticketVenue = new TicketVenue();
                ticketVenue.setVenueId(venueId);
                ticketVenue = ticketVenueDao.queryDetail(ticketVenue);
                venueList.add(ticketVenue);
            }

            bizDataJson.put("venueList", venueList);
            //获取演出关联商品信息

            if(isRedis){
                bizDataJson.put("isFav", false);   //未收藏
            }else{
                //收藏标识
                if(null !=reqJson.get("userId")){
                    String userId = reqJson.getString("userId");
                    //记录浏览历史
                    TicketHistory ticketHistory = new TicketHistory();
                    ticketHistory.setUserId(userId);
                    ticketHistory.setProjectId(projectId);
                    ticketHistoryDao.insert(ticketHistory);

                    HashMap reqMap = new HashMap();
                    reqMap.put("projectId", projectId);
                    reqMap.put("userId", userId);
                    reqMap.put("deleteStatus", '0');
                    if(!ticketFavoriteDao.queryList(reqMap).isEmpty()){
                        bizDataJson.put("isFav", true);   //已收藏
                    }else{
                        bizDataJson.put("isFav", false);   //未收藏
                    }
                }
            }

//            try{
//                jedisPoolUtils.setKey("projectDetail_"+projectId,bizDataJson.toJSONString());
//            }catch (Exception e){
//                e.printStackTrace();
//            }

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
     * 收藏
     */
    @Override
    public JSONObject addFavorite(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            TicketFavorite ticketFavorite = JSON.parseObject(reqJson.toJSONString(), TicketFavorite.class);
            ticketFavoriteDao.insert(ticketFavorite);

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
     * 取消收藏
     */
    @Override
    public JSONObject cancelFavorite(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            TicketFavorite ticketFavorite = JSON.parseObject(reqJson.toJSONString(), TicketFavorite.class);
            ticketFavoriteDao.cancelFav(ticketFavorite);

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
     * 我的收藏列表
     */
    @Override
    public JSONObject myFavList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            String userId = reqJson.getString("userId");

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("userId", userId);
            reqMap.put("deleteStatus", "0");
            List<TicketFavorite> objList= ticketFavoriteDao.queryList(reqMap);
            bizDataJson.put("total", ticketFavoriteDao.queryTotalCount(reqMap));

            JSONArray resArray = new JSONArray();
            for(TicketFavorite ticketFavorite:objList){
                String projectId = ticketFavorite.getProjectId();
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);
                JSONObject projectObj = new JSONObject();
                projectObj.put("projectId", projectId);   //项目id
                projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                projectObj.put("projectName", ticketProject.getProjectName());       //名称
                projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                //查询该项目满足查询条件的所有的场次信息，按时间排序
                reqMap.put("projectId", projectId);
                List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                //处理场次时间
                if (events.size() == 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                    projectObj.put("timeStr", str1+" "+str2);

                } else if (events.size() > 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                    String dayStr = str1 + "-" + str2;
                    String timeStr="";
                    List<String> times = new ArrayList<>();
                    for(TicketEvent event:events){
                        String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                        if(!times.contains(str)){
                            timeStr=timeStr+str+"/";
                            times.add(str);
                        }
                    }
                    if(!timeStr.equals("")){
                        timeStr=timeStr.substring(0,timeStr.length()-1);
                    }
                    projectObj.put("timeStr", dayStr + " " + timeStr);
                }
                //处理价格
                List<Integer> prices = new ArrayList<>();
                for (TicketEvent event : events) {
                    //场次下票档信息
                    HashMap<String, Object> pMap = new HashMap<>();
                    pMap.put("eventId", event.getEventId());
                    pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                    List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                    for (TicketEventPrice ticketEventPrice : priceList) {
                        int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                        prices.add(priceMoneyFen);
                    }
                }
                //获取最低价格
                if(prices.size()>0){
                    int minPrice = Collections.min(prices);
                    projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格

                }

                if(ticketProject.getProjectSaleState()!=2){
                    projectObj.put("invalid", true);  //失效
                }else{
                    projectObj.put("invalid", false);
                }
                resArray.add(projectObj);
            }

            bizDataJson.put("objList", resArray);

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
     * 我的足迹
     */
    @Override
    public JSONObject myHistoryList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            String userId = reqJson.getString("userId");

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("userId", userId);
            List<HashMap<String, Object>> objList= ticketHistoryDao.queryMyHistoryList(reqMap);
            bizDataJson.put("total", ticketHistoryDao.queryMyHistoryCount(reqMap));

            JSONArray resArray = new JSONArray();
            for(HashMap<String, Object> objMap:objList){
                String projectId = objMap.get("project_id").toString();
                String dateStr = objMap.get("dateStr").toString();
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);
                JSONObject projectObj = new JSONObject();
                projectObj.put("dateStr", dateStr);   //浏览日期
                projectObj.put("projectId", projectId);   //项目id
                projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                projectObj.put("projectName", ticketProject.getProjectName());       //名称
                projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                //查询该项目满足查询条件的所有的场次信息，按时间排序
                reqMap.put("projectId", projectId);
                List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                //处理场次时间
                if (events.size() == 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                    projectObj.put("timeStr", str1+" "+str2);

                } else if (events.size() > 1) {
                    String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                    String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                    String dayStr = str1 + "-" + str2;
                    String timeStr="";
                    List<String> times = new ArrayList<>();
                    for(TicketEvent event:events){
                        String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                        if(!times.contains(str)){
                            timeStr=timeStr+str+"/";
                            times.add(str);
                        }
                    }
                    if(!timeStr.equals("")){
                        timeStr=timeStr.substring(0,timeStr.length()-1);
                    }
                    projectObj.put("timeStr", dayStr + " " + timeStr);
                }
                //处理价格
                List<Integer> prices = new ArrayList<>();
                for (TicketEvent event : events) {
                    //场次下票档信息
                    HashMap<String, Object> pMap = new HashMap<>();
                    pMap.put("eventId", event.getEventId());
                    pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                    List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                    for (TicketEventPrice ticketEventPrice : priceList) {
                        int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                        prices.add(priceMoneyFen);
                    }
                }
                //获取最低价格
                if(prices.size()>0){
                    int minPrice = Collections.min(prices);
                    projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格

                }

                if(ticketProject.getProjectSaleState()!=2){
                    projectObj.put("invalid", true);  //失效
                }else{
                    projectObj.put("invalid", false);
                }
                resArray.add(projectObj);
            }

            bizDataJson.put("objList", resArray);

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
     * 清除历史轨迹
     */
    @Override
    public JSONObject clearHistory(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            TicketHistory ticketHistory = JSON.parseObject(reqJson.toJSONString(), TicketHistory.class);
            ticketHistoryDao.delete(ticketHistory);

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

//    /**
//     * 项目详情
//     */
//    @Override
//    public JSONObject projectDetail(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            String projectId = reqJson.getString("projectId");  //演出项目id
//            //查询演出详情
//            TicketProject ticketProject = new TicketProject();
//            ticketProject.setProjectId(projectId);
//            ticketProject = ticketProjectDao.queryDetail(ticketProject);
//            bizDataJson.put("projectId", projectId);
//            bizDataJson.put("projectName", ticketProject.getProjectName());
//            bizDataJson.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
//            bizDataJson.put("projectIntroduce", ticketProject.getProjectIntroduce());   //详情介绍
//            bizDataJson.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
//
//            //查询观演须知
//            TicketProjectWatchingNotice ticketProjectWatchingNotice = new TicketProjectWatchingNotice();
//            ticketProjectWatchingNotice.setProjectId(projectId);
//            ticketProjectWatchingNotice = ticketProjectWatchingNoticeDao.queryDetail(ticketProjectWatchingNotice);
//            bizDataJson.put("showLengthTips", ticketProjectWatchingNotice.getShowLengthTips());   //演出时长
//            bizDataJson.put("watchingNotice", ticketProjectWatchingNotice);   //观演须知
//
//            //查询该项目在售的所有的场次信息，按时间排序
//            HashMap<String, Object> reqMap = new HashMap<>();
//            reqMap.put("projectId", projectId);
//            List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
//            //处理场次时间
//            if (events.size() == 1) {
//                bizDataJson.put("timeStr", StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd"));
//            } else if (events.size() > 1) {
//                String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
//                String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "MM.dd");
//                bizDataJson.put("timeStr", str1 + "-" + str2);
//            }
//            List<Integer> prices = new ArrayList<>();
//            Set<String> venues = new HashSet<>();
//            for (TicketEvent event : events) {
//                //场次票档信息
//                HashMap<String, Object> pMap = new HashMap<>();
//                pMap.put("eventId", event.getEventId());
//                pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
//                List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
//                for (TicketEventPrice ticketEventPrice : priceList) {
//                    int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
//                    ticketEventPrice.setPriceMoneyYuan(new BigDecimal((float) priceMoneyFen / 100).setScale(2, BigDecimal.ROUND_HALF_UP));
//                    prices.add(priceMoneyFen);
//                }
//                event.setPriceList(priceList);
//                //场馆
//                venues.add(event.getVenueId());
//            }
//            //获取最低价格
//            int minPrice = Collections.min(prices);
//            bizDataJson.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//            bizDataJson.put("events", events);
//            //场馆信息
//            List<TicketVenue> venueList = new ArrayList<>();
//            for (String venueId : venues) {
//                TicketVenue ticketVenue = new TicketVenue();
//                ticketVenue.setVenueId(venueId);
//                ticketVenue = ticketVenueDao.queryDetail(ticketVenue);
//                venueList.add(ticketVenue);
//            }
//
//            bizDataJson.put("venueList", venueList);
//            //获取演出关联商品信息
//
//
//            retCode = "0";
//            retMsg = "操作成功！";
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    /**
     * 获取选座组件签名字段
     */
    @Override
    public JSONObject getSign(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String eventId = reqJson.getString("eventId");
            String extendInfo = reqJson.getString("extendInfo");
            Map<String, String> map = new HashMap<>();
            map.put("eventId", eventId);
            map.put("appkey", mzClientAppkey);
            map.put("extendInfo", extendInfo);

            String result = "";
            try {
                List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
                // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
                Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                        return (o1.getKey()).toString().compareTo(o2.getKey());
                    }
                });

                // 构造签名键值对的格式
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> item : infoIds) {
                    if (item.getKey() != null || item.getKey() != "") {
                        String key = item.getKey();
                        String val = item.getValue();
                        if (!(val == "" || val == null)) {
                            sb.append(key + val);
                        }
                    }

                }
                result = URLEncoder.encode(mzSignSecret+sb.toString()+mzSignSecret, "utf-8");
                //进行MD5加密
                result = DigestUtils.md5Hex(result);
                bizDataJson.put("sign", result);     //签名
                bizDataJson.put("appkey", mzClientAppkey);    //appKey
                bizDataJson.put("extendInfo", extendInfo);     //扩展信息
                bizDataJson.put("timestamp", System.currentTimeMillis());    //时间戳

            } catch (Exception e) {
                return null;
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
     * 查询默认地址
     */
    @Override
    public JSONObject queryDefaultAddress(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId= CommonUtil.getMzUserId(userId);
            JSONObject dataObj = MZService.getUserAddress(mzUserId, 100, 1);
            JSONObject resObj = null;
            if (null != dataObj) {
                JSONObject data_list = dataObj.getJSONObject("data_list");
                if(null !=data_list.get("user_address_detail_v_o")){
                    JSONArray addressArray = data_list.getJSONArray("user_address_detail_v_o");
                    for (int i = 0; i < addressArray.size(); i++) {
                        JSONObject addressObj = addressArray.getJSONObject(i);
                        resObj = addressObj;
                        if (addressObj.getBoolean("default_address_boolean")) {
                            break;
                        }

                    }
                }

                bizDataJson.put("data", resObj);
                retCode = "0";
                retMsg = "操作成功！";
            } else {
                retMsg = "从麦座获取会员默认地址信息失败！";
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }


    /**
     * 订单渲染
     */
    @Override
    public JSONObject renderOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");   //userId
            String eventId ="";
            TaobaoClient client = MZService.getClient();
            AlibabaDamaiMzOrderRenderRequest req = new AlibabaDamaiMzOrderRenderRequest();
            AlibabaDamaiMzOrderRenderRequest.OrderRenderParam orderRenderParam = new AlibabaDamaiMzOrderRenderRequest.OrderRenderParam();

            String mzUserId= CommonUtil.getMzUserId(userId);
            orderRenderParam.setMzUserId(mzUserId);
            List<AlibabaDamaiMzOrderRenderRequest.RenderGoods4c> RenderGoods = new ArrayList<AlibabaDamaiMzOrderRenderRequest.RenderGoods4c>();
            JSONArray renderGoodsList = reqJson.getJSONArray("render_goods_list");
            for (int i = 0; i < renderGoodsList.size(); i++) {
                JSONObject renderGoods = renderGoodsList.getJSONObject(i);
                AlibabaDamaiMzOrderRenderRequest.RenderGoods4c obj = new AlibabaDamaiMzOrderRenderRequest.RenderGoods4c();
                eventId = renderGoods.getString("event_id");  //场次id
                String priceId = renderGoods.getString("price_id");  //单票票档id
                obj.setEventId(eventId);
                obj.setPriceId(priceId);
                if (null != renderGoods.get("seat_id")) {
                    String seatId = renderGoods.getString("seat_id");  //座位号
                    obj.setSeatId(seatId);
                }
                RenderGoods.add(obj);
            }
            orderRenderParam.setRenderGoodsList(RenderGoods);

            //订单优惠
            if (null != reqJson.get("order_promotion_id")) {
                orderRenderParam.setOrderPromotionId(reqJson.getString("order_promotion_id"));  //选中的订单优惠id
                orderRenderParam.setOrderPromotionRuleId(reqJson.getString("order_promotion_rule_id"));  //订单优惠规则id
            }
            //优惠码
            if (null != reqJson.get("coupon_code")) {
                orderRenderParam.setCouponCode(reqJson.getString("coupon_code"));
            }
            //运费
            JSONObject addObj = null;
            if (null !=reqJson.get("express_use_boolean")&&reqJson.getBoolean("express_use_boolean")) {  //是否使用快递并计算运费；true=是，false=否
                orderRenderParam.setExpressUseBoolean(true);
                orderRenderParam.setExpressPayType(1L);      //运费支付类型；1=在线支付；expressUseBoolean=true时，非空；
            } else {
                orderRenderParam.setExpressUseBoolean(false);
            }

            if(null ==reqJson.get("addressId")||"".equals(reqJson.get("addressId"))&&reqJson.getBoolean("express_use_boolean")){
                //获取用户默认收货地址
                JSONObject addressList = MZService.getUserAddress(mzUserId, 100, 1);
                if (null != addressList) {
                    JSONArray addressArray = addressList.getJSONObject("data_list").getJSONArray("user_address_detail_v_o");
                    if(addressArray!=null){
                        for (int i = 0; i < addressArray.size(); i++) {
                            JSONObject addressObj = addressArray.getJSONObject(i);
                            addObj = addressObj;
                            if (addressObj.getBoolean("default_address_boolean")) {
                                break;
                            }
                        }
                        orderRenderParam.setAddressId(addObj.getString("address_id"));
                    }
                }
            }else{
                String address_id = reqJson.getString("addressId");
                JSONObject addressObj = MZService.getAddressDetail(mzUserId,address_id);
                addObj = addressObj;
                orderRenderParam.setAddressId(reqJson.getString("addressId"));
            }

            if (null != reqJson.get("use_point")) {
                orderRenderParam.setUsePoint(Long.parseLong(reqJson.getString("use_point")));   //使用积分值；单位：点数
                orderRenderParam.setUsePointMoneyFen(Long.parseLong(reqJson.getString("use_point_money_fen")));  //使用积分金额；单位：分；usePoint非空时必填
            }
            if (null != reqJson.get("use_account_money_fen")) {
                orderRenderParam.setUseAccountMoneyFen(Long.parseLong(reqJson.getString("use_account_money_fen")));  //使用账户余额金额；单位：分
            }
            if (null != reqJson.get("use_card_id")) {
                orderRenderParam.setUseCardId(reqJson.getString("use_card_id"));
                orderRenderParam.setUseCardMoneyFen(Long.parseLong(reqJson.getString("use_card_money_fen")));  //使用礼品卡支付金额；单位：分；useCardId非空时必填
            }
            req.setParam(orderRenderParam);
            System.out.println("orderRenderParam==="+req.getParam());
            String reqtime = StringUtil.nowTimeString();
            try {
                AlibabaDamaiMzOrderRenderResponse rsp = client.execute(req);
                String rettime = StringUtil.nowTimeString();

                log.info(rsp.getBody());
                try{
                    JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_render_response").getJSONObject("result");
                    CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"演出订单渲染","POST","AlibabaDamaiMzOrderRenderRequest",
                            reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                    if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                        retCode = "0";
                        retMsg = "操作成功！";
                        JSONObject dataObj = resObj.getJSONObject("data");
                        //查询该场次取票点信息
                        HashMap<String,Object> placeMap = new HashMap<>();
                        placeMap.put("eventId",eventId);
                        List<TicketExchangePlace> places = ticketExchangePlaceDao.queryList(placeMap);
                        dataObj.put("places", places);

                        dataObj.put("address", addObj);
                        bizDataJson.put("data", dataObj);

                        //查询积分和余额免密限额
                        ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                        if (null != shoppingAssetRule) {
                            bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                            bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                        }
                    } else {
                        retCode = "1";
                        retMsg = resObj.getString("msg");
                    }
                }catch (Exception e) {
                    CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"演出订单渲染","POST","AlibabaDamaiMzOrderRenderRequest",
                            reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                    retCode = "1";
                    retMsg = "请求麦座接口失败！";
                    log.error(e);
                }

            } catch (Exception e) {
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"演出订单渲染","POST","AlibabaDamaiMzOrderRenderRequest",
                        reqtime,req.getTextParams().toString(),rettime,e.getMessage());
                retCode = "1";
                retMsg = "请求麦座接口失败！";
                log.error(e);
            }



        } catch (Exception e) {
            retCode = "1";
            retMsg = "服务器内部错误！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 下单
     */
    @Override
    public JSONObject addOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");   //userId
            String eventId = reqJson.getString("event_id");  //场次id
            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量
            //创建订单号
            String orderId = PayUtil.getOrderNo(Const.TICKET_ORDER);

            TaobaoClient client = MZService.getClient();
            AlibabaDamaiMzOrderCreateRequest req = new AlibabaDamaiMzOrderCreateRequest();
            AlibabaDamaiMzOrderCreateRequest.OrderCreateParam orderCreateParam = new AlibabaDamaiMzOrderCreateRequest.OrderCreateParam();
            String mzUserId= CommonUtil.getMzUserId(userId);
            orderCreateParam.setMzUserId(mzUserId);
            orderCreateParam.setExternalOrderNo(orderId);   //外部order号
            List<AlibabaDamaiMzOrderCreateRequest.Goods> goods = new ArrayList<AlibabaDamaiMzOrderCreateRequest.Goods>();
            JSONArray goodsList = reqJson.getJSONArray("goods_list");
            for (int i = 0; i < goodsList.size(); i++) {
                JSONObject goodsObj = goodsList.getJSONObject(i);
                AlibabaDamaiMzOrderCreateRequest.Goods obj = new AlibabaDamaiMzOrderCreateRequest.Goods();
                String event_id = goodsObj.getString("event_id");  //场次id
                String priceId = goodsObj.getString("price_id");  //单票票档id
                obj.setEventId(event_id);
                obj.setPriceId(priceId);
                if (null != goodsObj.get("seat_id")) {
                    String seatId = goodsObj.getString("seat_id");  //座位号
                    obj.setSeatId(seatId);
                }
                //所购买商品实名制类型为一票一证限制时，必传以下实名信息
                if (null != goodsObj.get("real_name")) {
                    String real_name = goodsObj.getString("real_name");  //真实姓名
                    int card_type = goodsObj.getInteger("card_type");  //证件类型；1=身份证
                    String card_no = goodsObj.getString("card_no");  //证件号
                    obj.setRealName(real_name);
                    obj.setCardType(1L);
                    obj.setCardNo(card_no);
                }
                if(null != goodsObj.get("good_identity_list")) {
                    List<AlibabaDamaiMzOrderCreateRequest.GoodIdentity4c> list7 = new ArrayList<AlibabaDamaiMzOrderCreateRequest.GoodIdentity4c>();
                    JSONArray good_identity_list = goodsObj.getJSONArray("good_identity_list");
                    for(int j=0;j<good_identity_list.size();j++){
                        JSONObject identity = good_identity_list.getJSONObject(j);
                        AlibabaDamaiMzOrderCreateRequest.GoodIdentity4c obj8 = new AlibabaDamaiMzOrderCreateRequest.GoodIdentity4c();
                        obj8.setCardNo(identity.getString("card_no"));
                        obj8.setCardType(1L);
                        obj8.setRealName(identity.getString("real_name"));
                        list7.add(obj8);
                    }
                    obj.setGoodIdentityList(list7);
                }

                goods.add(obj);
            }
            orderCreateParam.setGoodsList(goods);

            //取票信息
            int delivery_type = reqJson.getInteger("delivery_type");  //取票方式；2=身份证自助换票（换纸质票），3=取票码自助换票（换纸质票），4=二维码自助换票（换纸质票），6=二维码电子票（无纸质票），7=快递
            orderCreateParam.setDeliveryType(Long.valueOf(delivery_type));

            String collector_name = reqJson.getString("collector_name");   //取票人真实姓名；非空；票类型deliveryType 为 （2=身份证自助换票（换纸质票））或 购买商品为一单一证时，必须传证件真实姓名
            String collector_phone = reqJson.getString("collector_phone");   //取票人手机号，11位；非空
            orderCreateParam.setCollectorName(collector_name);
            orderCreateParam.setCollectorPhone(collector_phone);
            if (null != reqJson.get("collector_card_no")) {
                String collector_card_no = reqJson.getString("collector_card_no");   //取票人证件号；票类型deliveryType 为 （2=身份证自助换票（换纸质票））或 购买商品为一单一证时，必传
                int collector_card_type = reqJson.getInteger("collector_card_type");
                orderCreateParam.setCollectorCardType(Long.valueOf(collector_card_type));
                orderCreateParam.setCollectorCardNo(collector_card_no);
            }
            //消费者实付金额 单位：分
            int order_payment_money_fen = reqJson.getInteger("order_payment_money_fen");
            //麦座订单应收金额；单位：分；麦座订单应收金额=商品应收金额+运费金额-优惠金额；取票方式为快递时，必须计入运费；
            int order_receive_money_fen = reqJson.getInteger("order_receive_money_fen");
            orderCreateParam.setOrderPaymentMoneyFen(Long.valueOf(order_payment_money_fen));
            orderCreateParam.setOrderReceiveMoneyFen(Long.valueOf(order_receive_money_fen));

            //订单优惠
            if (null != reqJson.get("order_promotion_id")) {
                orderCreateParam.setOrderPromotionId(reqJson.getString("order_promotion_id"));
                orderCreateParam.setOrderPromotionRuleId(reqJson.getString("order_promotion_rule_id"));
            }
            //优惠码
            if (null != reqJson.get("coupon_code")) {
                orderCreateParam.setCouponCode(reqJson.getString("coupon_code"));
                orderCreateParam.setCouponCodePromotionRuleId(reqJson.getString("coupon_code_promotion_rule_id"));
            }
            //运费
            if (null != reqJson.get("express_pay_type") && reqJson.getInteger("express_pay_type") == 1) { //运费支付类型；1=在线支付；deliveryType=7 快递时，非空；
                orderCreateParam.setExpressPayType(1L);
                orderCreateParam.setAddressId(reqJson.getString("addressId"));
            }
            //积分
            if (null != reqJson.get("use_point")) {
                orderCreateParam.setUsePoint(Long.parseLong(reqJson.getString("use_point")));   //使用积分值；单位：点数
                orderCreateParam.setUsePointMoneyFen(Long.parseLong(reqJson.get("use_point_money_fen").toString()));  //使用积分金额；单位：分；usePoint非空时必填
            }
            //余额
            if (null != reqJson.get("use_account_money_fen")) {
                orderCreateParam.setUseAccountMoneyFen(Long.parseLong(reqJson.getString("use_account_money_fen")));  //使用账户余额金额；单位：分
            }
            //礼品卡
            if (null != reqJson.get("use_card_id")) {
                orderCreateParam.setUseCardId(reqJson.getString("use_card_id"));
                orderCreateParam.setUseCardMoneyFen(Long.parseLong(reqJson.get("use_card_money_fen").toString()));  //使用礼品卡支付金额；单位：分；useCardId非空时必填
            }
            if (null != reqJson.get("asset_biz_key")) {
                orderCreateParam.setAssetBizKey(reqJson.getString("asset_biz_key"));
            }
            orderCreateParam.setRemark("江苏大剧院APP");
            req.setParam(orderCreateParam);
            String reqtime = StringUtil.nowTimeString();
            try {
                AlibabaDamaiMzOrderCreateResponse rsp = client.execute(req);
                String rettime = StringUtil.nowTimeString();
                log.info(rsp.getBody());
                try{
                    JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_create_response").getJSONObject("result");
                    CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"演出下单","POST","AlibabaDamaiMzOrderCreateRequest",
                            reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                    if (resObj.get("code").equals("200") && resObj.getBoolean("success")) {
                        JSONObject dataObj = resObj.getJSONObject("data");
                        String mz_order_id = dataObj.getString("mz_order_id");  //麦座订单号
                        String auto_cancel_order_time = dataObj.getString("auto_cancel_order_time");   //麦座超时未支付取消订单时间点，精确到秒
                        boolean need_online_pay = dataObj.getBoolean("need_online_pay");   //是否需要在线支付，下单时若使用余额、积分、礼品卡抵扣完整单金额则不需要在线支付；
                        int online_payment_money_fen = dataObj.getInteger("online_payment_money_fen");  //需在线支付的金额； 订单应收金额（商品原价-各种优惠+运费(若有)） - 积分抵扣的金额 - 使用余额支付的金额 - 礼品卡抵扣金额；单位：分

                        //创建本地订单
                        ShoppingOrderform orderform = new ShoppingOrderform();
                        //订单id（系统订单全局唯一标识）
                        orderform.setOrderId(orderId);
                        //关联麦座订单id
                        orderform.setOutOrderId(mz_order_id);
                        //订单类型：演出票
                        orderform.setOrderType(Const.TICKET_ORDER_TYPE);
                        //订单状态：待支付
                        orderform.setOrderStatus(10);
                        //运费金额
                        BigDecimal shipPrice = BigDecimal.ZERO;
                        if (null != reqJson.get("express_money_fen")) {
                            int express_money_fen = reqJson.getInteger("express_money_fen");
                            shipPrice = new BigDecimal(express_money_fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        }
                        orderform.setShipPrice(shipPrice);
                        //商品金额
                        int goods_original_total_money_fen = reqJson.getInteger("goods_original_total_money_fen");
                        BigDecimal goodsprice = new BigDecimal(goods_original_total_money_fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        //订单总金额（商品金额+运费）
                        orderform.setTotalPrice(goodsprice.add(shipPrice));
                        //麦座订单应收金额（麦座订单应收金额=商品应收金额+运费金额-优惠金额；取票方式为快递时，必须计入运费）
                        orderform.setOrderTolPrice(new BigDecimal(order_receive_money_fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));

                        //订单支付信息
                        ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                        shoppingOrderPay.setUserId(userId);

                        //优惠总金额(套票、票品优惠、订单优惠、优惠码优惠)
                        //由于目前的订单设计不支持将演出订单的优惠细分为票品优惠、订单优惠等部分进行记录，因此暂时通过文字备注的方式记录演出订单详细的优惠信息
                        if (null != reqJson.get("total_promotion_money_fen")) {
                            int total_promotion_money_fen = reqJson.getInteger("total_promotion_money_fen");
                            BigDecimal couponCut = new BigDecimal(total_promotion_money_fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                            orderform.setDeductionCouponPrice(couponCut);
                            shoppingOrderPay.setCouponStatus(0);
                        }

                        //积分抵扣
                        if (null != reqJson.get("use_point")) {
                            int use_point = reqJson.getInteger("use_point");
                            int use_point_money_fen = reqJson.getInteger("use_point_money_fen");
                            BigDecimal integralCut = new BigDecimal(use_point_money_fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                            orderform.setDeductionIntegral(use_point);
                            orderform.setDeductionIntegralPrice(integralCut);
                            shoppingOrderPay.setIntegralStatus(0);
                        }

                        //余额
                        if (null != reqJson.get("use_account_money_fen")) {
                            int use_account_money_fen = reqJson.getInteger("use_account_money_fen");
                            BigDecimal balanceCut = new BigDecimal(use_account_money_fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                            orderform.setDeductionBalancePrice(balanceCut);
                            shoppingOrderPay.setBalanceStatus(0);
                        }

                        //需支付的现金金额
                        BigDecimal payPrice = new BigDecimal(online_payment_money_fen).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        orderform.setPayPrice(payPrice);
//                if (payPrice.compareTo(BigDecimal.ZERO) == 1) {
//                    orderform.setPayPrice(new BigDecimal(0.01));
//                }
                        if (payPrice.compareTo(BigDecimal.ZERO) == 1) {
                            shoppingOrderPay.setCashStatus(0);
                        }

                        //商店id
                        orderform.setStoreId(Const.STORE_ID);
                        //用户id
                        orderform.setUserId(userId);
                        //保存订单信息
                        shoppingOrderformDao.insert(orderform);

                        //保存订单支付信息
                        shoppingOrderPay.setOfId(orderform.getId());
                        shoppingOrderPayDao.insert(shoppingOrderPay);

                        // 添加订单日志
                        ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                        shoppingOrderLog.setLogInfo("提交订单");
                        shoppingOrderLog.setLogUserId(userId);
                        shoppingOrderLog.setOfId(orderform.getId());
                        shoppingOrderLogDao.insert(shoppingOrderLog);

                        //保存订单-商品关联信息
                        ShoppingGoodscart goodscart = new ShoppingGoodscart();
                        String scId = CommonUtil.getUserScId(userId);
                        goodscart.setScId(scId);
                        goodscart.setGoodsId(eventId);
                        goodscart.setCartType(Const.TICKET_CART_TYPE);
                        goodscart.setCount(goodsCount);
                        if (null != reqJson.get("propertys")) {
                            goodscart.setSpecInfo(reqJson.getString("specInfo"));
                            goodscart.setPropertys(reqJson.getString("propertys"));
                        }
                        //查询场次信息
                        TicketEvent ticketEvent = new TicketEvent();
                        ticketEvent.setEventId(eventId);
                        ticketEvent = ticketEventDao.queryDetail(ticketEvent);
                        goodscart.setSpecInfo(ticketEvent!=null?ticketEvent.getEventName():"");
                        //下单时的商品价格
                        goodscart.setPrice(goodsprice);
                        goodscart.setOfId(orderform.getId());
                        shoppingGoodscartDao.insert(goodscart);

                        //获取当前系统可用支付方式
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("deleteStatus", 0);
                        List<ShoppingPayment> payments = shoppingPaymentDao.queryList(reqMap);
                        JSONArray payArray = new JSONArray();
                        for (ShoppingPayment shoppingPayment : payments) {
                            JSONObject obj = new JSONObject();
                            obj.put("id", shoppingPayment.getId());
                            obj.put("name", shoppingPayment.getName());
                            payArray.add(obj);
                        }
                        bizDataJson.put("payments", payArray);
                        bizDataJson.put("payPrice", payPrice);
                        bizDataJson.put("orderId", orderId);

                        retCode = "0";
                        retMsg = "操作成功！";
                    } else {
                        retCode = "1";
                        JSONObject dataObj = resObj.getJSONObject("data");
//                        bizDataJson.put("good_error_tip_info_list", dataObj.getJSONObject("good_error_tip_info_list"));
                        retMsg = resObj.getString("msg");
                        CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"演出下单","POST","AlibabaDamaiMzOrderCreateRequest",
                                reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                    }
                }
                catch (Exception e){
                    CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"演出下单","POST","AlibabaDamaiMzOrderCreateRequest",
                            reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                    retMsg="演出下单失败";
                }
            } catch (Exception e) {
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"演出下单","POST","AlibabaDamaiMzOrderCreateRequest",
                        reqtime,req.getTextParams().toString(),rettime,e.getMessage());
                retMsg="调用麦座接口失败";
                log.error(e);
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 手动生成homeData并放入缓存
     */
    @Override
    public void setHomeData() {
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            //热门推荐
            JSONArray recList = new JSONArray();
            List<TicketClass> classList = ticketClassDao.queryList(reqMap);
            JSONArray objList = new JSONArray();
            for (TicketClass ticketClass : classList) {
                JSONObject classObj = new JSONObject();
                String classId = ticketClass.getClassId();
                classObj.put("classId", classId);
                classObj.put("className", ticketClass.getClassName());
                reqMap.clear();
                reqMap.put("classId", classId);
                List<TicketEvent> eventList = ticketEventDao.queryClassEvents(reqMap);
                JSONArray projectList = new JSONArray();
                for (TicketEvent ticketEvent : eventList) {
                    JSONObject projectObj = new JSONObject();
                    String projectId = ticketEvent.getProjectId();  //演出项目id
                    //查询演出详情
                    TicketProject ticketProject = new TicketProject();
                    ticketProject.setProjectId(projectId);
                    ticketProject = ticketProjectDao.queryDetail(ticketProject);
                    projectObj.put("projectId", projectId);   //项目id
                    projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                    projectObj.put("projectName", ticketProject.getProjectName());       //名称
                    projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                    projectObj.put("venueName", ticketEvent.getVenueName());   //场馆名称
                    //查询该项目满足查询条件的所有的场次信息，按时间排序
                    reqMap.put("projectId", projectId);
                    List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                    //处理场次时间
                    if (events.size() == 1) {
                        String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                        String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                        projectObj.put("timeStr", str1+" "+str2);

                    } else if (events.size() > 1) {
                        String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                        String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                        String dayStr = str1 + "-" + str2;
                        String timeStr="";
                        List<String> times = new ArrayList<>();
                        for(TicketEvent event:events){
                            String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                            if(!times.contains(str)){
                                timeStr=timeStr+str+"/";
                                times.add(str);
                            }
                        }
                        if(!timeStr.equals("")){
                            timeStr=timeStr.substring(0,timeStr.length()-1);
                        }
                        projectObj.put("timeStr", dayStr + " " + timeStr);
                    }
                    //处理价格
                    List<Integer> prices = new ArrayList<>();
                    for (TicketEvent event : events) {
                        //场次下票档信息
                        HashMap<String, Object> pMap = new HashMap<>();
                        pMap.put("eventId", event.getEventId());
                        pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                        List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                        for (TicketEventPrice ticketEventPrice : priceList) {
                            int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                            prices.add(priceMoneyFen);
                        }
                    }
                    //获取最低价格
                    if(prices.size()>0){
                        int minPrice = Collections.min(prices);
                        projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格

                    }
                    projectList.add(projectObj);
                    projectObj.put("className", ticketProject.getFirstClassName());
                }
                classObj.put("projectList", projectList);
                objList.add(classObj);
            }
            bizDataJson.put("dataList", objList);
            //推荐数据
            List<TicketRecommend> recommends = ticketRecommendDao.queryList(new HashMap<>());
            for (TicketRecommend ticketRecommend : recommends) {
                JSONObject projectObj = new JSONObject();
                String projectId = ticketRecommend.getProjectId();  //演出项目id

                //查询演出详情
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketProjectDao.queryDetail(ticketProject);
                if(ticketProject.getProjectSaleState()==2){   //销售中
                    projectObj.put("projectId", projectId);   //项目id
                    projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                    projectObj.put("projectName", ticketProject.getProjectName());       //名称
                    projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座

                    //查询该项目满足查询条件的所有的场次信息，按时间排序
                    reqMap.put("projectId", projectId);
                    List<TicketEvent> events = ticketEventDao.queryProjectEvents(reqMap);
                    if(events.size()>0){
                        projectObj.put("venueName", events.get(0).getVenueName());   //场馆名称
                    }

                    //处理场次时间
                    if (events.size() == 1) {
                        String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                        String str2 = StringUtil.formatDate(events.get(0).getEventStartTime(), "HH:mm");
                        projectObj.put("timeStr", str1+" "+str2);

                    } else if (events.size() > 1) {
                        String str1 = StringUtil.formatDate(events.get(0).getEventStartTime(), "yyyy.MM.dd");
                        String str2 = StringUtil.formatDate(events.get(events.size() - 1).getEventStartTime(), "yyyy.MM.dd");
                        String dayStr = str1 + "-" + str2;
                        String timeStr="";
                        List<String> times = new ArrayList<>();
                        for(TicketEvent event:events){
                            String str = StringUtil.formatDate(event.getEventStartTime(), "HH:mm");
                            if(!times.contains(str)){
                                timeStr=timeStr+str+"/";
                                times.add(str);
                            }
                        }
                        if(!timeStr.equals("")){
                            timeStr=timeStr.substring(0,timeStr.length()-1);
                        }
                        projectObj.put("timeStr", dayStr + " " + timeStr);
                    }
                    //处理价格
                    List<Integer> prices = new ArrayList<>();
                    for (TicketEvent event : events) {
                        //场次下票档信息
                        HashMap<String, Object> pMap = new HashMap<>();
                        pMap.put("eventId", event.getEventId());
                        pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
                        List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
                        for (TicketEventPrice ticketEventPrice : priceList) {
                            int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
                            prices.add(priceMoneyFen);
                        }
                    }
                    //获取最低价格
                    if(prices.size()>0){
                        int minPrice = Collections.min(prices);
                        projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格
                    }
                    recList.add(projectObj);
                }
            }
            bizDataJson.put("recList", recList);
            jedisPoolUtils.setKey("homeData",bizDataJson.toJSONString());
        } catch (Exception e) {
            log.error(e);
        }

    }
}
