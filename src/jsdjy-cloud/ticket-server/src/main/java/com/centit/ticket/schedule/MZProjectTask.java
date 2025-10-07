package com.centit.ticket.schedule;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.dao.*;
import com.centit.ticket.po.*;
import com.centit.ticket.utils.MZService;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 麦座演出数据同步
 *
 * @author hy
 */
@Component
public class MZProjectTask {
    public static final Log log = LogFactory.getLog(MZProjectTask.class);
    @Value("${sheduleSwitch.master}")
    private Boolean  masterSwitch;

    @Value("${sheduleSwitch.mzProjectTask}")
    private Boolean  mzProjectTaskSwitch;

    @Value("${filterClassIds}")
    private String  filterClassIds;

    @Resource
    private TicketClassDao ticketClassDao;

    @Resource
    private TicketProjectDao ticketProjectDao;

    @Resource
    private TicketProjectSponsorDao ticketProjectSponsorDao;

    @Resource
    private TicketProjectWatchingNoticeDao ticketProjectWatchingNoticeDao;

    @Resource
    private TicketEventDao ticketEventDao;

    @Resource
    private TicketEventPriceDao ticketEventPriceDao;

    @Resource
    private TicketExchangePlaceDao ticketExchangePlaceDao;

    @Resource
    private TicketExchangePlaceWorktimeDao ticketExchangePlaceWorktimeDao;

    @Resource
    private  TLmThirdlogDao tLmThirdlogDao;

    @Resource
    private TicketVenueDao ticketVenueDao;

    @Resource
    private TConcurrencySwitchDao tConcurrencySwitchDao;

    private List<String> classIds = new ArrayList<>();

    private static boolean isSwitch=true;

    //存放高并发模式时需要同步的演出id
    private List<String> syncProjectIds = new ArrayList<>();

    @Scheduled(cron = "0 */20 * * * ?" )
//    @PostConstruct
    @SchedulerLock(name = "scheduledTask",
            lockAtMostFor = 10 * 60 * 1000, lockAtLeastFor = 2 * 60 * 1000)
    public void scheduledTask() {
        if (masterSwitch&&mzProjectTaskSwitch&&isSwitch) {
            log.info("开始同步麦座演出数据");
            //高并发时间段，只同步新增的演出数据，其他信息都不同步
            if(openConcurrencySwitch()){
                syncProjectInfoForConcurrency();
                for(String projectId:syncProjectIds){
                    syncEventInfoForConcurrency(projectId);
                }
            }else{
                syncProjectClass();
                syncProjectInfo();
                HashMap<String,Object> reqMap = new HashMap<>();
                reqMap.put("projectSaleState",2);
                List<TicketProject> projects= ticketProjectDao.queryList(reqMap);
                for(TicketProject ticketProject:projects){
                    try{
                        syncEventInfo(ticketProject.getProjectId());
                    }catch (Exception e){
                        log.error("同步项目"+ticketProject.getProjectName()+"场次信息失败");
                        e.printStackTrace();
                    }

                }
                syncVenueInfo();
            }

        }

    }

    @Scheduled(cron = "0 2,5,10 15 * * ?" )
//    @PostConstruct
    @SchedulerLock(name = "timeTask",
            lockAtMostFor = 5 * 60 * 1000, lockAtLeastFor = 1 * 60 * 1000)
    public void timeSyncTask() {
        if (masterSwitch&&mzProjectTaskSwitch&&isSwitch) {
            log.info("开始同步麦座演出数据");
            if(openConcurrencySwitch()){
                syncProjectInfoForConcurrency();
                for(String projectId:syncProjectIds){
                    syncEventInfoForConcurrency(projectId);
                }
            }else{
                syncProjectClass();
                syncProjectInfo();
                HashMap<String,Object> reqMap = new HashMap<>();
                reqMap.put("projectSaleState",2);
                List<TicketProject> projects= ticketProjectDao.queryList(reqMap);
                for(TicketProject ticketProject:projects){
                    try{
                        syncEventInfo(ticketProject.getProjectId());
                    }catch (Exception e){
                        log.error("同步项目"+ticketProject.getProjectName()+"场次信息失败");
                        e.printStackTrace();
                    }

                }
                syncVenueInfo();
            }
        }

    }

