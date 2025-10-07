package com.centit.shopping.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.TicketRedeemService;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
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
public class TicketRedeemServiceImpl implements TicketRedeemService {
    public static final Log log = LogFactory.getLog(TicketRedeemService.class);

    @Resource
    private TicketRedeemCodeDao ticketRedeemCodeDao;

    @Resource
    private TicketRedeemEventDao ticketRedeemEventDao;

    @Resource
    private TicketRedeemProjectDao ticketRedeemProjectDao;

    @Resource
    private TicketRedeemProjectWatchingNoticeDao ticketRedeemProjectWatchingNoticeDao;

    @Resource
    private TicketVenueDao ticketVenueDao;

    @Resource
    private TicketRedeemActivityDao ticketRedeemActivityDao;

    @Resource
    private TicketRedeemActivityProjectDao ticketRedeemActivityProjectDao;


    @Resource
    private TicketRedeemWriteoffDao ticketRedeemWriteoffDao;

    @Resource
    private TicketRedeemWatchingDao ticketRedeemWatchingDao;

//    @Value("${offcodeLength}")
//    private int offcodeLength;
//
//    @Value("${offcodeExtMins}")
//    private int offcodeExtMins;

    private static int offcodeLength=6;

    private static int offcodeExtMins=1;

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

            reqMap.put("inSaleTime", 1);
            reqMap.put("isDelete", "0");
            reqMap.put("projectSaleState", 2);
            reqMap.put("eventSaleState", 2);

