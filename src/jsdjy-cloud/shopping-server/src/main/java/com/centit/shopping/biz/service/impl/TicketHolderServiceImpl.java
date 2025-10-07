package com.centit.shopping.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.TicketHolderService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * <p>票夹<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-24
 **/
@Transactional
@Service
public class TicketHolderServiceImpl implements TicketHolderService {
    public static final Log log = LogFactory.getLog(TicketHolderService.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private TicketEventDao ticketEventDao;

    @Resource
    private TicketExchangePlaceDao ticketExchangePlaceDao;

    @Resource
    private TicketProjectWatchingNoticeDao ticketProjectWatchingNoticeDao;

    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;

    @Resource
    private ShoppingWriteoffDao shoppingWriteoffDao;
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    @Resource
    private ShoppingWriteoffRecordDao shoppingWriteoffRecordDao;

    @Resource
    private TicketProjectGuideDao ticketProjectGuideDao;

    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;

    @Resource
    private ShoppingCouponDao shoppingCouponDao;

    @Resource
    private ShoppingWriteoffCouponDao shoppingWriteoffCouponDao;

    @Resource
    private ShoppingCouponRecordDao shoppingCouponRecordDao;

    @Resource
    private TicketRedeemEventDao ticketRedeemEventDao;

    @Resource
    private TicketRedeemProjectDao ticketRedeemProjectDao;

    @Resource
    private TicketRedeemCodeDao ticketRedeemCodeDao;


    @Resource
    private TicketRedeemWriteoffDao ticketRedeemWriteoffDao;



    @Value("${order.orderState.hasDone}")
    private int orderStateDone;

    @Value("${order.orderState.anomalous}")
    private int orderStateAnomalous;

    @Value("${order.orderState.handAnomalous}")
    private int orderStateHandAnomalous;

//    /**
//     * 查询票夹演出票列表
//     */
//    @Override
//    public JSONObject queryTicketList(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
//            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
//            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
//            reqMap.put("startRow", (pageNo-1)*pageSize);
//            reqMap.put("pageSize", pageSize);
//
//            reqMap.put("orderType", Const.TICKET_ORDER_TYPE);
//            reqMap.put("orderStatus",orderStateDone);
//            reqMap.put("deleteStatus","0");
//            bizDataJson.put("total",shoppingOrderformDao.queryTotalCount(reqMap));
//            List<ShoppingOrderform> orderList = shoppingOrderformDao.queryList(reqMap);
//
//            JSONArray objArray = new JSONArray();
//            for(ShoppingOrderform shoppingOrderform:orderList){
//                JSONObject obj = new JSONObject();
//                //订单基本信息
//                obj.put("id",shoppingOrderform.getId());   //订单id
//                obj.put("orderId",shoppingOrderform.getOrderId());   //订单id
//
//                reqMap.clear();
//                reqMap.put("ofId",shoppingOrderform.getId());
//                reqMap.put("deleteStatus","0");
//                List<ShoppingGoodscart> cartGoods = CommonInit.staticShoppingGoodscartDao.queryList(reqMap);
//                ShoppingGoodscart shoppingGoodscart = cartGoods.get(0);
//                TicketEvent ticketEvent = new TicketEvent();
//                ticketEvent.setEventId(shoppingGoodscart.getGoodsId());
//                ticketEvent = ticketEventDao.queryDetail(ticketEvent);
//                obj.put("count",shoppingGoodscart.getCount());
//                obj.put("eventId",ticketEvent.getEventId());
//                obj.put("venueName",ticketEvent.getVenueName());
//                obj.put("eventStartTime",ticketEvent.getEventStartTime());
//                //根据场次信息查询项目信息
//                TicketProject ticketProject = new TicketProject();
//                ticketProject.setProjectId(ticketEvent.getProjectId());
//                ticketProject = CommonInit.staticTicketProjectDao.queryDetail(ticketProject);
//                obj.put("projectId",ticketProject.getProjectId());
//                obj.put("projectImgUrl",ticketProject.getProjectImgUrl());
//                obj.put("projectName",ticketProject.getProjectName());
//                objArray.add(obj);
//            }
//
//            bizDataJson.put("objList",objArray);
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
//
//    /**
//     * @Description 获取演出票凭证
//     **/
//    @Override
//    public JSONObject ticketDetail(String id) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "0";
//        String retMsg = "操作成功！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            //查询订单主体信息
//            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
//            shoppingOrderform.setOrderId(id);
//            shoppingOrderform = shoppingOrderformDao.queryDetailByOrderId(shoppingOrderform);
//
//            if(null ==shoppingOrderform){
//                shoppingOrderform = new ShoppingOrderform();
//                shoppingOrderform.setId(id);
//                shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
//            }
//            if(null !=shoppingOrderform){
//                String outOrderId = shoppingOrderform.getOutOrderId();
//                String mzUserId= CommonUtil.getMzUserId(shoppingOrderform.getUserId());
//                HashMap<String, Object> reqMap = new HashMap<>();
//                reqMap.put("ofId",shoppingOrderform.getId());
//                reqMap.put("deleteStatus","0");
//                List<ShoppingGoodscart> cartGoods = CommonInit.staticShoppingGoodscartDao.queryList(reqMap);
//                ShoppingGoodscart shoppingGoodscart = cartGoods.get(0);
//                TicketEvent ticketEvent = new TicketEvent();
//                ticketEvent.setEventId(shoppingGoodscart.getGoodsId());
//                ticketEvent = CommonInit.staticTicketEventDao.queryDetail(ticketEvent);
//                bizDataJson.put("count",shoppingGoodscart.getCount());
//                bizDataJson.put("eventId",ticketEvent.getEventId());
//                bizDataJson.put("venueName",ticketEvent.getVenueName());
//                bizDataJson.put("eventStartTime",ticketEvent.getEventStartTime());
//                //根据场次信息查询项目信息
//                TicketProject ticketProject = new TicketProject();
//                ticketProject.setProjectId(ticketEvent.getProjectId());
//                ticketProject = CommonInit.staticTicketProjectDao.queryDetail(ticketProject);
//                bizDataJson.put("projectId",ticketProject.getProjectId());
//                bizDataJson.put("projectImgUrl",ticketProject.getProjectImgUrl());
//                bizDataJson.put("projectName",ticketProject.getProjectName());
//                //查询该场次取票点信息
//                HashMap<String,Object> placeMap = new HashMap<>();
//                placeMap.put("eventId",shoppingGoodscart.getGoodsId());
//                List<TicketExchangePlace> places = ticketExchangePlaceDao.queryList(placeMap);
//                bizDataJson.put("places",places);
//                //查询观影须知
//                TicketProjectWatchingNotice ticketProjectWatchingNotice = new TicketProjectWatchingNotice();
//                ticketProjectWatchingNotice.setProjectId(ticketProject.getProjectId());
//                ticketProjectWatchingNotice = ticketProjectWatchingNoticeDao.queryDetail(ticketProjectWatchingNotice);
////            bizDataJson.put("showLengthTips", ticketProjectWatchingNotice.getShowLengthTips());   //演出时长
//                bizDataJson.put("watchingNotice", ticketProjectWatchingNotice);   //观演须知
//
//                JSONObject dataObj = MZService.getTicketList(mzUserId,outOrderId);
//                if(null !=dataObj){
//                    bizDataJson.put("ticketList",dataObj.get("eticket_v_o"));
//                }else{
//                    retCode = "1";
//                    retMsg = "获取麦座订单票凭证失败，请稍后再试！";
//                }
//                //电子节目单
//                TicketProjectGuide ticketProjectGuide = new TicketProjectGuide();
//                ticketProjectGuide.setProjectId(ticketProject.getProjectId());
//                ticketProjectGuide = ticketProjectGuideDao.queryDetail(ticketProjectGuide);
//                bizDataJson.put("guidePhoto",ticketProjectGuide==null?null:ticketProjectGuide.getPhotoId());
//            }else{
//                retCode = "1";
//                retMsg = "获取订单信息失败！";
//            }
//
//
//        } catch (Exception e) {
//            retCode = "1";
//            retMsg = "操作失败！";
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    /**
     * 查询票夹演出票列表
     */
    @Override
    public JSONObject queryTicketList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");

            String userId = reqJson.getString("userId");   //userId
            String mzUserId = CommonUtil.getMzUserId(userId);
            JSONArray resArray =MZService.getorderList(mzUserId);
//            JSONArray dataArray = new JSONArray();
            List<JSONObject> dataArray= new ArrayList<>();
            for(int i=0;i<resArray.size();i++){
                JSONObject resObj =  resArray.getJSONObject(i);
                int order_state = resObj.getInteger("order_state");   //订单状态；1=订单未完成，2=订单已完成，3=订单已关闭
                int order_pay_state = resObj.getInteger("order_pay_state");   //订单支付状态；1=已支付，2=待支付
                int refund_order_state = resObj.getInteger("refund_order_state");   //订单退单状态；退单状态：1=未退单，2=部分退单，3=已退单，4=退单申请中
                if(order_state==2&&order_pay_state==1&&refund_order_state==1){
                    dataArray.add(resObj);
                }
            }
            bizDataJson.put("total",dataArray.size());
            dataArray =  dataArray.subList((pageNo-1)*pageSize,dataArray.size()>pageNo*pageSize?pageNo*pageSize:dataArray.size());

            JSONArray objArray = new JSONArray();
            for(int i=0;i<dataArray.size();i++){
                try {
                    JSONObject dataObj = dataArray.get(i);
                    JSONObject obj = new JSONObject();
                    //订单基本信息
                    obj.put("id",dataObj.getString("mz_order_id"));   //订单id
                    obj.put("orderId",dataObj.getString("mz_order_id"));   //订单id
                    obj.put("count",dataObj.getInteger("order_goods_count"));

//                    JSONObject eventObj = dataObj.getJSONObject("order_event_list").getJSONArray("order_event_v_o").getJSONObject(0);

                    JSONArray eventList = dataObj.getJSONObject("order_event_list").getJSONArray("order_event_v_o");
                    HashSet<String> projectIdSet = new HashSet<>();
                    HashSet<String> eventIdSet = new HashSet<>();
                    String projectNameStr = "";
                    String eventStartTimeStr = "";
                    String venueNameeStr = "";
                    for(int m=0;m<eventList.size();m++){
                        JSONObject eventObj = eventList.getJSONObject(m);
                        String projectId = eventObj.getString("project_id");
                        String eventId = eventObj.getString("event_name");
                        String projectName = eventObj.getString("project_name");
                        String venueName = eventObj.getString("venue_name");
                        String eventStartTime = eventObj.getString("event_start_time");
                        if(!projectIdSet.contains(projectId)){
                            projectIdSet.add(projectId);
                            projectNameStr = projectNameStr+projectName+"、";
                        }

                        if(!eventIdSet.contains(eventId)){
                            eventIdSet.add(eventId);
                            eventStartTimeStr =eventStartTimeStr+eventStartTime+" ";
                            venueNameeStr=venueNameeStr+venueName+" ";
                        }

                    }
                    if(projectIdSet.size()>1){
//                        obj.put("eventId",eventObj.getString("event_name"));
                        obj.put("venueName",venueNameeStr);
                        obj.put("eventStartTime",eventStartTimeStr);

//                        obj.put("projectId",eventObj.getString("project_id"));
//                        obj.put("projectImgUrl",eventObj.getString("project_img_url"));
                        obj.put("projectName",projectNameStr);
                    }else{
                        JSONObject eventObj =dataObj.getJSONObject("order_event_list").getJSONArray("order_event_v_o").getJSONObject(0);
//                        obj.put("eventId",eventObj.getString("event_name"));
                        obj.put("venueName",venueNameeStr);
                        obj.put("eventStartTime",eventStartTimeStr);

                        obj.put("projectId",eventObj.getString("project_id"));
                        obj.put("projectImgUrl",eventObj.getString("project_img_url"));
                        obj.put("projectName",eventObj.getString("project_name"));
                    }


                    objArray.add(obj);
                }catch (Exception e) {
                    log.error(e);
                }
            }

            bizDataJson.put("objList",objArray);
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
     * @Description 获取演出票凭证
     **/
    @Override
    public JSONObject ticketDetail(JSONObject reqJson,String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //查询订单主体信息
            String outOrderId = id;
            String userId = reqJson.getString("userId");   //userId
            String mzUserId = CommonUtil.getMzUserId(userId);
            JSONObject dataObj = MZService.getTicketList(mzUserId,outOrderId);
            if(null==dataObj){
                retCode = "1";
                retMsg = "当前订单票凭证信息无法在APP查看，请通过其它渠道查询!";
            }else{
                JSONArray ticketArray = dataObj.getJSONArray("eticket_v_o");
                for(int i=0;i<ticketArray.size();i++){
                    JSONObject ticketObj = ticketArray.getJSONObject(i);
                    String project_id = ticketObj.getString("project_id");
                    String event_id = ticketObj.getString("event_id");
                    //查询项目信息
                    TicketProject ticketProject = new TicketProject();
                    ticketProject.setProjectId(project_id);
                    ticketProject = CommonInit.staticTicketProjectDao.queryDetail(ticketProject);
                    if(null !=ticketProject){
                        //项目海报
                        ticketObj.put("projectImgUrl",ticketProject.getProjectImgUrl());

                        //查询观影须知
                        TicketProjectWatchingNotice ticketProjectWatchingNotice = new TicketProjectWatchingNotice();
                        ticketProjectWatchingNotice.setProjectId(project_id);
                        ticketProjectWatchingNotice = ticketProjectWatchingNoticeDao.queryDetail(ticketProjectWatchingNotice);
                        ticketObj.put("watchingNotice", ticketProjectWatchingNotice);   //观演须知

                        //电子节目单
//                        TicketProjectGuide ticketProjectGuide = new TicketProjectGuide();
//                        ticketProjectGuide.setProjectId(project_id);
//                        ticketProjectGuide = ticketProjectGuideDao.queryDetail(ticketProjectGuide);
//                        ticketObj.put("guidePhoto",ticketProjectGuide==null?null:ticketProjectGuide.getPhotoId());
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("projectId",project_id);
                        ticketObj.put("guidePhotos",ticketProjectGuideDao.queryList(reqMap));
                    }

                    //查询该场次取票点信息
                    HashMap<String,Object> placeMap = new HashMap<>();
                    placeMap.put("eventId",event_id);
                    List<TicketExchangePlace> places = ticketExchangePlaceDao.queryList(placeMap);
                    ticketObj.put("places",places);

                }
                bizDataJson.put("objList",ticketArray);
                retCode = "0";
                retMsg = "操作成功！";
            }

        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询待核销商品列表
     */
    @Override
    public JSONObject queryGoodsList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            List<Integer> cartType = new ArrayList<>();
            cartType.add(Const.SHOPPING_CUL_CART_TYPE);
            cartType.add(Const.SHOPPING_INT_CART_TYPE);
            reqMap.put("cartType",cartType);
            reqMap.put("orderStatus",orderStateDone);
            reqMap.put("deleteStatus","0");

            bizDataJson.put("total",shoppingGoodscartDao.queryDoneGoodsListCount(reqMap));
            List<HashMap<String, Object>> cartList = shoppingGoodscartDao.queryDoneGoodsList(reqMap);
            JSONArray objArray = new JSONArray();
            for(HashMap<String, Object> goodsMap:cartList){
                JSONObject obj = new JSONObject();

                obj.put("gcId", goodsMap.get("gc_id"));
                obj.put("goodsId", goodsMap.get("goods_id"));
                obj.put("goodsName", goodsMap.get("goods_name"));
                obj.put("photoId", goodsMap.get("photo_id"));  //商品图id
                obj.put("count", goodsMap.get("count"));  //数量
                obj.put("addTime", goodsMap.get("addTime"));  //下单时间
                objArray.add(obj);
            }

            bizDataJson.put("objList",objArray);
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
     * @Description 获取待核销商品详情
     **/
    @Override
    public JSONObject goodsWriteOffDetail(String gcId) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //查询订单主体信息
            ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
            shoppingWriteoff.setGcId(gcId);
            shoppingWriteoff = shoppingWriteoffDao.queryDetail(shoppingWriteoff);

            if(null !=shoppingWriteoff){
                ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
                shoppingGoodscart.setId(gcId);
                shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);

                ShoppingGoods shoppingGoods = new ShoppingGoods();
                shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                //查询商品主体信息
                shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

                bizDataJson.put("goodsId", shoppingGoods.getId());
                bizDataJson.put("goodsName", shoppingGoods.getGoodsName());
                bizDataJson.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                bizDataJson.put("goodsNotice", shoppingGoods.getGoodsNotice());  //商品须知
                bizDataJson.put("returnExplain", shoppingGoods.getReturnExplain());  //退换说明

                //商品规格信息
                bizDataJson.put("specInfo", shoppingGoodscart.getSpecInfo());  //商品规格

                bizDataJson.put("offStatus", shoppingWriteoff.getOffStatus());  //核销状态
                bizDataJson.put("goodsCount", shoppingWriteoff.getGoodsCount());  //可核销商品总数
                bizDataJson.put("offCount", shoppingWriteoff.getOffCount());  //已核销数量
                bizDataJson.put("offCode", shoppingWriteoff.getOffCode());  //核销码
            }else{
                retCode = "1";
                retMsg = "获取订单信息失败！";
            }

        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询票夹艺教活动列表
     */
    @Override
    public JSONObject queryActivityList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            reqMap.put("cartType",Const.SHOPPING_ACT_CART_TYPE);  //艺教活动订单类型
            reqMap.put("orderStatus",orderStateDone);
            reqMap.put("deleteStatus","0");

            bizDataJson.put("total",shoppingGoodscartDao.queryDoneActivityListCount(reqMap));
            List<HashMap<String, Object>> cartList = shoppingGoodscartDao.queryDoneActivityList(reqMap);
            JSONArray objArray = new JSONArray();
            for(HashMap<String, Object> goodsMap:cartList){
                JSONObject obj = new JSONObject();
                obj.put("gcId", goodsMap.get("gc_id"));
                obj.put("activityId", goodsMap.get("goods_id"));
                obj.put("activityName", goodsMap.get("activity_name"));
                obj.put("photoId", goodsMap.get("photo_id"));  //商品图id
                obj.put("count", goodsMap.get("count"));  //数量
                obj.put("addTime", goodsMap.get("addTime"));  //下单时间
                objArray.add(obj);
            }

            bizDataJson.put("objList",objArray);
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
     * @Description 获取艺教活动核销详情
     **/
    @Override
    public JSONObject actWriteOffDetail(String gcId) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //查询订单主体信息
            ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
            shoppingWriteoff.setGcId(gcId);
            shoppingWriteoff = shoppingWriteoffDao.queryDetail(shoppingWriteoff);

            if(null !=shoppingWriteoff){
                ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
                shoppingGoodscart.setId(gcId);
                shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);

                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                //查询商品主体信息
                shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);