    public void syncData() {
            log.info("开始手动同步麦座演出数据");
        if(openConcurrencySwitch()){
            syncProjectInfoForConcurrency();
            for(String projectId:syncProjectIds){
                syncEventInfoForConcurrency(projectId);
            }
        }else{
            syncProjectClass();
            syncProjectInfo();
            HashMap<String,Object> reqMap = new HashMap<>();
            reqMap.put("projectSaleState",2);
            List<TicketProject> projects= ticketProjectDao.queryList(reqMap);
            for(TicketProject ticketProject:projects){
                try{
                    syncEventInfo(ticketProject.getProjectId());
                }catch (Exception e){
                    log.error("同步项目"+ticketProject.getProjectName()+"场次信息失败");
                    e.printStackTrace();
                }

            }
            syncVenueInfo();
        }
    }

    /**
     * 项目分类信息同步
     *
     */
    @Transactional
    public void syncProjectClass(){
        JSONObject classObj = MZService.getClassList();
        if(null !=classObj){
            try{
                String[] fClassIds = filterClassIds.split(",");
                List<String> list = new ArrayList<String>();
                Collections.addAll(list, fClassIds);
                List<TicketClass> classes = new ArrayList<>();
                JSONArray classArray = classObj.getJSONArray("project_first_class_v_o");
                for(int i=0;i<classArray.size();i++){
                    JSONObject firstClassObj = classArray.getJSONObject(i);
                    String first_class_id = firstClassObj.getString("first_class_id");  //一级项目分类id
                    if(!list.contains(first_class_id)){
                        classIds.add(first_class_id);
                        String first_class_name = firstClassObj.getString("first_class_name");  //一级项目分类名称
                        TicketClass fclass = new TicketClass();
                        fclass.setClassId(first_class_id);
                        TicketClass tClass =ticketClassDao.queryDetail(fclass);
                        fclass.setIsShow(tClass!=null?tClass.getIsShow():"0");
                        fclass.setClassName(first_class_name);
                        classes.add(fclass);
                        if(null !=firstClassObj.get("child_class_list")){
                            JSONObject child_class_list = firstClassObj.getJSONObject("child_class_list");
                            JSONArray childClassArray = child_class_list.getJSONArray("project_second_class_v_o");
                            for(int j=0;j<childClassArray.size();j++){
                                JSONObject childClassObj = childClassArray.getJSONObject(j);
                                String second_class_id = childClassObj.getString("second_class_id");  //二级项目分类id
                                String second_class_name = childClassObj.getString("second_class_name");  //二级项目分类名称
                                TicketClass chclass = new TicketClass();
                                chclass.setClassId(second_class_id);
                                TicketClass tcClass =ticketClassDao.queryDetail(chclass);
                                chclass.setIsShow(tcClass!=null?tcClass.getIsShow():"0");
                                chclass.setClassName(second_class_name);
                                chclass.setParentClassId(first_class_id);

                                classes.add(chclass);

                            }
                        }
                    }

                }
                //先清除已有数据
                ticketClassDao.delete(new TicketClass());
                for(TicketClass ticketClass:classes){
                    ticketClassDao.insert(ticketClass);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 项目信息同步
     *
     */
    @Transactional
    public void syncProjectInfo(){
        JSONArray projectArray = MZService.getProjectList();
        if(null !=projectArray){
            try{
                List<String> ids = new ArrayList<>();
                for(int i=0;i<projectArray.size();i++){
                    JSONObject projectObj = projectArray.getJSONObject(i);
                    String project_id = projectObj.getString("project_id");  //项目id
                    String project_name = projectObj.getString("project_name");   //项目名称
                    int project_seat_type = projectObj.getInteger("project_seat_type");   //项目座位类型；1=有座自助选座，2=无座
                    int project_sale_state = projectObj.getInteger("project_sale_state");   //项目销售状态；2=销售中；只返回在售项目
                    String first_class_id = projectObj.getString("first_class_id");   //一级项目分类id
                    if(classIds.contains(first_class_id)){
                        ids.add(project_id);
                        String first_class_name = projectObj.getString("first_class_name");    //一级项目分类名称
                        String second_class_id = projectObj.get("second_class_id")==null?"":projectObj.getString("second_class_id");  //二级项目分类id；可空
                        String second_class_name = projectObj.get("second_class_name")==null?"":projectObj.getString("second_class_name");   //二级项目分类名称
                        String project_img_url = projectObj.get("project_img_url")==null?"":projectObj.getString("project_img_url");   //项目海报图片地址；可空
                        String project_round = projectObj.get("project_round")==null?"":projectObj.getString("project_round");    //项目轮次

                        TicketProject ticketProject = new TicketProject();
                        ticketProject.setProjectId(project_id);
                        ticketProject.setProjectName(project_name);
                        ticketProject.setProjectSeatType(project_seat_type);
                        ticketProject.setProjectSaleState(project_sale_state);
                        ticketProject.setFirstClassId(first_class_id);
                        ticketProject.setFirstClassName(first_class_name);
                        ticketProject.setSecondClassId(second_class_id);
                        ticketProject.setSecondClassName(second_class_name);
                        ticketProject.setProjectImgUrl(project_img_url);
                        ticketProject.setProjectRound(project_round);

                        //项目主办信息
                        if(null !=projectObj.get("sponsor_info_list")){
                            try{
                                JSONObject sponsor_info_list = projectObj.getJSONObject("sponsor_info_list");
                                if(null !=sponsor_info_list.get("sponsor_info")){
                                    //先清除已有的项目主办方信息
                                    TicketProjectSponsor projectSponsor = new TicketProjectSponsor();
                                    projectSponsor.setProjectId(project_id);
                                    ticketProjectSponsorDao.delete(projectSponsor);

                                    JSONArray sponsorInfoArray = sponsor_info_list.getJSONArray("sponsor_info");
                                    for(int m=0;m<sponsorInfoArray.size();m++){
                                        JSONObject infoObj = sponsorInfoArray.getJSONObject(m);
                                        String sponsor_name = infoObj.getString("sponsor_name");  //主办方名称
                                        String sponsor_id = infoObj.getString("sponsor_id");   //主办方id
                                        TicketProjectSponsor ticketProjectSponsor = new TicketProjectSponsor();
                                        ticketProjectSponsor.setProjectId(project_id);
                                        ticketProjectSponsor.setSponsorId(sponsor_id);
                                        ticketProjectSponsor.setSponsorName(sponsor_name);
                                        ticketProjectSponsorDao.insert(ticketProjectSponsor);
                                    }
                                }
                            }catch(Exception e){

                            }
                        }

                        //查询项目详情
                        JSONObject detailObj = MZService.getProjectDetail(project_id);
                        if(null !=detailObj){
                            String project_introduce = detailObj.getString("project_introduce");  //项目简介
                            ticketProject.setProjectIntroduce(project_introduce);
                            //观影须知
                            try{
                                if(null !=detailObj.get("project_watching_notice")){
                                    JSONObject watchingNoticeObj = detailObj.getJSONObject("project_watching_notice");
                                    String show_length_tips = watchingNoticeObj.get("show_length_tips")==null?"":watchingNoticeObj.getString("show_length_tips");   //演出时长
                                    String entry_time_tips = watchingNoticeObj.get("entry_time_tips")==null?"":watchingNoticeObj.getString("entry_time_tips");   //入场时间
                                    String children_entry_tips = watchingNoticeObj.get("children_entry_tips")==null?"":watchingNoticeObj.getString("children_entry_tips");   //儿童入场提示
                                    String deposit_tips = watchingNoticeObj.get("deposit_tips")==null?"":watchingNoticeObj.getString("deposit_tips");    //寄存说明
                                    String prohibit_goods_tips = watchingNoticeObj.get("prohibit_goods_tips")==null?"":watchingNoticeObj.getString("prohibit_goods_tips");   //禁止携带的物品说明

                                    TicketProjectWatchingNotice ticketProjectWatchingNotice = new TicketProjectWatchingNotice();
                                    ticketProjectWatchingNotice.setProjectId(project_id);
                                    ticketProjectWatchingNotice.setShowLengthTips(show_length_tips);
                                    ticketProjectWatchingNotice.setEntryTimeTips(entry_time_tips);
                                    ticketProjectWatchingNotice.setChildrenEntryTips(children_entry_tips);
                                    ticketProjectWatchingNotice.setDepositTips(deposit_tips);
                                    ticketProjectWatchingNotice.setProhibitGoodsTips(prohibit_goods_tips);

                                    ticketProjectWatchingNoticeDao.delete(ticketProjectWatchingNotice);
                                    ticketProjectWatchingNoticeDao.insert(ticketProjectWatchingNotice);
                                }
                            }catch(Exception e){

                            }
                        }
                        ticketProjectDao.delete(ticketProject);
                        ticketProjectDao.insert(ticketProject);
                    }
                }
                //将所有不在接口返回数据中的本地项目状态置为"不在售"，即不在移动端展示
                if(ids.size()>0){
                    ticketProjectDao.updateProjectSaleState(ids);
                }else{
                    ticketProjectDao.updateALlProjectSaleState(ids);
                }

            }catch (Exception e){

            }

        }else{

        }
    }

    /**
     * 项目信息同步-高并发时间段
     *
     */
    @Transactional
    public void syncProjectInfoForConcurrency(){
        JSONArray projectArray = MZService.getProjectList();
        if(null !=projectArray){
            try{
                List<String> classIds = new ArrayList<>();
                List<TicketClass> classList = ticketClassDao.queryList(null);
                for(TicketClass ticketClass:classList){
                    if(!filterClassIds.contains(ticketClass.getClassId())){
                        classIds.add(ticketClass.getClassId());
                    }
                }
                List<String> existProjectIds =ticketProjectDao.queryIds(null);
                TConcurrencySwitch tConcurrencySwitch=tConcurrencySwitchDao.queryDetail(null);
                String syncProjectid= tConcurrencySwitch.getSyncProjectid()==null?"":tConcurrencySwitch.getSyncProjectid();
//                List<String> ids = new ArrayList<>();
                for(int i=0;i<projectArray.size();i++){
                    JSONObject projectObj = projectArray.getJSONObject(i);
                    String project_id = projectObj.getString("project_id");  //项目id
                    String project_name = projectObj.getString("project_name");   //项目名称
                    int project_seat_type = projectObj.getInteger("project_seat_type");   //项目座位类型；1=有座自助选座，2=无座
                    int project_sale_state = projectObj.getInteger("project_sale_state");   //项目销售状态；2=销售中；只返回在售项目
                    String first_class_id = projectObj.getString("first_class_id");   //一级项目分类id
                    if(classIds.contains(first_class_id)&&(!existProjectIds.contains(project_id)||syncProjectid.contains(project_id))){
                        syncProjectIds.add(project_id);
//                        ids.add(project_id);
                        String first_class_name = projectObj.getString("first_class_name");    //一级项目分类名称
                        String second_class_id = projectObj.get("second_class_id")==null?"":projectObj.getString("second_class_id");  //二级项目分类id；可空
                        String second_class_name = projectObj.get("second_class_name")==null?"":projectObj.getString("second_class_name");   //二级项目分类名称
                        String project_img_url = projectObj.get("project_img_url")==null?"":projectObj.getString("project_img_url");   //项目海报图片地址；可空
                        String project_round = projectObj.get("project_round")==null?"":projectObj.getString("project_round");    //项目轮次

                        TicketProject ticketProject = new TicketProject();
                        ticketProject.setProjectId(project_id);
                        ticketProject.setProjectName(project_name);
                        ticketProject.setProjectSeatType(project_seat_type);
                        ticketProject.setProjectSaleState(project_sale_state);
                        ticketProject.setFirstClassId(first_class_id);
                        ticketProject.setFirstClassName(first_class_name);
                        ticketProject.setSecondClassId(second_class_id);
                        ticketProject.setSecondClassName(second_class_name);
                        ticketProject.setProjectImgUrl(project_img_url);
                        ticketProject.setProjectRound(project_round);

                        //项目主办信息
                        if(null !=projectObj.get("sponsor_info_list")){
                            try{
                                JSONObject sponsor_info_list = projectObj.getJSONObject("sponsor_info_list");
                                if(null !=sponsor_info_list.get("sponsor_info")){
                                    //先清除已有的项目主办方信息
                                    TicketProjectSponsor projectSponsor = new TicketProjectSponsor();
                                    projectSponsor.setProjectId(project_id);
                                    ticketProjectSponsorDao.delete(projectSponsor);

                                    JSONArray sponsorInfoArray = sponsor_info_list.getJSONArray("sponsor_info");
                                    for(int m=0;m<sponsorInfoArray.size();m++){
                                        JSONObject infoObj = sponsorInfoArray.getJSONObject(m);
                                        String sponsor_name = infoObj.getString("sponsor_name");  //主办方名称
                                        String sponsor_id = infoObj.getString("sponsor_id");   //主办方id
                                        TicketProjectSponsor ticketProjectSponsor = new TicketProjectSponsor();
                                        ticketProjectSponsor.setProjectId(project_id);
                                        ticketProjectSponsor.setSponsorId(sponsor_id);
                                        ticketProjectSponsor.setSponsorName(sponsor_name);
                                        ticketProjectSponsorDao.insert(ticketProjectSponsor);
                                    }
                                }
                            }catch(Exception e){

                            }
                        }

                        //查询项目详情
                        JSONObject detailObj = MZService.getProjectDetail(project_id);
                        if(null !=detailObj){
                            String project_introduce = detailObj.getString("project_introduce");  //项目简介
                            ticketProject.setProjectIntroduce(project_introduce);
                            //观影须知
                            try{
                                if(null !=detailObj.get("project_watching_notice")){
                                    JSONObject watchingNoticeObj = detailObj.getJSONObject("project_watching_notice");
                                    String show_length_tips = watchingNoticeObj.get("show_length_tips")==null?"":watchingNoticeObj.getString("show_length_tips");   //演出时长
                                    String entry_time_tips = watchingNoticeObj.get("entry_time_tips")==null?"":watchingNoticeObj.getString("entry_time_tips");   //入场时间
                                    String children_entry_tips = watchingNoticeObj.get("children_entry_tips")==null?"":watchingNoticeObj.getString("children_entry_tips");   //儿童入场提示
                                    String deposit_tips = watchingNoticeObj.get("deposit_tips")==null?"":watchingNoticeObj.getString("deposit_tips");    //寄存说明
                                    String prohibit_goods_tips = watchingNoticeObj.get("prohibit_goods_tips")==null?"":watchingNoticeObj.getString("prohibit_goods_tips");   //禁止携带的物品说明

                                    TicketProjectWatchingNotice ticketProjectWatchingNotice = new TicketProjectWatchingNotice();
                                    ticketProjectWatchingNotice.setProjectId(project_id);
                                    ticketProjectWatchingNotice.setShowLengthTips(show_length_tips);
                                    ticketProjectWatchingNotice.setEntryTimeTips(entry_time_tips);
                                    ticketProjectWatchingNotice.setChildrenEntryTips(children_entry_tips);
                                    ticketProjectWatchingNotice.setDepositTips(deposit_tips);
                                    ticketProjectWatchingNotice.setProhibitGoodsTips(prohibit_goods_tips);

                                    ticketProjectWatchingNoticeDao.delete(ticketProjectWatchingNotice);
                                    ticketProjectWatchingNoticeDao.insert(ticketProjectWatchingNotice);
                                }
                            }catch(Exception e){

                            }
                        }
                        ticketProjectDao.delete(ticketProject);
                        ticketProjectDao.insert(ticketProject);
                    }
                }
//                //将所有不在接口返回数据中的本地项目状态置为"不在售"，即不在移动端展示
//                if(ids.size()>0){
//                    ticketProjectDao.updateProjectSaleState(ids);
//                }else{
//                    ticketProjectDao.updateALlProjectSaleState(ids);
//                }

            }catch (Exception e){

            }

        }else{

        }
    }

    /**
     * 项目场次信息同步
     *
     */
    @Transactional
    public void syncEventInfo(String project_id){
        JSONObject resObj = MZService.getProjectEvent(project_id);
        if(null !=resObj&&null !=resObj.get("event_v_o")){
            try{
                List<String> ids = new ArrayList<>();
                JSONArray eventArray = resObj.getJSONArray("event_v_o");
                for(int i=0;i<eventArray.size();i++){
                    JSONObject eventObj = eventArray.getJSONObject(i);
                    if(project_id.equals(eventObj.getString("project_id"))){
                        String event_id = eventObj.getString("event_id");   //麦座场次id
                        ids.add(event_id);
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
                        ticketEvent.setProjectId(project_id);
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
                        //保存场次信息
                        ticketEventDao.delete(ticketEvent);
                        ticketEventDao.insert(ticketEvent);

                        //场次下票档信息
                        if(null !=eventObj.get("price_list")){
                            try{
                                TicketEventPrice eventPrice = new TicketEventPrice();
                                eventPrice.setEventId(event_id);
                                ticketEventPriceDao.delete(eventPrice);

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
                                        ticketEventPrice.setPriceColor(price_color);
                                        ticketEventPrice.setMarginStockNum(margin_stock_num);
                                        ticketEventPriceDao.insert(ticketEventPrice);
                                    }
                                }
                            }catch (Exception e){
                                log.error("获取场次下票档信息失败:"+eventObj);
                                e.printStackTrace();
                            }
                        }

                        //当前场次取票点信息
                        if(null !=eventObj.get("exchange_place_list")){
                            try{
                                TicketExchangePlace exchangePlace = new TicketExchangePlace();
                                exchangePlace.setEventId(event_id);
                                ticketExchangePlaceDao.delete(exchangePlace);

                                JSONObject exchange_place_list = eventObj.getJSONObject("exchange_place_list");
                                if(null !=exchange_place_list.get("exchange_place")){
                                    JSONArray placeArray = exchange_place_list.getJSONArray("exchange_place");
                                    for(int m=0;m<placeArray.size();m++){
                                        JSONObject placeObj = placeArray.getJSONObject(m);
                                        String address = placeObj.getString("address");
                                        String longitude = placeObj.getString("longitude");
                                        String latitude = placeObj.getString("latitude");
                                        TicketExchangePlace ticketExchangePlace = new TicketExchangePlace();
                                        ticketExchangePlace.setEventId(event_id);
                                        ticketExchangePlace.setAddress(address);
                                        ticketExchangePlace.setPlaceId(address);
                                        ticketExchangePlace.setLongitude(longitude);
                                        ticketExchangePlace.setLatitude(latitude);
                                        //取票点类型
                                        String placeTypeList ="";
                                        if(null !=placeObj.get("place_type_list")){
                                            try{
                                                JSONObject place_type_list = placeObj.getJSONObject("place_type_list");
                                                if(null !=place_type_list.get("number")){

                                                    List<Integer> list = (List<Integer>) place_type_list.get("number");
                                                    for(int num:list){
                                                        placeTypeList += num+",";
                                                    }
                                                }
                                            }catch (Exception e){
                                                log.error("获取取票点类型list失败:"+eventObj);
                                                e.printStackTrace();
                                            }
                                        }
                                        ticketExchangePlace.setPlaceTypeList(placeTypeList);
                                        ticketExchangePlaceDao.insert(ticketExchangePlace);
                                        //工作时间list
                                        if(null !=placeObj.get("work_time_list")){
                                            try{
                                                TicketExchangePlaceWorktime exchangePlaceWorktime = new TicketExchangePlaceWorktime();
                                                exchangePlaceWorktime.setPlaceId(address);
                                                ticketExchangePlaceWorktimeDao.delete(exchangePlaceWorktime);

                                                JSONObject work_time_list = placeObj.getJSONObject("work_time_list");
                                                if(null !=work_time_list.get("work_time")){
                                                    JSONArray workTimeArray = work_time_list.getJSONArray("work_time");
                                                    for(int n=0;n<workTimeArray.size();n++){
                                                        JSONObject workTimeObj =workTimeArray.getJSONObject(n);
                                                        int work_date_type = workTimeObj.getInteger("work_date_type"); //工作时间类型；1=固定时间，2=自定义时间
                                                        String work_date_desc = workTimeObj.get("work_date_desc")==null?"":workTimeObj.getString("work_date_desc");  //自定义工作时间；当work_date_type=2时非空
                                                        String work_start_time = workTimeObj.get("work_start_time")==null?"":workTimeObj.getString("work_start_time");  //工作时间，开始时间 格式：HH:mm；当work_date_type=1时非空
                                                        String work_end_time = workTimeObj.get("work_end_time")==null?"":workTimeObj.getString("work_end_time");  //工作时间，结束时间 格式：HH:mm；当work_date_type=1时非空
                                                        TicketExchangePlaceWorktime ticketExchangePlaceWorktime =new TicketExchangePlaceWorktime();
                                                        ticketExchangePlaceWorktime.setPlaceId(address);
                                                        ticketExchangePlaceWorktime.setWorkDateType(work_date_type);
                                                        ticketExchangePlaceWorktime.setWorkDateDesc(work_date_desc);
                                                        ticketExchangePlaceWorktime.setWorkStartTime(work_start_time);
                                                        ticketExchangePlaceWorktime.setWorkEndTime(work_end_time);
                                                        //具体一周中周几工作
                                                        String workDateSets ="";
                                                        if(null !=workTimeObj.get("work_date_sets")){
                                                            try{
                                                                JSONObject work_date_sets = workTimeObj.getJSONObject("work_date_sets");
                                                                if(null !=work_date_sets.get("number")){

                                                                    List<Integer> list = (List<Integer>) work_date_sets.get("number");
                                                                    for(int num:list){
                                                                        workDateSets += num+",";
                                                                    }
                                                                }
                                                            }catch (Exception e){
                                                                log.error("获取一周中周几工作失败:"+eventObj);
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        ticketExchangePlaceWorktime.setWorkDateSets(workDateSets);
                                                        ticketExchangePlaceWorktimeDao.insert(ticketExchangePlaceWorktime);
                                                    }

                                                }
                                            }catch (Exception e){

                                            }
                                        }
                                    }
                                }

                            }catch (Exception e){
                                log.error("获取场次取票点信息失败:"+eventObj);
                                e.printStackTrace();
                            }
                        }
                    }
                }
                //将所有不在接口返回数据中的场次状态置为"不在售"，即不在移动端展示
                if(ids.size()>0){
                    HashMap<String,Object> reqMap = new HashMap<>();
                    reqMap.put("ids",ids);
                    reqMap.put("projectId",project_id);
                    ticketEventDao.updateEventSaleState(reqMap);
                }else{
                    HashMap<String,Object> reqMap = new HashMap<>();
                    reqMap.put("ids",ids);
                    reqMap.put("projectId",project_id);
                    ticketEventDao.updateAllEventSaleState(reqMap);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * 项目场次信息同步-高并发模式
     *
     */
    @Transactional
    public void syncEventInfoForConcurrency(String project_id){
        JSONObject resObj = MZService.getProjectEvent(project_id);
        if(null !=resObj&&null !=resObj.get("event_v_o")){
            try{
                List<String> ids = new ArrayList<>();
                JSONArray eventArray = resObj.getJSONArray("event_v_o");
                for(int i=0;i<eventArray.size();i++){
                    JSONObject eventObj = eventArray.getJSONObject(i);
                    if(project_id.equals(eventObj.getString("project_id"))){
                        String event_id = eventObj.getString("event_id");   //麦座场次id
                        ids.add(event_id);
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
                        ticketEvent.setProjectId(project_id);
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
                        //保存场次信息
                        ticketEventDao.delete(ticketEvent);
                        ticketEventDao.insert(ticketEvent);

                        //场次下票档信息
                        if(null !=eventObj.get("price_list")){
                            try{
                                TicketEventPrice eventPrice = new TicketEventPrice();
                                eventPrice.setEventId(event_id);
                                ticketEventPriceDao.delete(eventPrice);

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
                                        ticketEventPrice.setPriceColor(price_color);
                                        ticketEventPrice.setMarginStockNum(margin_stock_num);
                                        ticketEventPriceDao.insert(ticketEventPrice);
                                    }
                                }
                            }catch (Exception e){
                                log.error("获取场次下票档信息失败:"+eventObj);
                                e.printStackTrace();
                            }
                        }

                        //当前场次取票点信息
                        if(null !=eventObj.get("exchange_place_list")){
                            try{
                                TicketExchangePlace exchangePlace = new TicketExchangePlace();
                                exchangePlace.setEventId(event_id);
                                ticketExchangePlaceDao.delete(exchangePlace);

                                JSONObject exchange_place_list = eventObj.getJSONObject("exchange_place_list");
                                if(null !=exchange_place_list.get("exchange_place")){
                                    JSONArray placeArray = exchange_place_list.getJSONArray("exchange_place");
                                    for(int m=0;m<placeArray.size();m++){
                                        JSONObject placeObj = placeArray.getJSONObject(m);
                                        String address = placeObj.getString("address");
                                        String longitude = placeObj.getString("longitude");
                                        String latitude = placeObj.getString("latitude");
                                        TicketExchangePlace ticketExchangePlace = new TicketExchangePlace();
                                        ticketExchangePlace.setEventId(event_id);
                                        ticketExchangePlace.setAddress(address);
                                        ticketExchangePlace.setPlaceId(address);
                                        ticketExchangePlace.setLongitude(longitude);
                                        ticketExchangePlace.setLatitude(latitude);
                                        //取票点类型
                                        String placeTypeList ="";
                                        if(null !=placeObj.get("place_type_list")){
                                            try{
                                                JSONObject place_type_list = placeObj.getJSONObject("place_type_list");
                                                if(null !=place_type_list.get("number")){

                                                    List<Integer> list = (List<Integer>) place_type_list.get("number");
                                                    for(int num:list){
                                                        placeTypeList += num+",";
                                                    }
                                                }
                                            }catch (Exception e){
                                                log.error("获取取票点类型list失败:"+eventObj);
                                                e.printStackTrace();
                                            }
                                        }
                                        ticketExchangePlace.setPlaceTypeList(placeTypeList);
                                        ticketExchangePlaceDao.insert(ticketExchangePlace);
                                        //工作时间list
                                        if(null !=placeObj.get("work_time_list")){
                                            try{
                                                TicketExchangePlaceWorktime exchangePlaceWorktime = new TicketExchangePlaceWorktime();
                                                exchangePlaceWorktime.setPlaceId(address);
                                                ticketExchangePlaceWorktimeDao.delete(exchangePlaceWorktime);

                                                JSONObject work_time_list = placeObj.getJSONObject("work_time_list");
                                                if(null !=work_time_list.get("work_time")){
                                                    JSONArray workTimeArray = work_time_list.getJSONArray("work_time");
                                                    for(int n=0;n<workTimeArray.size();n++){
                                                        JSONObject workTimeObj =workTimeArray.getJSONObject(n);
                                                        int work_date_type = workTimeObj.getInteger("work_date_type"); //工作时间类型；1=固定时间，2=自定义时间
                                                        String work_date_desc = workTimeObj.get("work_date_desc")==null?"":workTimeObj.getString("work_date_desc");  //自定义工作时间；当work_date_type=2时非空
                                                        String work_start_time = workTimeObj.get("work_start_time")==null?"":workTimeObj.getString("work_start_time");  //工作时间，开始时间 格式：HH:mm；当work_date_type=1时非空
                                                        String work_end_time = workTimeObj.get("work_end_time")==null?"":workTimeObj.getString("work_end_time");  //工作时间，结束时间 格式：HH:mm；当work_date_type=1时非空
                                                        TicketExchangePlaceWorktime ticketExchangePlaceWorktime =new TicketExchangePlaceWorktime();
                                                        ticketExchangePlaceWorktime.setPlaceId(address);
                                                        ticketExchangePlaceWorktime.setWorkDateType(work_date_type);
                                                        ticketExchangePlaceWorktime.setWorkDateDesc(work_date_desc);
                                                        ticketExchangePlaceWorktime.setWorkStartTime(work_start_time);
                                                        ticketExchangePlaceWorktime.setWorkEndTime(work_end_time);
                                                        //具体一周中周几工作
                                                        String workDateSets ="";
                                                        if(null !=workTimeObj.get("work_date_sets")){
                                                            try{
                                                                JSONObject work_date_sets = workTimeObj.getJSONObject("work_date_sets");
                                                                if(null !=work_date_sets.get("number")){

                                                                    List<Integer> list = (List<Integer>) work_date_sets.get("number");
                                                                    for(int num:list){
                                                                        workDateSets += num+",";
                                                                    }
                                                                }
                                                            }catch (Exception e){
                                                                log.error("获取一周中周几工作失败:"+eventObj);
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                        ticketExchangePlaceWorktime.setWorkDateSets(workDateSets);
                                                        ticketExchangePlaceWorktimeDao.insert(ticketExchangePlaceWorktime);
                                                    }

                                                }
                                            }catch (Exception e){

                                            }
                                        }
                                    }
                                }

                            }catch (Exception e){
                                log.error("获取场次取票点信息失败:"+eventObj);
                                e.printStackTrace();
                            }
                        }
                    }
                }
//                //将所有不在接口返回数据中的场次状态置为"不在售"，即不在移动端展示
//                if(ids.size()>0){
//                    HashMap<String,Object> reqMap = new HashMap<>();
//                    reqMap.put("ids",ids);
//                    reqMap.put("projectId",project_id);
//                    ticketEventDao.updateEventSaleState(reqMap);
//                }else{
//                    HashMap<String,Object> reqMap = new HashMap<>();
//                    reqMap.put("ids",ids);
//                    reqMap.put("projectId",project_id);
//                    ticketEventDao.updateAllEventSaleState(reqMap);
//                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void syncVenueInfo() {
        HashMap<String,Object> reqMap = new HashMap<>();
        List<TicketVenue> venues = ticketVenueDao.queryList(reqMap);
        List<String> venueIds = new ArrayList<>();
        for(TicketVenue ticketVenue:venues){
            venueIds.add(ticketVenue.getVenueId());
        }
        List<TicketEvent> events = ticketEventDao.queryList(reqMap);
        Set<String> eventVenueIds = new HashSet<String>();
        for(TicketEvent event:events){
            eventVenueIds.add(event.getVenueId());
        }
        for(String eventVenueId:eventVenueIds){
            if(!venueIds.contains(eventVenueId)){
                JSONObject venueObj = MZService.getVenueInfo(eventVenueId);
                if (null != venueObj) {
                    String venue_id = venueObj.getString("venue_id");
                    String venue_name = venueObj.getString("venue_name");
                    String venue_introduce = venueObj.getString("venue_introduce");
                    String address = venueObj.getString("address");
                    String longitude = venueObj.getString("longitude");
                    String latitude = venueObj.getString("latitude");
                    int province_code = venueObj.getInteger("province_code");
                    String province_name = venueObj.getString("province_name");
                    int city_code = venueObj.getInteger("city_code");
                    String city_name = venueObj.getString("city_name");
                    boolean big_venue_boolean = venueObj.getBoolean("big_venue_boolean");

                    TicketVenue ticketVenue = new TicketVenue();
                    ticketVenue.setVenueId(venue_id);
                    ticketVenue.setVenueName(venue_name);
                    ticketVenue.setVenueIntroduce(venue_introduce);
                    ticketVenue.setAddress(address);
                    ticketVenue.setLongitude(longitude);
                    ticketVenue.setLatitude(latitude);
                    ticketVenue.setProvinceCode(province_code);
                    ticketVenue.setProvinceName(province_name);
                    ticketVenue.setCityCode(city_code);
                    ticketVenue.setCityName(city_name);
                    ticketVenue.setBigVenueBoolean(big_venue_boolean?"1":"0");
                    ticketVenueDao.insert(ticketVenue);
                }
            }
        }


    }

    public boolean openConcurrencySwitch(){
        try{
            TConcurrencySwitch tConcurrencySwitch= tConcurrencySwitchDao.queryDetail(null);
            if(tConcurrencySwitch !=null &&tConcurrencySwitch.getSyncMzSwitch().equals("on")){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
}