            bizDataJson.put("total", ticketRedeemEventDao.queryTotalCount(reqMap));
            List<TicketRedeemEvent> eventList = ticketRedeemEventDao.queryPageList(reqMap);
            for (TicketRedeemEvent ticketEvent : eventList) {
                JSONObject projectObj = new JSONObject();
                String projectId = ticketEvent.getProjectId();  //演出项目id
                //查询演出详情
                TicketRedeemProject ticketProject = new TicketRedeemProject();
                ticketProject.setProjectId(projectId);
                ticketProject = ticketRedeemProjectDao.queryDetail(ticketProject);
                projectObj.put("projectId", projectId);   //项目id
                projectObj.put("projectImgUrl", ticketProject.getProjectImgUrl());   //海报图片
                projectObj.put("projectName", ticketProject.getProjectName());       //名称
                projectObj.put("projectSeatType", ticketProject.getProjectSeatType());       //项目座位类型；1=有座自助选座，2=无座
                projectObj.put("venueName", ticketEvent.getVenueName());   //场馆名称
                //查询该项目满足查询条件的所有的场次信息，按时间排序
                reqMap.put("projectId", projectId);
                List<TicketRedeemEvent> events = ticketRedeemEventDao.queryProjectEvents(reqMap);
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
                    for(TicketRedeemEvent event:events){
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
//                //处理价格
//                List<Integer> prices = new ArrayList<>();
//                for (TicketEvent event : events) {
//                    //场次下票档信息
//                    HashMap<String, Object> pMap = new HashMap<>();
//                    pMap.put("eventId", event.getEventId());
//                    pMap.put("priceSaleState", 1);   //票档销售状态；1=可售，2=禁售
//                    List<TicketEventPrice> priceList = ticketEventPriceDao.queryList(pMap);
//                    for (TicketEventPrice ticketEventPrice : priceList) {
//                        int priceMoneyFen = ticketEventPrice.getPriceMoneyFen(); //票档价格；单位：分
//                        prices.add(priceMoneyFen);
//                    }
//                }
//                //获取最低价格
//                if(prices.size()>0){
//                    int minPrice = Collections.min(prices);
//                    projectObj.put("minPrice", new BigDecimal((float) minPrice / 100).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());   //价格
//
//                }
                projectObj.put("className", ticketProject.getFirstClassName());
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
            //查询演出详情
            TicketRedeemProject ticketProject = new TicketRedeemProject();
            ticketProject.setProjectId(projectId);
            ticketProject = ticketRedeemProjectDao.queryDetail(ticketProject);

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
            TicketRedeemProjectWatchingNotice ticketProjectWatchingNotice = new TicketRedeemProjectWatchingNotice();
            ticketProjectWatchingNotice.setProjectId(projectId);
            ticketProjectWatchingNotice = ticketRedeemProjectWatchingNoticeDao.queryDetail(ticketProjectWatchingNotice);
            bizDataJson.put("showLengthTips", ticketProjectWatchingNotice.getShowLengthTips());   //演出时长
            bizDataJson.put("watchingNotice", ticketProjectWatchingNotice);   //观演须知

            List<Integer> prices = new ArrayList<>();
            Set<String> venues = new HashSet<>();

            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("inSaleTime", 1);
            reqMap.put("isDelete", "0");
            reqMap.put("eventSaleState", 2);
            reqMap.put("projectId", projectId);
            List<TicketRedeemEvent> events = ticketRedeemEventDao.queryProjectEvents(reqMap);
            for(TicketRedeemEvent ticketRedeemEvent:events){
                //场馆
                venues.add(ticketRedeemEvent.getVenueId());
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
                for(TicketRedeemEvent event:events){
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
     * 兑换项目
     */
    @Override
    public JSONObject exchangeProject(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String projectId = reqJson.getString("projectId");  //演出项目id
            String eventId = reqJson.getString("eventId");  //项目场次id
            String userId = reqJson.getString("userId");  //用户id
            String code = reqJson.getString("code");  //兑换码
            String pwd = reqJson.getString("pwd");  //密码
            String watchingUser = reqJson.getString("watchingUser");  //观看人姓名
            String watchingMobile = reqJson.getString("watchingMobile");  //观看人手机号
            String watchingCard = reqJson.getString("watchingCard");  //观看人证件号

            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //查询演出详情
            TicketRedeemProject ticketProject = new TicketRedeemProject();
            ticketProject.setProjectId(projectId);
            ticketProject = ticketRedeemProjectDao.queryDetail(ticketProject);

            if(ticketProject.getProjectSaleState()==2&&StringUtil.compareMillisecond(ticketProject.getSaleStartTime(),sf)<0&&StringUtil.compareMillisecond(ticketProject.getSaleEndTime(),sf)>0){
                TicketRedeemEvent ticketRedeemEvent =new TicketRedeemEvent();
                ticketRedeemEvent.setEventId(eventId);
                ticketRedeemEvent = ticketRedeemEventDao.queryDetail(ticketRedeemEvent);
//                if(ticketRedeemEvent.getEventSaleState()==2&&StringUtil.compareMillisecond(ticketRedeemEvent.getSaleStartTime(),sf)<0&&StringUtil.compareMillisecond(ticketRedeemEvent.getSaleEndTime(),sf)>0){
                if(ticketRedeemEvent.getEventSaleState()==2){
                    TicketRedeemCode ticketRedeemCode = new TicketRedeemCode();
                    ticketRedeemCode.setCode(code);
                    ticketRedeemCode  = ticketRedeemCodeDao.queryDetail(ticketRedeemCode);
                    if(ticketRedeemCode!=null&&ticketRedeemCode.getIsDelete().equals("0")){
                        if(ticketRedeemCode.getProjectId()==null&&ticketRedeemCode.getEventId()==null){
                            if(ticketRedeemCode.getPwd().equals(pwd)){
                                String activityId = ticketRedeemCode.getActivityId();
                                TicketRedeemActivity ticketRedeemActivity = new TicketRedeemActivity();
                                ticketRedeemActivity.setId(activityId);
                                ticketRedeemActivity = ticketRedeemActivityDao.queryDetail(ticketRedeemActivity);
                                if(ticketRedeemActivity.getIsDelete().equals("0")&&ticketRedeemActivity.getPubStatus().equals("1")&&
                                        StringUtil.compareMillisecond(ticketRedeemActivity.getStartTime(),sf)<0&&StringUtil.compareMillisecond(ticketRedeemActivity.getEndTime(),sf)>0){
                                    List<String> projectIds = new ArrayList<>();
                                    HashMap<String, Object> projMap = new HashMap<>();
                                    projMap.put("activityId", activityId);
                                    List<TicketRedeemActivityProject> projectList = ticketRedeemActivityProjectDao.queryList(projMap);
                                    for(TicketRedeemActivityProject redeemActivityProject:projectList){
                                        projectIds.add(redeemActivityProject.getProjectId());
                                    }
                                    if(projectIds.contains(projectId)){
                                        if(ticketRedeemEvent.getInventory()>0){

                                            //兑换项目，生成核销码
                                            TicketRedeemWriteoff ticketRedeemWriteoff = new TicketRedeemWriteoff();
                                            ticketRedeemWriteoff.setCode(code);
                                            ticketRedeemWriteoff.setOffCode("RE_"+StringUtil.randomOffCode(offcodeLength));
                                            ticketRedeemWriteoff.setExpTime(StringUtil.nowTimePlusMinutes(offcodeExtMins));
                                            ticketRedeemWriteoffDao.insert(ticketRedeemWriteoff);

                                            ShoppingUser user = CommonUtil.getShoppingUserByUserId(userId);
                                            ticketRedeemCode.setProjectId(projectId);
                                            ticketRedeemCode.setEventId(eventId);
                                            ticketRedeemCode.setExchangeUser(userId);
                                            ticketRedeemCode.setExchangeMobile(user.getMobile());
                                            ticketRedeemCode.setExchangeTime(StringUtil.nowTimeMilesString());
                                            ticketRedeemCodeDao.update(ticketRedeemCode);

                                            TicketRedeemWatching ticketRedeemWatching = new TicketRedeemWatching();
                                            ticketRedeemWatching.setCode(code);
                                            ticketRedeemWatching.setWatchingUser(watchingUser);
                                            ticketRedeemWatching.setWatchingMobile(watchingMobile);
                                            ticketRedeemWatching.setWatchingCard(watchingCard);
                                            ticketRedeemWatchingDao.insert(ticketRedeemWatching);


                                            //更新库存
                                            ticketRedeemEvent.setInventory(ticketRedeemEvent.getInventory()-1);
                                            ticketRedeemEventDao.update(ticketRedeemEvent);
                                            retCode = "0";
                                            retMsg = "操作成功！";
                                        }else{
                                            retMsg = "该演出场次已售罄！";
                                        }
                                    }else{
                                        retMsg = "您输入的兑换码不可兑换当前项目！";
                                    }
                                }else{
                                    retMsg = "该兑换码已失效或不在可兑换时间！";
                                }
                            }else{
                                retMsg = "兑换密码错误！";
                            }
                        }else{
                            retMsg = "该兑换码已被使用！";
                        }
                    }else{
                        retMsg = "您输入的兑换码无效！";
                    }
                }else{
                    retMsg = "该演出场次已下架或不在销售时间！";
                }
            }else{
                retMsg = "该演出项目已下架或不在销售时间！";
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
     * 查询我的兑换记录
     */
    @Override
    public JSONObject queryMyExchangeList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = new HashMap<>();
            JSONArray objList = new JSONArray();
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);

            reqMap.put("isDelete", "0");
            reqMap.put("exchangeUser", reqJson.getString("userId"));
            bizDataJson.put("total", ticketRedeemCodeDao.queryMyCodeTotalCount(reqMap));
            List<TicketRedeemCode> codeList = ticketRedeemCodeDao.queryMyCodeList(reqMap);

            JSONArray objArray = new JSONArray();
            for(TicketRedeemCode ticketRedeemCode:codeList){
                JSONObject obj = new JSONObject();
                obj.put("code",ticketRedeemCode.getCode());  //兑换码
                obj.put("exchangeTime",ticketRedeemCode.getExchangeTime());   //兑换时间
                obj.put("projectId",ticketRedeemCode.getProjectId());   //项目ID
                obj.put("projectName",ticketRedeemCode.getProjectName());   //项目名称
                obj.put("projectImgUrl",ticketRedeemCode.getProjectImgUrl());   //项目海报图
                obj.put("eventId",ticketRedeemCode.getEventId());   //场次id
                obj.put("eventStartTime",ticketRedeemCode.getEventStartTime());   //开场时间
                obj.put("venueName",ticketRedeemCode.getVenueName());   //场馆名称
                obj.put("writeoffTime",ticketRedeemCode.getWriteoffTime());   //核销时间
                objArray.add(obj);
            }

            bizDataJson.put("objList", objArray);
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
     * 获取兑换详情
     */
    @Override
    public JSONObject queryMyExchangeDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String code = reqJson.getString("code");
            TicketRedeemCode ticketRedeemCode = new TicketRedeemCode();
            ticketRedeemCode.setCode(code);
            ticketRedeemCode= ticketRedeemCodeDao.queryDetail(ticketRedeemCode);

//            JSONObject obj = new JSONObject();
            bizDataJson.put("code",ticketRedeemCode.getCode());  //兑换码
            bizDataJson.put("exchangeTime",ticketRedeemCode.getExchangeTime());   //兑换时间
            bizDataJson.put("projectId",ticketRedeemCode.getProjectId());   //项目ID
            bizDataJson.put("projectName",ticketRedeemCode.getProjectName());   //项目名称
            bizDataJson.put("projectImgUrl",ticketRedeemCode.getProjectImgUrl());   //项目海报图
            bizDataJson.put("eventId",ticketRedeemCode.getEventId());   //场次id
            bizDataJson.put("eventStartTime",ticketRedeemCode.getEventStartTime());   //开场时间
            bizDataJson.put("venueName",ticketRedeemCode.getVenueName());   //场馆名称
            bizDataJson.put("writeoffTime",ticketRedeemCode.getWriteoffTime());   //核销时间

            //查询观演须知
            TicketRedeemProjectWatchingNotice ticketProjectWatchingNotice = new TicketRedeemProjectWatchingNotice();
            ticketProjectWatchingNotice.setProjectId(ticketRedeemCode.getProjectId());
            ticketProjectWatchingNotice = ticketRedeemProjectWatchingNoticeDao.queryDetail(ticketProjectWatchingNotice);
            bizDataJson.put("watchingNotice", ticketProjectWatchingNotice);   //观演须知

            TicketRedeemWriteoff ticketRedeemWriteoff = new TicketRedeemWriteoff();
            ticketRedeemWriteoff.setCode(code);
            ticketRedeemWriteoff = ticketRedeemWriteoffDao.queryDetail(ticketRedeemWriteoff);
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String offCode = ticketRedeemWriteoff.getOffCode();
            if(!StringUtil.isNotNull(ticketRedeemWriteoff.getOffTime())&&StringUtil.compareMillisecond(ticketRedeemWriteoff.getExpTime(),sf)<0){   //核销码失效
                ticketRedeemWriteoff.setOffCode(StringUtil.randomOffCode(offcodeLength));
                ticketRedeemWriteoff.setExpTime(StringUtil.nowTimePlusMinutes(offcodeExtMins));
                ticketRedeemWriteoffDao.update(ticketRedeemWriteoff);
                offCode = ticketRedeemWriteoff.getOffCode();
            }
            bizDataJson.put("offCode", offCode);   //核销码
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
     * 刷新核销码
     */
    @Override
    public JSONObject refreshOffCode(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String code = reqJson.getString("code");

            TicketRedeemWriteoff ticketRedeemWriteoff = new TicketRedeemWriteoff();
            ticketRedeemWriteoff.setCode(code);
            ticketRedeemWriteoff = ticketRedeemWriteoffDao.queryDetail(ticketRedeemWriteoff);
            ticketRedeemWriteoff.setOffCode("RE_"+StringUtil.randomOffCode(offcodeLength));
            ticketRedeemWriteoff.setExpTime(StringUtil.nowTimePlusMinutes(offcodeExtMins));
            ticketRedeemWriteoffDao.update(ticketRedeemWriteoff);
            bizDataJson.put("offCode", ticketRedeemWriteoff.getOffCode());   //核销码
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