                bizDataJson.put("activityId", shoppingArtactivity.getId());
                bizDataJson.put("activityName", shoppingArtactivity.getActivityName());
                bizDataJson.put("photoId", shoppingArtactivity.getMainPhotoId());  //商品图id
                bizDataJson.put("activityTime", shoppingArtactivity.getActivityName());  //活动时间
                bizDataJson.put("activityLocation", shoppingArtactivity.getActivityLocation());  //活动地点
                bizDataJson.put("activityNotice", shoppingArtactivity.getActivityNotice());  //活动须知

                //规格信息
                bizDataJson.put("specInfo", shoppingGoodscart.getSpecInfo());  //规格


                bizDataJson.put("offStatus", shoppingWriteoff.getOffStatus());  //核销状态
                bizDataJson.put("goodsCount", shoppingWriteoff.getGoodsCount());  //可核销商品总数
                bizDataJson.put("offCount", shoppingWriteoff.getOffCount());  //已核销数量
                bizDataJson.put("offCode", shoppingWriteoff.getOffCode());  //核销码
            }else{
                retCode = "1";
                retMsg = "获取订单信息失败！";
            }

        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询票夹爱艺计划列表
     */
    @Override
    public JSONObject queryPlanList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            reqMap.put("cartType",Const.SHOPPING_PLAN_CART_TYPE);  //爱艺计划订单类型
            reqMap.put("orderStatus",orderStateDone);
            reqMap.put("deleteStatus","0");

            bizDataJson.put("total",shoppingGoodscartDao.queryDonePlanListCount(reqMap));
            List<HashMap<String, Object>> cartList = shoppingGoodscartDao.queryDonePlanList(reqMap);
            JSONArray objArray = new JSONArray();
            for(HashMap<String, Object> goodsMap:cartList){
                JSONObject obj = new JSONObject();
                obj.put("gcId", goodsMap.get("gc_id"));
                obj.put("activityId", goodsMap.get("goods_id"));
                obj.put("activityName", goodsMap.get("activity_name"));
                obj.put("photoId", goodsMap.get("photo_id"));  //商品图id
                obj.put("count", goodsMap.get("count"));  //数量
                obj.put("addTime", goodsMap.get("addTime"));  //下单时间
                objArray.add(obj);
            }

            bizDataJson.put("objList",objArray);
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
     * @Description 获取爱艺计划核销详情
     **/
    @Override
    public JSONObject planWriteOffDetail(String gcId) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //查询订单主体信息
            ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
            shoppingWriteoff.setGcId(gcId);
            shoppingWriteoff = shoppingWriteoffDao.queryDetail(shoppingWriteoff);

            if(null !=shoppingWriteoff){
                ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
                shoppingGoodscart.setId(gcId);
                shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);


                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                //查询商品主体信息
                shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);

                bizDataJson.put("activityId", shoppingArtplan.getId());
                bizDataJson.put("activityName", shoppingArtplan.getActivityName());
                bizDataJson.put("photoId", shoppingArtplan.getMainPhotoId());  //商品图id
                bizDataJson.put("activityTime", shoppingArtplan.getActivityName());  //活动时间
                bizDataJson.put("activityLocation", shoppingArtplan.getActivityLocation());  //活动地点
                bizDataJson.put("activityNotice", shoppingArtplan.getActivityNotice());  //活动须知

                //规格信息
                bizDataJson.put("specInfo", shoppingGoodscart.getSpecInfo());  //规格

                bizDataJson.put("offStatus", shoppingWriteoff.getOffStatus());  //核销状态
                bizDataJson.put("goodsCount", shoppingWriteoff.getGoodsCount());  //可核销商品总数
                bizDataJson.put("offCount", shoppingWriteoff.getOffCount());  //已核销数量
                bizDataJson.put("offCode", shoppingWriteoff.getOffCode());  //核销码
            }else{
                retCode = "1";
                retMsg = "获取订单信息失败！";
            }

        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 获取待核销商品详情
     **/
    @Override
    public JSONObject toWriteOffDetail(JSONObject reqJson,String offCode) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            if(offCode.startsWith("RE_")){   //演出兑换核销

                TicketRedeemWriteoff ticketRedeemWriteoff = new TicketRedeemWriteoff();
                ticketRedeemWriteoff.setOffCode(offCode);
                ticketRedeemWriteoff = ticketRedeemWriteoffDao.queryDetail(ticketRedeemWriteoff);
                if(null !=ticketRedeemWriteoff){
                    String expTime =ticketRedeemWriteoff.getExpTime();
                    SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if(StringUtil.compareMillisecond(expTime,sf)>0){
                        String code = ticketRedeemWriteoff.getCode();
                        TicketRedeemCode ticketRedeemCode = new TicketRedeemCode();
                        ticketRedeemCode.setCode(code);
                        ticketRedeemCode= ticketRedeemCodeDao.queryDetail(ticketRedeemCode);

                        String projectId = ticketRedeemCode.getProjectId();
                        TicketRedeemProject project = new TicketRedeemProject();
                        project.setProjectId(projectId);
                        project = ticketRedeemProjectDao.queryDetail(project);
                        if(project.getWriteOffCount().equals(userId)){
                            JSONObject redeemProjectInfo = new JSONObject();
                            redeemProjectInfo.put("projectId",ticketRedeemCode.getProjectId());   //项目ID
                            redeemProjectInfo.put("projectName",ticketRedeemCode.getProjectName());   //项目名称
                            redeemProjectInfo.put("projectImgUrl",ticketRedeemCode.getProjectImgUrl());   //项目海报图
                            redeemProjectInfo.put("eventId",ticketRedeemCode.getEventId());   //场次id
                            redeemProjectInfo.put("eventStartTime",ticketRedeemCode.getEventStartTime());   //开场时间
                            redeemProjectInfo.put("venueName",ticketRedeemCode.getVenueName());   //场馆名称
                            redeemProjectInfo.put("watchingUser",ticketRedeemCode.getWatchingUser());   //观看人姓名
                            redeemProjectInfo.put("watchingMobile",ticketRedeemCode.getWatchingMobile());   //观看人手机号
                            redeemProjectInfo.put("watchingCard",ticketRedeemCode.getWatchingCard());   //观看人证件号
                            redeemProjectInfo.put("code",ticketRedeemCode.getCode());  //兑换码
                            redeemProjectInfo.put("exchangeMobile",ticketRedeemCode.getExchangeMobile());   //用户手机号
                            redeemProjectInfo.put("companyName",ticketRedeemCode.getCompanyName());   //单位
                            redeemProjectInfo.put("exchangeTime",ticketRedeemCode.getExchangeTime());   //兑换时间

                            bizDataJson.put("redeemProjectInfo", redeemProjectInfo);
                            bizDataJson.put("cartType", 11);
                            ShoppingUser user = CommonUtil.getShoppingUserByUserId(ticketRedeemCode.getExchangeUser());
                            bizDataJson.put("userName", user.getNickName());  //用户名
                            bizDataJson.put("offStatus", StringUtil.isNotNull(ticketRedeemWriteoff.getOffCount())?1:0);  //核销状态
                            bizDataJson.put("offCode", ticketRedeemWriteoff.getOffCode());  //核销码
                        }else{
                            retCode = "1";
                            retMsg = "您没有权限核销该演出！";
                        }
                    }else{
                        retCode = "1";
                        retMsg = "该二维码已失效，请刷新页面并重试！";
                    }
                }else{
                    retCode = "1";
                    retMsg = "未获取到演出兑换信息，请刷新二维码或确认展示的二维码是否正确！";
                }
            }else if(offCode.startsWith("CP_")){   //优惠券核销
                ShoppingWriteoffCoupon shoppingWriteoffCoupon = new ShoppingWriteoffCoupon();
                shoppingWriteoffCoupon.setOffCode(offCode);
                shoppingWriteoffCoupon = shoppingWriteoffCouponDao.queryDetail(shoppingWriteoffCoupon);
                if(null !=shoppingWriteoffCoupon){
                    ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                    shoppingCoupon.setRight_No(shoppingWriteoffCoupon.getRightNo());
                    shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
                    if(shoppingCoupon.getWriteOffCount()!=null&&shoppingCoupon.getWriteOffCount().equals(userId)){
                        bizDataJson.put("couponInfo", shoppingCoupon);
                        bizDataJson.put("cartType", 10);
                        ShoppingUser user = CommonUtil.getShoppingUserByUserId(shoppingWriteoffCoupon.getUserId());
                        bizDataJson.put("userName", user.getNickName());  //用户名
                        bizDataJson.put("offStatus", shoppingWriteoffCoupon.getOffStatus());  //核销状态
                        bizDataJson.put("offCode", shoppingWriteoffCoupon.getOffCode());  //核销码
                    }else{
                        retCode = "1";
                        retMsg = "您没有权限核销该商品！";
                    }
                }else{
                    retCode = "1";
                    retMsg = "获取优惠券信息失败！";
                }
            }else{
                //查询订单主体信息
                ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                shoppingWriteoff.setOffCode(offCode);
                shoppingWriteoff = shoppingWriteoffDao.queryDetail(shoppingWriteoff);

                if(null !=shoppingWriteoff){
                    ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
                    shoppingGoodscart.setId(shoppingWriteoff.getGcId());
                    shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);
                    bizDataJson.put("cartType", shoppingGoodscart.getCartType());


                    //商品
                    if(shoppingGoodscart.getCartType()==1||shoppingGoodscart.getCartType()==2){
                        ShoppingGoods shoppingGoods = new ShoppingGoods();
                        shoppingGoods.setId(shoppingGoodscart.getGoodsId());//查询商品主体信息
                        shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                        JSONObject goodsInfo = new JSONObject();
                        if(shoppingGoods.getWriteOffCount()!=null&&shoppingGoods.getWriteOffCount().equals(userId)){
                            goodsInfo.put("goodsId", shoppingGoods.getId());
                            goodsInfo.put("goodsName", shoppingGoods.getGoodsName());
                            goodsInfo.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                            goodsInfo.put("goodsNotice", shoppingGoods.getGoodsNotice());  //商品须知
                            goodsInfo.put("returnExplain", shoppingGoods.getReturnExplain());  //退换说明

                            bizDataJson.put("goodsInfo", goodsInfo);

                            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
                            shoppingOrderform.setId(shoppingGoodscart.getOfId());
                            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);

                            bizDataJson.put("orderId", shoppingOrderform.getOrderId());  //订单号
                            bizDataJson.put("addTime", shoppingOrderform.getAddTime());  //下单时间
                            bizDataJson.put("payTime", shoppingOrderform.getPayTime());  //支付时间
                            bizDataJson.put("userName", shoppingOrderform.getUserInfo().get("userName"));  //用户名

                            bizDataJson.put("offStatus", shoppingWriteoff.getOffStatus());  //核销状态
                            bizDataJson.put("goodsCount", shoppingWriteoff.getGoodsCount());  //可核销商品总数
                            bizDataJson.put("offCount", shoppingWriteoff.getOffCount());  //已核销数量
                            bizDataJson.put("offCode", shoppingWriteoff.getOffCode());  //核销码
                        }else{
                            retCode = "1";
                            retMsg = "您没有权限核销该商品！";
                        }
                    }else if(shoppingGoodscart.getCartType()==3){
                        ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                        shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                        //活动主体信息
                        shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                        if(shoppingArtactivity.getWriteOffCount()!=null&&shoppingArtactivity.getWriteOffCount().equals(userId)){
                            JSONObject activityInfo = new JSONObject();
                            activityInfo.put("activityId", shoppingArtactivity.getId());
                            activityInfo.put("activityName", shoppingArtactivity.getActivityName());
                            activityInfo.put("photoId", shoppingArtactivity.getMainPhotoId());  //主图id
                            String activityTime = shoppingArtactivity.getActivityTime();
                            if(activityTime.length()<16){  //活动时间长度不足16的，补全空格
                                for(int i=activityTime.length();i<=16;i++){
                                    activityTime = activityTime+" ";
                                }
                            }
                            activityInfo.put("activityTime", activityTime);  //活动时间
                            activityInfo.put("activityLocation", shoppingArtactivity.getActivityLocation());  //活动地点
                            activityInfo.put("activityNotice", shoppingArtactivity.getActivityNotice());  //活动须知

                            bizDataJson.put("activityInfo", activityInfo);

                            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
                            shoppingOrderform.setId(shoppingGoodscart.getOfId());
                            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);

                            bizDataJson.put("orderId", shoppingOrderform.getOrderId());  //订单号
                            bizDataJson.put("addTime", shoppingOrderform.getAddTime());  //下单时间
                            bizDataJson.put("payTime", shoppingOrderform.getPayTime());  //支付时间
                            bizDataJson.put("userName", shoppingOrderform.getUserInfo().get("userName"));  //用户名

                            bizDataJson.put("offStatus", shoppingWriteoff.getOffStatus());  //核销状态
                            bizDataJson.put("goodsCount", shoppingWriteoff.getGoodsCount());  //可核销商品总数
                            bizDataJson.put("offCount", shoppingWriteoff.getOffCount());  //已核销数量
                            bizDataJson.put("offCode", shoppingWriteoff.getOffCode());  //核销码
                        }else{
                            retCode = "1";
                            retMsg = "您没有权限核销该商品！";
                        }
                    }else if(shoppingGoodscart.getCartType()==9){
                        ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                        shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                        //爱艺计划主体信息
                        shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                        if(shoppingArtplan.getWriteOffCount()!=null&&shoppingArtplan.getWriteOffCount().equals(userId)){
                            JSONObject activityInfo = new JSONObject();
                            activityInfo.put("activityId", shoppingArtplan.getId());
                            activityInfo.put("activityName", shoppingArtplan.getActivityName());
                            activityInfo.put("photoId", shoppingArtplan.getMainPhotoId());  //主图id
                            String activityTime = shoppingArtplan.getActivityTime();
                            if(activityTime.length()<16){  //活动时间长度不足16的，补全空格
                                for(int i=activityTime.length();i<=16;i++){
                                    activityTime = activityTime+" ";
                                }
                            }
                            activityInfo.put("activityTime", activityTime);  //活动时间
                            activityInfo.put("activityLocation", shoppingArtplan.getActivityLocation());  //活动地点
                            activityInfo.put("activityNotice", shoppingArtplan.getActivityNotice());  //活动须知

                            bizDataJson.put("activityInfo", activityInfo);

                            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
                            shoppingOrderform.setId(shoppingGoodscart.getOfId());
                            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);

                            bizDataJson.put("orderId", shoppingOrderform.getOrderId());  //订单号
                            bizDataJson.put("addTime", shoppingOrderform.getAddTime());  //下单时间
                            bizDataJson.put("payTime", shoppingOrderform.getPayTime());  //支付时间
                            bizDataJson.put("userName", shoppingOrderform.getUserInfo().get("userName"));  //用户名

                            bizDataJson.put("offStatus", shoppingWriteoff.getOffStatus());  //核销状态
                            bizDataJson.put("goodsCount", shoppingWriteoff.getGoodsCount());  //可核销商品总数
                            bizDataJson.put("offCount", shoppingWriteoff.getOffCount());  //已核销数量
                            bizDataJson.put("offCode", shoppingWriteoff.getOffCode());  //核销码
                        }else{
                            retCode = "1";
                            retMsg = "您没有权限核销该商品！";
                        }


                    }


                }else{
                    retCode = "1";
                    retMsg = "获取订单信息失败！";
                }
            }

        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 核销
     **/
    @Override
    public JSONObject writeOff(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String offAccount = reqJson.getString("offAccount");  //核销账号
            //查询核销信息
            String offCode = reqJson.getString("offCode");  //核销码
            if(offCode.startsWith("RE_")){   //优惠券核销
                TicketRedeemWriteoff ticketRedeemWriteoff = new TicketRedeemWriteoff();
                ticketRedeemWriteoff.setOffCode(offCode);
                ticketRedeemWriteoff = ticketRedeemWriteoffDao.queryDetail(ticketRedeemWriteoff);
                if(null !=ticketRedeemWriteoff){
                    String expTime =ticketRedeemWriteoff.getExpTime();
                    SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if(StringUtil.compareMillisecond(expTime,sf)>0){
                        if(null !=ticketRedeemWriteoff.getOffCount()){
                            retCode = "1";
                            retMsg = "该演出兑换已核销完成，请勿重复核销！";
                        }else{
                            ticketRedeemWriteoff.setOffCount(offAccount);
                            ticketRedeemWriteoff.setOffTime(StringUtil.nowTimeString());
                            ticketRedeemWriteoffDao.update(ticketRedeemWriteoff);

                            String code = ticketRedeemWriteoff.getCode();
                            TicketRedeemCode ticketRedeemCode = new TicketRedeemCode();
                            ticketRedeemCode.setCode(code);
                            ticketRedeemCode.setWriteoffTime(StringUtil.nowTimeString());
                            ticketRedeemCodeDao.update(ticketRedeemCode);
                        }
                    }else{
                        retCode = "1";
                        retMsg = "核销失败！该二维码已失效，请刷新并重试！";
                    }
                }else{
                    retCode = "1";
                    retMsg = "核销失败！未获取到演出兑换信息，请刷新二维码并重试！";
                }

            }else if(offCode.startsWith("CP_")){   //优惠券核销
                ShoppingWriteoffCoupon shoppingWriteoffCoupon = new ShoppingWriteoffCoupon();
                shoppingWriteoffCoupon.setOffCode(offCode);
                shoppingWriteoffCoupon = shoppingWriteoffCouponDao.queryDetail(shoppingWriteoffCoupon);
                if(null !=shoppingWriteoffCoupon){
                    if(shoppingWriteoffCoupon.getOffStatus()==1){
                        retCode = "1";
                        retMsg = "该优惠券已核销完成，请勿重复核销！";
                    }else{
                        //向CRM核销券
                        JSONObject resObj = CRMService.writeoffCoupon(shoppingWriteoffCoupon.getRightId());
                        if(resObj.get("result").equals("ok")){
                            shoppingWriteoffCoupon.setOffAccount(offAccount);
                            shoppingWriteoffCoupon.setOffTime(StringUtil.nowTimeString());
                            shoppingWriteoffCoupon.setOffStatus(1);
                            shoppingWriteoffCouponDao.update(shoppingWriteoffCoupon);

                            //本地保存优惠券消费记录
                            ShoppingCouponRecord shoppingCouponRecord = new ShoppingCouponRecord();
                            shoppingCouponRecord.setUserId(shoppingWriteoffCoupon.getUserId());
                            shoppingCouponRecord.setRightNo(shoppingWriteoffCoupon.getRightNo());
                            shoppingCouponRecord.setCouponId(shoppingWriteoffCoupon.getRightId());
//                            shoppingCouponRecord.setOfId(orderform.getId());
                            shoppingCouponRecordDao.insert(shoppingCouponRecord);
                        }else{
                            retCode = "1";
                            retMsg = "该优惠券核销失败，请联系开发人员处理！";
                        }
                    }
                }else{
                    retCode = "1";
                    retMsg = "当前核销码有误，请联系客服人员处理！";
                }
            }else{
                ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                shoppingWriteoff.setOffCode(offCode);
                shoppingWriteoff = shoppingWriteoffDao.queryDetail(shoppingWriteoff);

                if(null !=shoppingWriteoff){
                    if(shoppingWriteoff.getOffCount()== shoppingWriteoff.getGoodsCount()){
                        retCode = "1";
                        retMsg = "该商品已核销完成，请勿重复核销！";
                    }else{
                        int offCount = shoppingWriteoff.getGoodsCount();
                        shoppingWriteoff.setOffCount(offCount);
                        shoppingWriteoff.setOffStatus(1);

                        shoppingWriteoffDao.update(shoppingWriteoff);
                        ShoppingWriteoffRecord shoppingWriteoffRecord = new ShoppingWriteoffRecord();
                        shoppingWriteoffRecord.setGcId(shoppingWriteoff.getGcId());
                        shoppingWriteoffRecord.setOffAccount(offAccount);
                        shoppingWriteoffRecordDao.insert(shoppingWriteoffRecord);

                        //更新订单状态
                        String gcId = shoppingWriteoff.getGcId();
                        ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
                        shoppingGoodscart.setId(gcId);
                        shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);
                        if(null !=shoppingGoodscart){
                            String ofId = shoppingGoodscart.getOfId();
                            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
                            shoppingOrderform.setId(ofId);
                            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
                            if(!shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)){   //非合并支付订单，直接将核销成功的订单状态置为已完成
                                shoppingOrderform.setOrderStatus(50);
                                shoppingOrderformDao.update(shoppingOrderform);
                            }else{
                                HashMap<String, Object> reqMap = new HashMap<>();
                                reqMap.put("ofId",ofId);
                                reqMap.put("deleteStatus","0");
                                List<ShoppingGoodscart> cartList = shoppingGoodscartDao.queryList(reqMap);
                                boolean isDone = true;
                                for(ShoppingGoodscart cart:cartList){
                                    //如果订单内有需要快递的商品，则整个订单的状态已快递商品的状态为准
                                    if(cart.getTransport().equals("快递")){
                                        isDone = false;
                                        break;
                                    }
                                    else if(cart.getTransport().equals("自提")&&!cart.getId().equals(gcId)){  //查询订单内的其它自提物品是否已经核销
                                        ShoppingWriteoff writeoff = new ShoppingWriteoff();
                                        writeoff.setGcId(cart.getId());
                                        writeoff =shoppingWriteoffDao.queryDetail(writeoff);
                                        //如果订单内还有其他未核销商品，则不能将订单状态置为已完成
                                        if(null ==writeoff||writeoff.getOffStatus()!=1){
                                            isDone = false;
                                            break;
                                        }

                                    }
                                }
                                if(isDone){
                                    shoppingOrderform.setOrderStatus(50);
                                    shoppingOrderformDao.update(shoppingOrderform);
                                }
                            }
                        }

                    }
                }else{
                    retCode = "1";
                    retMsg = "当前核销码有误，请联系客服人员处理！";
                }
            }


        } catch (Exception e) {
            retCode = "1";
            retMsg = "当前核销码有误，请联系客服人员处理！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询我的可核销商品列表
     */
    @Override
    public JSONObject queryMyWriteOffGoodsList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            List<Integer> cartType = new ArrayList<>();
            reqMap.put("deleteStatus","0");
            reqMap.put("goodsStatus", "0");  //只取已上架的商品

            bizDataJson.put("total",shoppingGoodsDao.queryTotalCount(reqMap));
            List<ShoppingGoods> goodsList = shoppingGoodsDao.queryList(reqMap);
            JSONArray objArray = new JSONArray();
            for(ShoppingGoods shoppingGoods:goodsList){
                JSONObject obj = new JSONObject();

                obj.put("goodsId",shoppingGoods.getId());
                obj.put("photoId",shoppingGoods.getGoodsMainPhotoId());
                obj.put("goodsName",shoppingGoods.getGoodsName());
                objArray.add(obj);
            }

            bizDataJson.put("objList",objArray);
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
     * 查询我的可核销商品详情
     */
    @Override
    public JSONObject queryMyWriteOffGoodsDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            bizDataJson.put("total",shoppingOrderformDao.queryGoodsOrderTotalCount(reqMap));
            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryGoodsOrderList(reqMap);
            List<OrderDetail> objList = new ArrayList<>();
            for(ShoppingOrderform shoppingOrderform:orderforms){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOfId(shoppingOrderform.getId());
                orderDetail.setOrderId(shoppingOrderform.getOrderId());
                orderDetail.setAddTime(shoppingOrderform.getAddTime());
                orderDetail.setOrderType(shoppingOrderform.getOrderType());
                orderDetail.setOrderStatus(shoppingOrderform.getOrderStatus());
                orderDetail.setTotalPrice(shoppingOrderform.getTotalPrice());
                orderDetail.setShipPrice(shoppingOrderform.getShipPrice());
                orderDetail.setGoodsPrice(shoppingOrderform.getTotalPrice().subtract(shoppingOrderform.getShipPrice()));
                orderDetail.setCouponId(shoppingOrderform.getCouponId());

                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setOfId(shoppingOrderform.getId());
                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
                if(null !=shoppingOrderform.getCouponId()){
                    ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                    shoppingCoupon.setRight_No(shoppingOrderform.getCouponId());
                    shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
                    orderDetail.setCouponInfo(shoppingCoupon);
                }
                if(shoppingOrderform.getOrderStatus()==orderStateAnomalous||shoppingOrderform.getOrderStatus()==orderStateHandAnomalous){  //异常订单，需要到订单支付表中查询实际支付情况

                    if(shoppingOrderPay.getCouponStatus()==1){
                        orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());   //优惠券优惠金额
                    }
                    orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
                    if(shoppingOrderPay.getIntegralStatus()==1){
                        orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
                        orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
                    }
                    if(shoppingOrderPay.getBalanceStatus()==1){
                        orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
                    }
                    if(shoppingOrderPay.getCashStatus()==1){
                        orderDetail.setPayPrice(shoppingOrderform.getPayPrice());
                    }
                }else{
                    orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());
                    orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
                    orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
                    orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
                    orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
                    orderDetail.setPayPrice(shoppingOrderform.getPayPrice());                  //现金支付金额
                }
                orderDetail.setPaymentId(shoppingOrderform.getPaymentId());   //现金支付方式Id
                orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
                orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）
                orderDetail.setOutOrderId(shoppingOrderform.getOutOrderId());  //第三方交互订单号 （麦座订单号/速停车账单号）
                //商品信息
                orderDetail.setGoodsInfo(CommonUtil.getOrderGoods(shoppingOrderform.getId()));
                List<HashMap<String,Object>> goodsList = orderDetail.getGoodsInfo();
                for(HashMap<String,Object> goodsMap:goodsList){
                    if(goodsMap.get("goodsId").equals(reqJson.get("goodsId"))&&goodsMap.get("goodsType")==reqJson.getInteger("cartType")){
                        orderDetail.setGoodsCount(Integer.valueOf(goodsMap.get("goodsCount").toString()));
                        break;
                    }
                }

                orderDetail.setUserId(shoppingOrderform.getUserId());
                orderDetail.setUserName(shoppingOrderform.getUserName());
                orderDetail.setMobile(shoppingOrderform.getMobile());

                //核销状态
                reqMap.clear();
                reqMap.put("ofId",shoppingOrderform.getId());
                reqMap.put("goodsId",reqJson.get("goodsId"));
                ShoppingWriteoff shoppingWriteoff = shoppingWriteoffDao.queryGoodsWriteoff(reqMap);
                if(null !=shoppingWriteoff){
                    orderDetail.setOffStatus(shoppingWriteoff.getOffStatus());
                }

                objList.add(orderDetail);
            }
            bizDataJson.put("objList",objList);
            //统计商品总数
            bizDataJson.put("sum",shoppingGoodscartDao.queryGoodsCount(reqMap));
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
     * 查询我的可核销爱艺计划列表
     */
    @Override
    public JSONObject queryMyWriteOffPlanList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            List<Integer> cartType = new ArrayList<>();
            reqMap.put("deleteStatus","0");

            bizDataJson.put("total",shoppingArtplanDao.queryTotalCount(reqMap));
            List<ShoppingArtplan> goodsList = shoppingArtplanDao.queryList(reqMap);
            JSONArray objArray = new JSONArray();
            for(ShoppingArtplan shoppingArtplan:goodsList){
                JSONObject obj = new JSONObject();

                obj.put("goodsId",shoppingArtplan.getId());
                obj.put("photoId",shoppingArtplan.getMainPhotoId());
                obj.put("goodsName",shoppingArtplan.getActivityName());
                objArray.add(obj);
            }

            bizDataJson.put("objList",objArray);
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
     * 查询我的可核销爱艺计划详情
     */
    @Override
    public JSONObject queryMyWriteOffPlanDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            bizDataJson.put("total",shoppingOrderformDao.queryPlanOrderTotalCount(reqMap));
            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryPlanOrderList(reqMap);
            List<OrderDetail> objList = new ArrayList<>();
            for(ShoppingOrderform shoppingOrderform:orderforms){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOfId(shoppingOrderform.getId());
                orderDetail.setOrderId(shoppingOrderform.getOrderId());
                orderDetail.setAddTime(shoppingOrderform.getAddTime());
                orderDetail.setOrderType(shoppingOrderform.getOrderType());
                orderDetail.setOrderStatus(shoppingOrderform.getOrderStatus());
                orderDetail.setTotalPrice(shoppingOrderform.getTotalPrice());
                orderDetail.setShipPrice(shoppingOrderform.getShipPrice());
                orderDetail.setGoodsPrice(shoppingOrderform.getTotalPrice().subtract(shoppingOrderform.getShipPrice()));
                orderDetail.setCouponId(shoppingOrderform.getCouponId());

                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setOfId(shoppingOrderform.getId());
                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
                if(null !=shoppingOrderform.getCouponId()){
                    ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                    shoppingCoupon.setRight_No(shoppingOrderform.getCouponId());
                    shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
                    orderDetail.setCouponInfo(shoppingCoupon);
                }
                if(shoppingOrderform.getOrderStatus()==orderStateAnomalous||shoppingOrderform.getOrderStatus()==orderStateHandAnomalous){  //异常订单，需要到订单支付表中查询实际支付情况

                    if(shoppingOrderPay.getCouponStatus()==1){
                        orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());   //优惠券优惠金额
                    }
                    orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
                    if(shoppingOrderPay.getIntegralStatus()==1){
                        orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
                        orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
                    }
                    if(shoppingOrderPay.getBalanceStatus()==1){
                        orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
                    }
                    if(shoppingOrderPay.getCashStatus()==1){
                        orderDetail.setPayPrice(shoppingOrderform.getPayPrice());
                    }
                }else{
                    orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());
                    orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
                    orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
                    orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
                    orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
                    orderDetail.setPayPrice(shoppingOrderform.getPayPrice());                  //现金支付金额
                }
                orderDetail.setPaymentId(shoppingOrderform.getPaymentId());   //现金支付方式Id
                orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
                orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）
                orderDetail.setOutOrderId(shoppingOrderform.getOutOrderId());  //第三方交互订单号 （麦座订单号/速停车账单号）
                //商品信息
                orderDetail.setGoodsInfo(CommonUtil.getOrderGoods(shoppingOrderform.getId()));
                List<HashMap<String,Object>> goodsList = orderDetail.getGoodsInfo();
                for(HashMap<String,Object> goodsMap:goodsList){
                    if(goodsMap.get("goodsId").equals(reqJson.get("goodsId"))&&goodsMap.get("goodsType")==reqJson.getInteger("cartType")){
                        orderDetail.setGoodsCount(Integer.valueOf(goodsMap.get("goodsCount").toString()));
                        break;
                    }
                }

                orderDetail.setUserId(shoppingOrderform.getUserId());
                orderDetail.setUserName(shoppingOrderform.getUserName());
                orderDetail.setMobile(shoppingOrderform.getMobile());

                //核销状态
                reqMap.clear();
                reqMap.put("ofId",shoppingOrderform.getId());
                reqMap.put("goodsId",reqJson.get("goodsId"));
                ShoppingWriteoff shoppingWriteoff = shoppingWriteoffDao.queryActivityWriteoff(reqMap);
                if(null !=shoppingWriteoff){
                    orderDetail.setOffStatus(shoppingWriteoff.getOffStatus());
                }

                objList.add(orderDetail);
            }
            bizDataJson.put("objList",objList);
            //统计商品总数
            bizDataJson.put("sum",shoppingGoodscartDao.queryGoodsCount(reqMap));
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
     * 查询我的可核销艺术活动列表
     */
    @Override
    public JSONObject queryMyWriteOffActivityList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            List<Integer> cartType = new ArrayList<>();
            reqMap.put("deleteStatus","0");

            bizDataJson.put("total",shoppingArtactivityDao.queryTotalCount(reqMap));
            List<ShoppingArtactivity> goodsList = shoppingArtactivityDao.queryList(reqMap);
            JSONArray objArray = new JSONArray();
            for(ShoppingArtactivity shoppingArtactivity:goodsList){
                JSONObject obj = new JSONObject();

                obj.put("goodsId",shoppingArtactivity.getId());
                obj.put("photoId",shoppingArtactivity.getMainPhotoId());
                obj.put("goodsName",shoppingArtactivity.getActivityName());
                objArray.add(obj);
            }

            bizDataJson.put("objList",objArray);
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
     * 查询我的可核销艺术活动详情
     */
    @Override
    public JSONObject queryMyWriteOffActivityDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            bizDataJson.put("total",shoppingOrderformDao.queryActivityOrderTotalCount(reqMap));
            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryActivityOrderList(reqMap);
            List<OrderDetail> objList = new ArrayList<>();
            for(ShoppingOrderform shoppingOrderform:orderforms){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOfId(shoppingOrderform.getId());
                orderDetail.setOrderId(shoppingOrderform.getOrderId());
                orderDetail.setAddTime(shoppingOrderform.getAddTime());
                orderDetail.setOrderType(shoppingOrderform.getOrderType());
                orderDetail.setOrderStatus(shoppingOrderform.getOrderStatus());
                orderDetail.setTotalPrice(shoppingOrderform.getTotalPrice());
                orderDetail.setShipPrice(shoppingOrderform.getShipPrice());
                orderDetail.setGoodsPrice(shoppingOrderform.getTotalPrice().subtract(shoppingOrderform.getShipPrice()));
                orderDetail.setCouponId(shoppingOrderform.getCouponId());

                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setOfId(shoppingOrderform.getId());
                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
                if(null !=shoppingOrderform.getCouponId()){
                    ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                    shoppingCoupon.setRight_No(shoppingOrderform.getCouponId());
                    shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
                    orderDetail.setCouponInfo(shoppingCoupon);
                }
                if(shoppingOrderform.getOrderStatus()==orderStateAnomalous||shoppingOrderform.getOrderStatus()==orderStateHandAnomalous){  //异常订单，需要到订单支付表中查询实际支付情况

                    if(shoppingOrderPay.getCouponStatus()==1){
                        orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());   //优惠券优惠金额
                    }
                    orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
                    if(shoppingOrderPay.getIntegralStatus()==1){
                        orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
                        orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
                    }
                    if(shoppingOrderPay.getBalanceStatus()==1){
                        orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
                    }
                    if(shoppingOrderPay.getCashStatus()==1){
                        orderDetail.setPayPrice(shoppingOrderform.getPayPrice());
                    }
                }else{
                    orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());
                    orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
                    orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
                    orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
                    orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
                    orderDetail.setPayPrice(shoppingOrderform.getPayPrice());                  //现金支付金额
                }
                orderDetail.setPaymentId(shoppingOrderform.getPaymentId());   //现金支付方式Id
                orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
                orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）
                orderDetail.setOutOrderId(shoppingOrderform.getOutOrderId());  //第三方交互订单号 （麦座订单号/速停车账单号）
                //商品信息
                orderDetail.setGoodsInfo(CommonUtil.getOrderGoods(shoppingOrderform.getId()));
                List<HashMap<String,Object>> goodsList = orderDetail.getGoodsInfo();
                for(HashMap<String,Object> goodsMap:goodsList){
                    if(goodsMap.get("goodsId").equals(reqJson.get("goodsId"))&&goodsMap.get("goodsType")==reqJson.getInteger("cartType")){
                        orderDetail.setGoodsCount(Integer.valueOf(goodsMap.get("goodsCount").toString()));
                        break;
                    }
                }

                orderDetail.setUserId(shoppingOrderform.getUserId());
                orderDetail.setUserName(shoppingOrderform.getUserName());
                orderDetail.setMobile(shoppingOrderform.getMobile());

                //核销状态
                reqMap.clear();
                reqMap.put("ofId",shoppingOrderform.getId());
                reqMap.put("goodsId",reqJson.get("goodsId"));
                ShoppingWriteoff shoppingWriteoff = shoppingWriteoffDao.queryActivityWriteoff(reqMap);
                if(null !=shoppingWriteoff){
                    orderDetail.setOffStatus(shoppingWriteoff.getOffStatus());
                }

                objList.add(orderDetail);
            }
            bizDataJson.put("objList",objList);
            //统计商品总数
            bizDataJson.put("sum",shoppingGoodscartDao.queryGoodsCount(reqMap));
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
     * 查询我的可核销优惠券列表
     */
    @Override
    public JSONObject queryMyWriteOffCouponList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            reqMap.put("isdelete","0");
            List<ShoppingCoupon> couponList =shoppingCouponDao.queryList(reqMap);
            bizDataJson.put("total",shoppingCouponDao.queryTotalCount(reqMap));
            bizDataJson.put("objList",couponList);
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
     * 查询我的优惠券核销详情
     */
    @Override
    public JSONObject queryMyWriteOffCouponDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("offStatus",1);
            bizDataJson.put("total",shoppingWriteoffCouponDao.queryWriteOffCount(reqMap));
            List<ShoppingWriteoffCoupon> shoppingWriteoffCouponList= shoppingWriteoffCouponDao.queryWriteOffList(reqMap);

            bizDataJson.put("objList",shoppingWriteoffCouponList);
            //已核销优惠券总数
            bizDataJson.put("sum",bizDataJson.get("total"));
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
     * 查询我的可核销演出项目兑换列表
     */
    @Override
    public JSONObject queryMyWriteOffRedeemProjectList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            reqMap.put("isDelete","0");

            bizDataJson.put("total",ticketRedeemProjectDao.queryTotalCount(reqMap));
            List<TicketRedeemProject> goodsList = ticketRedeemProjectDao.queryList(reqMap);
            JSONArray objArray = new JSONArray();
            for(TicketRedeemProject ticketRedeemProject:goodsList){
                JSONObject obj = new JSONObject();

                obj.put("projectId", ticketRedeemProject.getProjectId());
                obj.put("projectName", ticketRedeemProject.getProjectName());
                obj.put("projectImgUrl", ticketRedeemProject.getProjectImgUrl());   //海报图片
                objArray.add(obj);
            }

            bizDataJson.put("objList",objArray);
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
     * 查询我的可核销演出兑换详情
     */
    @Override
    public JSONObject queryMyWriteOffRedeemProjectDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("isDelete", "0");
            bizDataJson.put("total", ticketRedeemCodeDao.queryTotalCount(reqMap));
            List<TicketRedeemCode> objList = ticketRedeemCodeDao.queryList(reqMap);

            JSONArray objArray = new JSONArray();
            for(TicketRedeemCode ticketRedeemCode:objList){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code",ticketRedeemCode.getCode());  //兑换码
                jsonObject.put("exchangeTime",ticketRedeemCode.getExchangeTime());   //兑换时间
                jsonObject.put("exchangeMobile",ticketRedeemCode.getExchangeMobile());   //用户手机号
                jsonObject.put("companyName",ticketRedeemCode.getCompanyName());   //单位
//                jsonObject.put("projectId",ticketRedeemCode.getProjectId());   //项目ID
//                jsonObject.put("projectName",ticketRedeemCode.getProjectName());   //项目名称
//                jsonObject.put("projectImgUrl",ticketRedeemCode.getProjectImgUrl());   //项目海报图
                jsonObject.put("eventId",ticketRedeemCode.getEventId());   //场次id
                jsonObject.put("eventStartTime",ticketRedeemCode.getEventStartTime());   //开场时间
                jsonObject.put("venueName",ticketRedeemCode.getVenueName());   //场馆名称
//                jsonObject.put("watchingUser",ticketRedeemCode.getWatchingUser());   //观看人姓名
//                jsonObject.put("watchingMobile",ticketRedeemCode.getWatchingMobile());   //观看人手机号
//                jsonObject.put("watchingCard",ticketRedeemCode.getWatchingCard());   //观看人证件号
                if(StringUtil.isNotNull(ticketRedeemCode.getWriteoffTime())){
                    jsonObject.put("writeoffTime",ticketRedeemCode.getWriteoffTime());   //核销时间
                    jsonObject.put("offStatus",1);   //已核销
                }else{
                    jsonObject.put("offStatus",0);   //未核销
                }
                objArray.add(jsonObject);
            }
            bizDataJson.put("objList",objArray);
            //统计商品总数
            bizDataJson.put("sum",bizDataJson.get("total"));
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
