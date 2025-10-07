package com.centit.shopping.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.po.*;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/4/24 14:08
 * @description ：通用工具类
 */
public class CommonUtil {

    /**
     * 获取官方默认店铺
     */
    public static ShoppingStore getSystemStore() {
        ShoppingStore shoppingStore = new ShoppingStore();
        shoppingStore.setId("1");
        return CommonInit.staticShoppingStoreDao.queryDetail(shoppingStore);
    }

    /**
     * 获取店铺信息
     */
    public static ShoppingStore getStoreInfo(String storeId) {
        if(null !=storeId){
            ShoppingStore shoppingStore = new ShoppingStore();
            shoppingStore.setId(storeId);
            return CommonInit.staticShoppingStoreDao.queryDetail(shoppingStore);
        }
        return null;
    }

    /**
     * 获取用户购物车id
     */
    public static String getUserScId(String userId) {
        String goodsStoreId = getSystemStore().getId();  //默认商户id
        //商户购物车信息
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("userId", userId);
        reqMap.put("storeId", goodsStoreId);
        reqMap.put("deleteStatus", '0');
        List<ShoppingStorecart> shoppingStorecartList = CommonInit.staticshoppingStorecartDao.queryList(reqMap);
        ShoppingStorecart shoppingStorecart = new ShoppingStorecart();
        if (shoppingStorecartList.isEmpty()) {
            shoppingStorecart.setUserId(userId);
            shoppingStorecart.setStoreId(goodsStoreId);
            CommonInit.staticshoppingStorecartDao.insert(shoppingStorecart);
        } else {
            shoppingStorecart = shoppingStorecartList.get(0);
        }

        return shoppingStorecart.getId();
    }

    /**
     * 根据用户名获取用户
     */
    public static ShoppingUser getShoppingUserByName(String userName){
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("nickName",userName);
        List<ShoppingUser> users=CommonInit.staticShoppingUserDao.queryList(reqMap);
        if(!users.isEmpty()){
            return users.get(0);
        }
        return null;
    }

    /**
     * 根据userId获取用户
     */
    public static ShoppingUser getShoppingUserByUserId(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        return CommonInit.staticShoppingUserDao.queryDetail(user);
    }

    /**
     * 根据手机号获取用户
     */
    public static ShoppingUser getShoppingUserByMobile(String mobile){
        ShoppingUser user = new ShoppingUser();
        user.setMobile(mobile);
        return CommonInit.staticShoppingUserDao.queryDetailByMobile(user);
    }

    /**
     * 根据userId获取用户麦座id
     */
    public static String getMzUserId(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user=CommonInit.staticShoppingUserDao.queryDetail(user);
//        if(user==null||user.getMzuserid()==null||"".equals(user.getMzuserid())){
//            return "9253315";
//        }else{
//            return user.getMzuserid();
//        }
        return user.getMzuserid();

    }

    /**
     * 根据id获取收货地址详情
     */
    public static ShoppingAddress getShoppingAddress(String id){
        ShoppingAddress shoppingAddress = new ShoppingAddress();
        shoppingAddress.setId(id);
        return CommonInit.staticShoppingAddressDao.queryDetail(shoppingAddress);
    }

    public static boolean checkPayInfo(String id){
        //用户选择的优惠券
        return true;

    }

    /**
     * 获取订单关联的商品
     */
    public static List<HashMap<String,Object>> getOrderGoods(String ofId){
        ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
        shoppingOrderform.setId(ofId);
        shoppingOrderform = CommonInit.staticShoppingOrderformDao.queryDetail(shoppingOrderform);
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("ofId",ofId);
        reqMap.put("deleteStatus","0");
        List<ShoppingGoodscart> cartGoods = CommonInit.staticShoppingGoodscartDao.queryList(reqMap);
        List<HashMap<String,Object>> goods = new ArrayList<>();
        for(ShoppingGoodscart shoppingGoodscart:cartGoods){
            HashMap<String,Object> map =new HashMap<>();
            //充值订单
            if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.RECHARGE_CART_TYPE)){
                ShoppingRecharge shoppingRecharge = new ShoppingRecharge();
                shoppingRecharge.setId(shoppingGoodscart.getGoodsId());
                shoppingRecharge =CommonInit.staticShoppingRechargeDao.queryDetail(shoppingRecharge);

                map.put("goodsId",shoppingRecharge.getId());
                map.put("photoId",Const.RECHARGE_ORDER_PHOTO);  //充值默认图片id
                map.put("goodsName","余额充值");
                map.put("moneyAmount",shoppingRecharge.getMoneyAmount());
            }
            //点播订单
            else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.VIDEO_CART_TYPE)){
                TOnOndemand tOnOndemand = new TOnOndemand();
                tOnOndemand.setId(shoppingGoodscart.getGoodsId());
                tOnOndemand =CommonInit.staticTOnOndemandDao.queryDetail(tOnOndemand);

                map.put("goodsId",tOnOndemand.getId());
                map.put("photoUrl",tOnOndemand.getCoverfilepath());
                map.put("goodsName",tOnOndemand.getTitle());
            }
            //停车缴费订单
            else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.PARK_CART_TYPE)){
                ParkOrder parkOrder = new ParkOrder();
                parkOrder.setOrderNo(shoppingGoodscart.getGoodsId());
                parkOrder.setOrderId(shoppingOrderform.getOrderId());
                parkOrder =CommonInit.staticParkOrderDao.queryDetail(parkOrder);

                map.put("goodsId",parkOrder.getOrderNo());
//                map.put("photoId",ticketProject.getProjectImgUrl());
                map.put("photoId",Const.PARK_ORDER_PHOTO);  //停车缴费默认图片id
                map.put("goodsName","停车缴费");
                //订单列表展示车牌号和停车场信息
                map.put("plateNo",parkOrder.getPlateNo());
                map.put("parkName",parkOrder.getParkName());
            }
            //演出票订单
            else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.TICKET_CART_TYPE)){
                TicketEvent ticketEvent = new TicketEvent();
                ticketEvent.setEventId(shoppingGoodscart.getGoodsId());
                ticketEvent =CommonInit.staticTicketEventDao.queryDetail(ticketEvent);
                //根据场次信息查询项目信息
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(ticketEvent.getProjectId());
                ticketProject = CommonInit.staticTicketProjectDao.queryDetail(ticketProject);
                map.put("goodsId",ticketProject.getProjectId());
//                map.put("photoId",ticketProject.getProjectImgUrl());
                map.put("photoUrl",ticketProject.getProjectImgUrl());
                map.put("goodsName",ticketProject.getProjectName());

                //演出订单列表展示场馆和日期信息
                map.put("venueName",ticketEvent.getVenueName());
                map.put("eventStartTime",ticketEvent.getEventStartTime());
            }else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.SHOPPING_ACT_CART_TYPE)){
                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                //活动主体信息
                shoppingArtactivity = CommonInit.staticShoppingArtactivityDao.queryDetail(shoppingArtactivity);
                map.put("goodsId",shoppingArtactivity.getId());
                map.put("photoId",shoppingArtactivity.getMainPhotoId());
                map.put("goodsName",shoppingArtactivity.getActivityName());
                map.put("spec",shoppingGoodscart.getSpecInfo());
                //报名信息
                reqMap.put("ofId",ofId);
                reqMap.put("activityId",shoppingArtactivity.getId());
                List<ShoppingArtactivitySignupinfo> signupInfos = CommonInit.staticShoppingArtactivitySignupinfoDao.queryList(reqMap);
                map.put("signupInfos",signupInfos);

                ShoppingRefund shoppingRefund = new ShoppingRefund();
                shoppingRefund.setGcId(shoppingGoodscart.getId());
                shoppingRefund.setDeleteStatus("0");
                shoppingRefund = CommonInit.staticShoppingRefundDao.queryDetail(shoppingRefund);
                if(null !=shoppingRefund){
                    map.put("refundStatus",shoppingRefund.getRefundStatus());
                    map.put("refundId",shoppingRefund.getId());
                }else {
                    if(shoppingOrderform.getOrderStatus()==50){
                        map.put("toRefund",true);
                    }else if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)&&(shoppingOrderform.getOrderStatus()==20||shoppingOrderform.getOrderStatus()==30||shoppingOrderform.getOrderStatus()==60||shoppingOrderform.getOrderStatus()==70)){  //合并支付订单
                        map.put("toRefund",true);
                    }
                }
            }else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.SHOPPING_PLAN_CART_TYPE)){
                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                //活动主体信息
                shoppingArtplan = CommonInit.staticShoppingArtplanDao.queryDetail(shoppingArtplan);
                map.put("goodsId",shoppingArtplan.getId());
                map.put("photoId",shoppingArtplan.getMainPhotoId());
                map.put("goodsName",shoppingArtplan.getActivityName());
                map.put("spec",shoppingGoodscart.getSpecInfo());
                //报名信息
                reqMap.put("ofId",ofId);
                reqMap.put("activityId",shoppingArtplan.getId());
                List<ShoppingArtplanSignupinfo> signupInfos = CommonInit.staticShoppingArtplanSignupinfoDao.queryList(reqMap);
                map.put("signupInfos",signupInfos);

                ShoppingRefund shoppingRefund = new ShoppingRefund();
                shoppingRefund.setGcId(shoppingGoodscart.getId());
                shoppingRefund.setDeleteStatus("0");
                shoppingRefund = CommonInit.staticShoppingRefundDao.queryDetail(shoppingRefund);
                if(null !=shoppingRefund){
                    map.put("refundStatus",shoppingRefund.getRefundStatus());
                    map.put("refundId",shoppingRefund.getId());
                }else {
                    if(shoppingOrderform.getOrderStatus()==50){
                        map.put("toRefund",true);
                    }else if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)&&(shoppingOrderform.getOrderStatus()==20||shoppingOrderform.getOrderStatus()==30||shoppingOrderform.getOrderStatus()==60||shoppingOrderform.getOrderStatus()==70)){  //合并支付订单
                        map.put("toRefund",true);
                    }
                }
            }else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.SHOPPING_CLASS_CART_TYPE)){
                ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                shoppingArtclass.setId(shoppingGoodscart.getGoodsId());
                //活动主体信息
                shoppingArtclass = CommonInit.staticShoppingArtclassDao.queryDetail(shoppingArtclass);
                map.put("goodsId",shoppingArtclass.getId());
                map.put("photoId",shoppingArtclass.getMainPhotoId());
                map.put("goodsName",shoppingArtclass.getClassName());

                reqMap.put("ofId",ofId);
                reqMap.put("classId",shoppingArtclass.getId());
                List<ShoppingArtclassSignupinfo> signupInfos = CommonInit.staticShoppingArtclassSignupinfoDao.queryList(reqMap);
                map.put("signupInfos",signupInfos);

                ShoppingRefund shoppingRefund = new ShoppingRefund();
                shoppingRefund.setGcId(shoppingGoodscart.getId());
                shoppingRefund.setDeleteStatus("0");
                shoppingRefund = CommonInit.staticShoppingRefundDao.queryDetail(shoppingRefund);
                if(null !=shoppingRefund){
                    map.put("refundStatus",shoppingRefund.getRefundStatus());
                    map.put("refundId",shoppingRefund.getId());
                }else {
                    if(shoppingOrderform.getOrderStatus()==50){
                        map.put("toRefund",true);
                    }else if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)&&(shoppingOrderform.getOrderStatus()==20||shoppingOrderform.getOrderStatus()==30||shoppingOrderform.getOrderStatus()==60||shoppingOrderform.getOrderStatus()==70)){  //合并支付订单
                        map.put("toRefund",true);
                    }
                }
            }else{
                ShoppingGoods shoppingGoods=new ShoppingGoods();
                shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                shoppingGoods = CommonInit.staticShoppingGoodsDao.queryDetail(shoppingGoods);

                map.put("goodsId",shoppingGoods.getId());
                map.put("photoId",shoppingGoods.getGoodsMainPhotoId());
                map.put("goodsName",shoppingGoods.getGoodsName());

                map.put("selfextractionAddress",shoppingGoods.getSelfextractionAddress());
            }
            map.put("gcId",shoppingGoodscart.getId());
            map.put("transport",shoppingGoodscart.getTransport());
            map.put("goodsType",shoppingGoodscart.getCartType());
            map.put("spec",shoppingGoodscart.getSpecInfo());
            map.put("goodsCount",shoppingGoodscart.getCount());
            map.put("goodsPrice",shoppingGoodscart.getPrice());

            goods.add(map);
        }
        return goods;
    }

    /**
     * 获取订单关联的商品
     */
    public static List<HashMap<String,Object>> getOrderGoodsForSeller(String ofId){
        ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
        shoppingOrderform.setId(ofId);
        shoppingOrderform = CommonInit.staticShoppingOrderformDao.queryDetail(shoppingOrderform);
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("ofId",ofId);
        reqMap.put("deleteStatus","0");
        List<ShoppingGoodscart> cartGoods = CommonInit.staticShoppingGoodscartDao.queryList(reqMap);
        List<HashMap<String,Object>> goods = new ArrayList<>();
        for(ShoppingGoodscart shoppingGoodscart:cartGoods){
            HashMap<String,Object> map =new HashMap<>();
            map.put("cartType",shoppingGoodscart.getCartType());
            //充值订单
            if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.RECHARGE_CART_TYPE)){
                ShoppingRecharge shoppingRecharge = new ShoppingRecharge();
                shoppingRecharge.setId(shoppingGoodscart.getGoodsId());
                shoppingRecharge =CommonInit.staticShoppingRechargeDao.queryDetail(shoppingRecharge);

                map.put("goodsId",shoppingRecharge.getId());
                map.put("photoId",Const.RECHARGE_ORDER_PHOTO);  //充值默认图片id
                map.put("goodsName","余额充值");
                map.put("moneyAmount",shoppingRecharge.getMoneyAmount());
            }
            //点播订单
            else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.VIDEO_CART_TYPE)){
                TOnOndemand tOnOndemand = new TOnOndemand();
                tOnOndemand.setId(shoppingGoodscart.getGoodsId());
                tOnOndemand =CommonInit.staticTOnOndemandDao.queryDetail(tOnOndemand);

                map.put("goodsId",tOnOndemand.getId());
                map.put("photoUrl",tOnOndemand.getCoverfilepath());
                map.put("goodsName",tOnOndemand.getTitle());
            }
            //停车缴费订单
            else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.PARK_CART_TYPE)){
                ParkOrder parkOrder = new ParkOrder();
                parkOrder.setOrderNo(shoppingGoodscart.getGoodsId());
                parkOrder.setOrderId(shoppingOrderform.getOrderId());
                parkOrder =CommonInit.staticParkOrderDao.queryDetail(parkOrder);

                map.put("goodsId",parkOrder.getOrderNo());
//                map.put("photoId",ticketProject.getProjectImgUrl());
                map.put("photoId",Const.PARK_ORDER_PHOTO);  //停车缴费默认图片id
                map.put("goodsName","停车缴费");
                //订单列表展示车牌号和停车场信息
                map.put("plateNo",parkOrder.getPlateNo());
                map.put("parkName",parkOrder.getParkName());
            }
            //演出票订单
            else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.TICKET_CART_TYPE)){
                TicketEvent ticketEvent = new TicketEvent();
                ticketEvent.setEventId(shoppingGoodscart.getGoodsId());
                ticketEvent =CommonInit.staticTicketEventDao.queryDetail(ticketEvent);
                //根据场次信息查询项目信息
                TicketProject ticketProject = new TicketProject();
                ticketProject.setProjectId(ticketEvent.getProjectId());
                ticketProject = CommonInit.staticTicketProjectDao.queryDetail(ticketProject);
                map.put("goodsId",ticketProject.getProjectId());
//                map.put("photoId",ticketProject.getProjectImgUrl());
                map.put("photoUrl",ticketProject.getProjectImgUrl());
                map.put("goodsName",ticketProject.getProjectName());

                //演出订单列表展示场馆和日期信息
                map.put("venueName",ticketEvent.getVenueName());
                map.put("eventStartTime",ticketEvent.getEventStartTime());
                map.put("eventName",ticketEvent.getEventName());
            }else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.SHOPPING_ACT_CART_TYPE)){
                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                //活动主体信息
                shoppingArtactivity = CommonInit.staticShoppingArtactivityDao.queryDetail(shoppingArtactivity);
                map.put("goodsId",shoppingArtactivity.getId());
                map.put("photoId",shoppingArtactivity.getMainPhotoId());
                map.put("goodsName",shoppingArtactivity.getActivityName());

                //报名信息
                reqMap.put("ofId",ofId);
                reqMap.put("activityId",shoppingArtactivity.getId());
                List<ShoppingArtactivitySignupinfo> signupInfos = CommonInit.staticShoppingArtactivitySignupinfoDao.queryList(reqMap);
                map.put("signupInfos",signupInfos);

                ShoppingRefund shoppingRefund = new ShoppingRefund();
                shoppingRefund.setGcId(shoppingGoodscart.getId());
                shoppingRefund.setDeleteStatus("0");
                shoppingRefund = CommonInit.staticShoppingRefundDao.queryDetail(shoppingRefund);
                if(null !=shoppingRefund){
                    map.put("refundStatus",shoppingRefund.getRefundStatus());
                    map.put("refundId",shoppingRefund.getId());
                }else {
                    if(shoppingOrderform.getOrderStatus()==50){
                        map.put("toRefund",true);
                    }else if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)&&(shoppingOrderform.getOrderStatus()==20||shoppingOrderform.getOrderStatus()==30||shoppingOrderform.getOrderStatus()==60||shoppingOrderform.getOrderStatus()==70)){  //合并支付订单
                        map.put("toRefund",true);
                    }
                }
            }else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.SHOPPING_PLAN_CART_TYPE)){
                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                //爱艺计划主体信息
                shoppingArtplan = CommonInit.staticShoppingArtplanDao.queryDetail(shoppingArtplan);
                map.put("goodsId",shoppingArtplan.getId());
                map.put("photoId",shoppingArtplan.getMainPhotoId());
                map.put("goodsName",shoppingArtplan.getActivityName());

                //报名信息
                reqMap.put("ofId",ofId);
                reqMap.put("activityId",shoppingArtplan.getId());
                List<ShoppingArtplanSignupinfo> signupInfos = CommonInit.staticShoppingArtplanSignupinfoDao.queryList(reqMap);
                map.put("signupInfos",signupInfos);

                ShoppingRefund shoppingRefund = new ShoppingRefund();
                shoppingRefund.setGcId(shoppingGoodscart.getId());
                shoppingRefund.setDeleteStatus("0");
                shoppingRefund = CommonInit.staticShoppingRefundDao.queryDetail(shoppingRefund);
                if(null !=shoppingRefund){
                    map.put("refundStatus",shoppingRefund.getRefundStatus());
                    map.put("refundId",shoppingRefund.getId());
                }else {
                    if(shoppingOrderform.getOrderStatus()==50){
                        map.put("toRefund",true);
                    }else if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)&&(shoppingOrderform.getOrderStatus()==20||shoppingOrderform.getOrderStatus()==30||shoppingOrderform.getOrderStatus()==60||shoppingOrderform.getOrderStatus()==70)){  //合并支付订单
                        map.put("toRefund",true);
                    }
                }
            }else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.SHOPPING_CLASS_CART_TYPE)){
                ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                shoppingArtclass.setId(shoppingGoodscart.getGoodsId());
                //活动主体信息
                shoppingArtclass = CommonInit.staticShoppingArtclassDao.queryDetail(shoppingArtclass);
                map.put("goodsId",shoppingArtclass.getId());
                map.put("photoId",shoppingArtclass.getMainPhotoId());
                map.put("goodsName",shoppingArtclass.getClassName());

                reqMap.put("ofId",ofId);
                reqMap.put("classId",shoppingArtclass.getId());
                List<ShoppingArtclassSignupinfo> signupInfos = CommonInit.staticShoppingArtclassSignupinfoDao.queryList(reqMap);
                map.put("signupInfos",signupInfos);

                ShoppingRefund shoppingRefund = new ShoppingRefund();
                shoppingRefund.setGcId(shoppingGoodscart.getId());
                shoppingRefund.setDeleteStatus("0");
                shoppingRefund = CommonInit.staticShoppingRefundDao.queryDetail(shoppingRefund);
                if(null !=shoppingRefund){
                    map.put("refundStatus",shoppingRefund.getRefundStatus());
                    map.put("refundId",shoppingRefund.getId());
                }else {
                    if(shoppingOrderform.getOrderStatus()==50){
                        map.put("toRefund",true);
                    }else if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)&&(shoppingOrderform.getOrderStatus()==20||shoppingOrderform.getOrderStatus()==30||shoppingOrderform.getOrderStatus()==60||shoppingOrderform.getOrderStatus()==70)){  //合并支付订单
                        map.put("toRefund",true);
                    }
                }
            }else{
                ShoppingGoods shoppingGoods=new ShoppingGoods();
                shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                shoppingGoods = CommonInit.staticShoppingGoodsDao.queryDetail(shoppingGoods);

                map.put("goodsId",shoppingGoods.getId());
                map.put("photoId",shoppingGoods.getGoodsMainPhotoId());
                map.put("goodsName",shoppingGoods.getGoodsName());

                map.put("selfextractionAddress",shoppingGoods.getSelfextractionAddress());

                ShoppingRefund shoppingRefund = new ShoppingRefund();
                shoppingRefund.setGcId(shoppingGoodscart.getId());
                shoppingRefund.setDeleteStatus("0");
                shoppingRefund = CommonInit.staticShoppingRefundDao.queryDetail(shoppingRefund);
                if(null !=shoppingRefund){
                    map.put("refundStatus",shoppingRefund.getRefundStatus());
                    map.put("refundId",shoppingRefund.getId());
                }else if(shoppingOrderform.getOrderStatus()==20||shoppingOrderform.getOrderStatus()==30||shoppingOrderform.getOrderStatus()==50||shoppingOrderform.getOrderStatus()==60||shoppingOrderform.getOrderStatus()==70){
                    map.put("toRefund",true);
                }
            }
            map.put("gcId",shoppingGoodscart.getId());
            map.put("transport",shoppingGoodscart.getTransport());
            map.put("goodsType",shoppingGoodscart.getCartType());
            map.put("spec",shoppingGoodscart.getSpecInfo());
            map.put("goodsCount",shoppingGoodscart.getCount());
            map.put("goodsPrice",shoppingGoodscart.getPrice());

            goods.add(map);
        }
        return goods;
    }

    /**
     * 获取订单中可评价的商品列表
     */
    public static List<HashMap<String,Object>> getOrderEvaluateGoods(String ofId){
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("ofId",ofId);
        reqMap.put("deleteStatus","0");
        List<ShoppingGoodscart> cartGoods = CommonInit.staticShoppingGoodscartDao.queryList(reqMap);
        List<HashMap<String,Object>> goods = new ArrayList<>();
        for(ShoppingGoodscart shoppingGoodscart:cartGoods){
            HashMap<String,Object> map =new HashMap<>();
            //只有文创或积分商品可以评价
            if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_CUL_CART_TYPE)||shoppingGoodscart.getCartType().equals(Const.SHOPPING_INT_CART_TYPE))){
                ShoppingGoods shoppingGoods=new ShoppingGoods();
                shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                shoppingGoods = CommonInit.staticShoppingGoodsDao.queryDetail(shoppingGoods);

                map.put("goodsId",shoppingGoods.getId());
                map.put("photoId",shoppingGoods.getGoodsMainPhotoId());
                map.put("goodsName",shoppingGoods.getGoodsName());
            }
            map.put("spec",shoppingGoodscart.getSpecInfo());
            map.put("goodsCount",shoppingGoodscart.getCount());

            goods.add(map);
        }
        return goods;
    }

    public static List<ShoppingGoodsclass> getChildClass(String parentId) {
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("parentId",parentId);
        reqMap.put("deleteStatus","0");
        return CommonInit.staticShoppingGoodsclassDao.queryList(reqMap);
    }


    public static List<Map<String,Object>> getGcValue(String gcId) {
        String valueStr="";
        String idStr="";
        //获取当前分类名称
        ShoppingGoodsclass shoppingGoodsclass = new ShoppingGoodsclass();
        shoppingGoodsclass.setId(gcId);
        shoppingGoodsclass = CommonInit.staticShoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
        valueStr=shoppingGoodsclass.getClassName();
        idStr = shoppingGoodsclass.getId();
        while(shoppingGoodsclass.getParentId()!=null){
            shoppingGoodsclass.setId(shoppingGoodsclass.getParentId());
            shoppingGoodsclass = CommonInit.staticShoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
            valueStr=shoppingGoodsclass.getClassName()+">"+valueStr;
            idStr = shoppingGoodsclass.getId()+">"+idStr;
        }
        String[] ids =idStr.split(">");
        String[] values =valueStr.split(">");
        List<Map<String,Object>> res = new ArrayList<>();
        for(int i=0;i<ids.length;i++){
            Map<String,Object> map =new HashMap<>();
            map.put("id",ids[i]);
            map.put("value",values[i]);

            res.add(map);
        }
        return res;
    }

    public static List<ShoppingGoodsspecproperty> getPropertys(String specId){
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("specId",specId);
        reqMap.put("deleteStatus","0");
        List<ShoppingGoodsspecproperty> propertys = CommonInit.staticShoppingGoodsspecpropertyDao.queryList(reqMap);
        return propertys;
    }

    /**
     * 查询物流公司信息
     *
     */
    public static ShoppingExpressCompany getExpressCompany(String ecId){
        ShoppingExpressCompany shoppingExpressCompany = new ShoppingExpressCompany();
        shoppingExpressCompany.setId(ecId);

        return CommonInit.staticShoppingExpressCompanyDao.queryDetail(shoppingExpressCompany);
    }

    /**
     * 查询物流
     *
     */
    public static JSONObject queryTransportInfo(String ecId,String shipCode,String receiver_phone) {
        try{
            Map<String, String> param = new HashMap<>();
            ShoppingExpressCompany shoppingExpressCompany= new ShoppingExpressCompany();
            shoppingExpressCompany.setId(ecId);
            shoppingExpressCompany = CommonInit.staticShoppingExpressCompanyDao.queryDetail(shoppingExpressCompany);
            param.put("com", shoppingExpressCompany.getCompanyMark());
            param.put("num", shipCode);
            if(StringUtil.isNotNull(receiver_phone)){
                param.put("phone", receiver_phone);
            }
            String paramString = new Gson().toJson(param);
//            String customer = "F29F22B9A1023053073B20EB7383F3B1";
//            String key = "YvWLMRkQ7738";
            String customer = "21F1E7F76643C421D6DB7E0FBA7F97EE";
            String key = "nZXUriYh703";
            String sign = MD5Util.md5(paramString + key + customer).toUpperCase();
            HashMap<String, String> params = new HashMap<>();
            params.put("param", paramString);
            params.put("sign", sign);
            params.put("customer", customer);
            String resp = HttpSendUtil.post("http://poll.kuaidi100.com/poll/query.do", params);
            return JSONObject.parseObject(resp);
        }catch (Exception e){
        }
        return null;
    }

    public static void main(String[] args) {
        Map<String, String> param = new HashMap<>();

        param.put("com", "shunfeng");
        param.put("num", "SF1668262290505");
        param.put("phone", "13602899696");
        String paramString = new Gson().toJson(param);
//            String customer = "F29F22B9A1023053073B20EB7383F3B1";
//            String key = "YvWLMRkQ7738";
        String customer = "21F1E7F76643C421D6DB7E0FBA7F97EE";
        String key = "nZXUriYh703";
        String sign = MD5Util.md5(paramString + key + customer).toUpperCase();
        HashMap<String, String> params = new HashMap<>();
        params.put("param", paramString);
        params.put("sign", sign);
        params.put("customer", customer);
        String resp = HttpSendUtil.post("http://poll.kuaidi100.com/poll/query.do", params);


        System.out.println(resp);
    }

    /**
     * 获取商品类型
     *
     */
    public static ShoppingGoodstype getShoppingGoodstype(String id){
        ShoppingGoodstype shoppingGoodstype = new ShoppingGoodstype();
        shoppingGoodstype.setId(id);

        return CommonInit.staticShoppingGoodstypeDao.queryDetail(shoppingGoodstype);
    }

    /**
     * 获取商品评价照片
     *
     */
    public static List<ShoppingEvaluatePhoto> getShoppingEvaluatePhotos(String evaluateId){
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("evaluateId",evaluateId);
        reqMap.put("deleteStatus",0);

        return CommonInit.staticShoppingEvaluatePhotoDao.queryList(reqMap);
    }

    /**
     * 获取系统设置的余额充值优惠折扣
     *
     */
    public static BigDecimal getRechargeDiscountNum(){
        HashMap<String, Object> reqMap = new HashMap<>();

         List<ShoppingRechargeDiscount> objList= CommonInit.staticShoppingRechargeDiscountDao.queryList(reqMap);
         if(objList.isEmpty()){
             return BigDecimal.ONE;
         }else{
             return objList.get(0).getDiscountNum();
         }
    }

    /**
     * 获取商品可用的优惠券
     *
     */
    public static JSONArray getGoodsCouppon(String goodsId,int goodsType,String userId,BigDecimal goodsPrice,BigDecimal payPrice){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user= CommonInit.staticShoppingUserDao.queryDetail(user);

        ShoppingGoods shoppingGoods=new ShoppingGoods();
        shoppingGoods.setId(goodsId);
        shoppingGoods = CommonInit.staticShoppingGoodsDao.queryDetail(shoppingGoods);
        String gcId = shoppingGoods.getGcId();
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("goodsId",goodsId);
        reqMap.put("gcId",gcId);
        reqMap.put("goodsType",goodsType);
        List<ShoppingCoupon> coupons = CommonInit.staticShoppingCouponDao.queryUserGoodsCouponList(reqMap);
        List<String> couponIds = new ArrayList<>();
        for(ShoppingCoupon shoppingCoupon:coupons){
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
            if(shoppingCoupon.getStart_Date()!=null&&StringUtil.compareMillisecond(shoppingCoupon.getStart_Date(),sf)>0){
                continue;
            }
            if(shoppingCoupon.getEnd_Date()!=null){
                try {
                    Calendar cd = Calendar.getInstance();
                    cd.setTime(sf.parse(shoppingCoupon.getEnd_Date()));
                    cd.add(Calendar.DATE, 1);//增加n天
                    if(StringUtil.compareMillisecond(sf.format(sf.parse(sf.format(cd.getTime())).getTime()),sf)<0){
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if(shoppingCoupon.getRight_Type().equals("coincp")){  //代金券，在现金的基础上判断满减额度
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&new BigDecimal(shoppingCoupon.getMax_Money()).compareTo(payPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&new BigDecimal(shoppingCoupon.getMin_Money()).compareTo(payPrice)>0){
                    continue;
                }
            }else if(shoppingCoupon.getRight_Type().equals("discount")){  //折扣券，在原价的基础上判断满减额度
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&new BigDecimal(shoppingCoupon.getMax_Money()).compareTo(goodsPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&new BigDecimal(shoppingCoupon.getMin_Money()).compareTo(goodsPrice)>0){
                    continue;
                }
            }
            couponIds.add(shoppingCoupon.getRight_No());
        }

        System.out.println("+++++++++couponIds================="+couponIds);

        JSONArray objList = new JSONArray();
        if(null !=user&&null !=user.getMobile()){
            JSONArray resArray = CRMService.getUserCouponList(userId,user.getMobile(),"0");
            if(null !=resArray){
                for (int i = 0; i < resArray.size(); i++) {
                    JSONObject obj =  resArray.getJSONObject(i);
                    String right_No =  obj.get("right_No").toString();
                    if(couponIds.contains(right_No)){
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        ShoppingCoupon detail = CommonInit.staticShoppingCouponDao.queryDetail(shoppingCoupon);
                        if(null !=detail){
                            obj.put("detail",detail);
                            objList.add(obj);
                        }
                    }
                }
            }

        }
        return objList;
    }

    /**
     * 获取艺教可用的优惠券
     *
     */
    public static JSONArray getArtCouppon(String goodsId,int goodsType,String userId,BigDecimal goodsPrice,BigDecimal payPrice){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user= CommonInit.staticShoppingUserDao.queryDetail(user);

        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("goodsId",goodsId);
        reqMap.put("gcId",goodsType);
        reqMap.put("goodsType",goodsType);
        List<ShoppingCoupon> coupons = CommonInit.staticShoppingCouponDao.queryUserArtCouponList(reqMap);
        List<String> couponIds = new ArrayList<>();
        for(ShoppingCoupon shoppingCoupon:coupons){
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
            if(shoppingCoupon.getStart_Date()!=null&&StringUtil.compareMillisecond(shoppingCoupon.getStart_Date(),sf)>0){
                continue;
            }
            if(shoppingCoupon.getEnd_Date()!=null){
                try {
                    Calendar cd = Calendar.getInstance();
                    cd.setTime(sf.parse(shoppingCoupon.getEnd_Date()));
                    cd.add(Calendar.DATE, 1);//增加n天
                    if(StringUtil.compareMillisecond(sf.format(sf.parse(sf.format(cd.getTime())).getTime()),sf)<0){
                        System.out.println("22222");
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if(shoppingCoupon.getRight_Type().equals("coincp")){  //代金券，在现金的基础上判断满减额度
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&new BigDecimal(shoppingCoupon.getMax_Money()).compareTo(payPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&new BigDecimal(shoppingCoupon.getMin_Money()).compareTo(payPrice)>0){
                    continue;
                }
            }else if(shoppingCoupon.getRight_Type().equals("discount")){  //折扣券，在原价的基础上判断满减额度
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&new BigDecimal(shoppingCoupon.getMax_Money()).compareTo(goodsPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&new BigDecimal(shoppingCoupon.getMin_Money()).compareTo(goodsPrice)>0){
                    continue;
                }
            }
            couponIds.add(shoppingCoupon.getRight_No());
        }

        JSONArray objList = new JSONArray();
        if(null !=user&&null !=user.getMobile()){
            JSONArray resArray = CRMService.getUserCouponList(userId,user.getMobile(),"0");
            if(null !=resArray){
                for (int i = 0; i < resArray.size(); i++) {
                    JSONObject obj = (JSONObject) resArray.get(i);
                    String right_No =  obj.get("right_No").toString();
                    if(couponIds.contains(right_No)){
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        ShoppingCoupon detail = CommonInit.staticShoppingCouponDao.queryDetail(shoppingCoupon);
                        if(null !=detail){
                            obj.put("detail",detail);
                            objList.add(obj);
                        }
                    }
                }
            }

        }
        return objList;
    }

    /**
     * 获取购物车结算时可用的优惠券
     *
     */
    public static JSONArray getCartsCouppon(String userId,JSONArray goodsArray){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user= CommonInit.staticShoppingUserDao.queryDetail(user);

        //用户拥有的优惠券
        JSONArray objList = new JSONArray();
        HashSet<String> userCouponIds = new HashSet<>();
        if(null !=user&&null !=user.getMobile()){
            JSONArray resArray = CRMService.getUserCouponList(userId,user.getMobile(),"0");
            if(null !=resArray){
                for (int i = 0; i < resArray.size(); i++) {
                    JSONObject obj =  resArray.getJSONObject(i);
                    String right_No =  obj.get("right_No").toString();
                    userCouponIds.add(right_No);
                    ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                    shoppingCoupon.setRight_No(right_No);
                    obj.put("detail",CommonInit.staticShoppingCouponDao.queryDetail(shoppingCoupon));
                    objList.add(obj);
                }
            }

        }

        //判断用户的优惠券里哪些符合使用条件
        //首先统计出每个优惠券能用于订单中的哪些商品
        Map<String,HashSet<String>> map = new HashMap<>();
        for(int i=0;i<goodsArray.size();i++){
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            if(goodsObj.get("cartType")==Const.SHOPPING_CUL_CART_TYPE){  //文创
                String goodsId = goodsObj.getString("goodsId");
                ShoppingGoods shoppingGoods=new ShoppingGoods();
                shoppingGoods.setId(goodsId);
                shoppingGoods = CommonInit.staticShoppingGoodsDao.queryDetail(shoppingGoods);
                String gcId = shoppingGoods.getGcId();
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("goodsId",goodsId);
                reqMap.put("gcId",gcId);
                reqMap.put("goodsType",1);
                List<ShoppingCoupon> coupons = CommonInit.staticShoppingCouponDao.queryUserGoodsCouponList(reqMap);
                for(ShoppingCoupon shoppingCoupon:coupons){
                    String right_No =shoppingCoupon.getRight_No();
                    if(userCouponIds.contains(right_No)){
                        if(map.get(right_No)==null){
                            HashSet<String> set =new HashSet<>();
                            set.add(goodsObj.get("cartType")+"&"+goodsId);
                            map.put(right_No,set);
                        }else{
                            HashSet<String> set =map.get(right_No);
                            set.add(goodsObj.get("cartType")+"&"+goodsId);
                            map.put(right_No,set);
                        }
                    }
                }
            }
            else if(goodsObj.get("cartType")==Const.SHOPPING_ACT_CART_TYPE) {  //艺术活动
                String goodsId = goodsObj.getString("goodsId");
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("goodsId",goodsId);
                reqMap.put("gcId","3");
                reqMap.put("goodsType",3);
                List<ShoppingCoupon> coupons = CommonInit.staticShoppingCouponDao.queryUserArtCouponList(reqMap);
                for(ShoppingCoupon shoppingCoupon:coupons){
                    String right_No =shoppingCoupon.getRight_No();
                    if(userCouponIds.contains(right_No)){
                        if(map.get(right_No)==null){
                            HashSet<String> set =new HashSet<>();
                            set.add(goodsObj.get("cartType")+"&"+goodsId);
                            map.put(right_No,set);
                        }else{
                            HashSet<String> set =map.get(right_No);
                            set.add(goodsObj.get("cartType")+"&"+goodsId);
                            map.put(right_No,set);
                        }
                    }
                }
            }
            else if(goodsObj.get("cartType")==Const.SHOPPING_PLAN_CART_TYPE) {  //爱艺计划
                String goodsId = goodsObj.getString("goodsId");
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("goodsId",goodsId);
                reqMap.put("gcId","3");
                reqMap.put("goodsType",6);
                List<ShoppingCoupon> coupons = CommonInit.staticShoppingCouponDao.queryUserArtCouponList(reqMap);
                for(ShoppingCoupon shoppingCoupon:coupons){
                    String right_No =shoppingCoupon.getRight_No();
                    if(userCouponIds.contains(right_No)){
                        if(map.get(right_No)==null){
                            HashSet<String> set =new HashSet<>();
                            set.add(goodsObj.get("cartType")+"&"+goodsId);
                            map.put(right_No,set);
                        }else{
                            HashSet<String> set =map.get(right_No);
                            set.add(goodsObj.get("cartType")+"&"+goodsId);
                            map.put(right_No,set);
                        }
                    }
                }
            }
            else if(goodsObj.get("cartType")==Const.SHOPPING_CLASS_CART_TYPE) {  //艺术课程
                String goodsId = goodsObj.getString("goodsId");
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("goodsId",goodsId);
                reqMap.put("gcId","4");
                reqMap.put("goodsType",4);
                List<ShoppingCoupon> coupons = CommonInit.staticShoppingCouponDao.queryUserArtCouponList(reqMap);
                for(ShoppingCoupon shoppingCoupon:coupons){
                    String right_No =shoppingCoupon.getRight_No();
                    if(userCouponIds.contains(right_No)){
                        if(map.get(right_No)==null){
                            HashSet<String> set =new HashSet<>();
                            set.add(goodsObj.get("cartType")+"&"+goodsId);
                            map.put(right_No,set);
                        }else{
                            HashSet<String> set =map.get(right_No);
                            set.add(goodsObj.get("cartType")+"&"+goodsId);
                            map.put(right_No,set);
                        }
                    }
                }
            }
        }
        List<String> couponIds = new ArrayList<>();
        for(String key:map.keySet()){
            ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
            shoppingCoupon.setRight_No(key);
            shoppingCoupon =CommonInit.staticShoppingCouponDao.queryDetail(shoppingCoupon);
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
            if(shoppingCoupon.getStart_Date()!=null&&StringUtil.compareMillisecond(shoppingCoupon.getStart_Date(),sf)>0){
                continue;
            }else if(shoppingCoupon.getEnd_Date()!=null){
                try {
                    Calendar cd = Calendar.getInstance();
                    cd.setTime(sf.parse(shoppingCoupon.getEnd_Date()));
                    cd.add(Calendar.DATE, 1);//增加n天
                    if(StringUtil.compareMillisecond(sf.format(sf.parse(sf.format(cd.getTime())).getTime()),sf)<0){
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            HashSet<String> goodsSet = map.get(key);
            JSONArray couponGoodsArray = new JSONArray();
            //获取该优惠券可用的商品信息
            for(String str:goodsSet){
                for(int i=0;i<goodsArray.size();i++){
                    JSONObject goodsObj = goodsArray.getJSONObject(i);
                    String goodsId = goodsObj.getString("goodsId");
                    String cartType = goodsObj.getString("cartType");
                    if((cartType+"&"+goodsId).equals(str)){
                        couponGoodsArray.add(goodsObj);
                    }
                }
            }
            BigDecimal payPrice=BigDecimal.ZERO;
            BigDecimal goodsPrice =BigDecimal.ZERO;
            for(int i=0;i<couponGoodsArray.size();i++){
                JSONObject goodsObj = couponGoodsArray.getJSONObject(i);
                payPrice = payPrice.add(goodsObj.getBigDecimal("perPayPrice"));
                goodsPrice = goodsPrice.add(goodsObj.getBigDecimal("perGoodsPrice"));
            }
            //计算额度
            if(shoppingCoupon.getRight_Type().equals("coincp")){  //代金券，在现金的基础上判断满减额度
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&new BigDecimal(shoppingCoupon.getMax_Money()).compareTo(payPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&new BigDecimal(shoppingCoupon.getMin_Money()).compareTo(payPrice)>0){
                    continue;
                }
            }else if(shoppingCoupon.getRight_Type().equals("discount")){  //折扣券，在原价的基础上判断满减额度
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&new BigDecimal(shoppingCoupon.getMax_Money()).compareTo(goodsPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&new BigDecimal(shoppingCoupon.getMin_Money()).compareTo(goodsPrice)>0){
                    continue;
                }
            }
            couponIds.add(shoppingCoupon.getRight_No());
        }

        JSONArray resList = new JSONArray();
        for (int i = 0; i < objList.size(); i++) {
            JSONObject obj =  objList.getJSONObject(i);
            String right_No =  obj.get("right_No").toString();
            if(couponIds.contains(right_No)){
                obj.put("goodsSet",map.get(right_No));
                resList.add(obj);
            }
        }
        return resList;
    }

    /**
     * 获取视频点播可用的优惠券
     *
     */
    public static JSONArray getVideoCouppon(String userId,BigDecimal goodsPrice,BigDecimal payPrice){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user= CommonInit.staticShoppingUserDao.queryDetail(user);

        HashMap<String, Object> reqMap = new HashMap<>();
        List<ShoppingCoupon> coupons = CommonInit.staticShoppingCouponDao.queryUserVideoCouponList(reqMap);
        List<String> couponIds = new ArrayList<>();
        for(ShoppingCoupon shoppingCoupon:coupons){
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
            if(shoppingCoupon.getStart_Date()!=null&&StringUtil.compareMillisecond(shoppingCoupon.getStart_Date(),sf)>0){
                continue;
            }
            if(shoppingCoupon.getEnd_Date()!=null){
                try {
                    Calendar cd = Calendar.getInstance();
                    cd.setTime(sf.parse(shoppingCoupon.getEnd_Date()));
                    cd.add(Calendar.DATE, 1);//增加n天
                    if(StringUtil.compareMillisecond(sf.format(sf.parse(sf.format(cd.getTime())).getTime()),sf)<0){
                        continue;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if(shoppingCoupon.getRight_Type().equals("coincp")){  //代金券，在现金的基础上判断满减额度
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&new BigDecimal(shoppingCoupon.getMax_Money()).compareTo(payPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&new BigDecimal(shoppingCoupon.getMin_Money()).compareTo(payPrice)>0){
                    continue;
                }
            }else if(shoppingCoupon.getRight_Type().equals("discount")){  //折扣券，在原价的基础上判断满减额度
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&new BigDecimal(shoppingCoupon.getMax_Money()).compareTo(goodsPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&new BigDecimal(shoppingCoupon.getMin_Money()).compareTo(goodsPrice)>0){
                    continue;
                }
            }
            couponIds.add(shoppingCoupon.getRight_No());
        }

        JSONArray objList = new JSONArray();
        if(null !=user&&null !=user.getMobile()){
            JSONArray resArray = CRMService.getUserCouponList(userId,user.getMobile(),"0");
            if(null !=resArray){
                for (int i = 0; i < resArray.size(); i++) {
                    JSONObject obj = (JSONObject) resArray.get(i);
                    String right_No =  obj.get("right_No").toString();
                    if(couponIds.contains(right_No)){
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        ShoppingCoupon detail = CommonInit.staticShoppingCouponDao.queryDetail(shoppingCoupon);
                        if(null !=detail){
                            obj.put("detail",detail);
                            objList.add(obj);
                        }
                    }
                }
            }
        }
        return objList;
    }

    /**
     * 获取停车券
     *
     */
    public static JSONArray getParkCouppon(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user= CommonInit.staticShoppingUserDao.queryDetail(user);

        JSONArray objList = new JSONArray();
        if(null !=user&&null !=user.getMobile()){
            JSONArray resArray = CRMService.getUserCouponList(userId,user.getMobile(),"0");

            for (int i = 0; i < resArray.size(); i++) {
                JSONObject obj = (JSONObject) resArray.get(i);
                String right_Type =  obj.get("right_Type").toString();
                String right_No =  obj.get("right_No").toString();
                if(right_Type.equals("停车券")){
                    ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                    shoppingCoupon.setRight_No(right_No);
                    ShoppingCoupon detail = CommonInit.staticShoppingCouponDao.queryDetail(shoppingCoupon);
                    if(null !=detail){
                        obj.put("detail",detail);
                        objList.add(obj);
                    }

                }
            }
        }
        return objList;
    }

    /**
     * 获取用户已经锁定的优惠券id
     *
     */
    public static List<String> getUserLockCoupon(String userId){
        List<String> ids=new ArrayList<>();
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("userId",userId);
        List<ShoppingCouponUsertemp> list= CommonInit.staticShoppingCouponUsertempDao.queryList(reqMap);
        for(ShoppingCouponUsertemp shoppingCouponUsertemp:list){
            ids.add(shoppingCouponUsertemp.getCouponId());
        }
        return ids;
    }

    /**
     * 获取用户会员权益
     *
     */
    public static BigDecimal getUserMemberShip(String userId){
        ShoppingUser user = new ShoppingUser();
        user.setId(userId);
        user=CommonInit.staticShoppingUserDao.queryDetail(user);
        String levelname = user.getLevelname();
        ShoppingMembership membership = new ShoppingMembership();
        membership = CommonInit.staticShoppingMembershipDao.queryDetail(membership);
        if(null==levelname||"".equals(levelname)){
            return membership.getNormal();
        }else{
            switch (levelname) {
                case "普卡":
                    return membership.getNormal();
                case "银卡":
                    return membership.getSilver();
                case "金卡":
                    return membership.getGold();
                case "钻卡":
                    return membership.getDiamond();
                default:
                    return membership.getNormal();
            }
        }

    }

    /**
     * 获取积分赠送设置
     *
     */
    public static ShoppingIntegralSet getIntegralSet(String name){
        ShoppingIntegralSet shoppingIntegralSet = new ShoppingIntegralSet();
        shoppingIntegralSet.setName(name);
        shoppingIntegralSet=CommonInit.staticShoppingIntegralSetDao.queryDetail(shoppingIntegralSet);
        return shoppingIntegralSet;
    }

    /**
     * 保存第三方请求日志
     *
     */
    public static void addThirdLog(int status,int logType,String logInfo,String reqmethod,String serverpath,String reqtime,
                                   String reqinfo,String rettime,String retinfo){
        TLmThirdlog thirdlog = new TLmThirdlog();
        thirdlog.setLogtype(logType);
        thirdlog.setLoginfo(logInfo);
        thirdlog.setLogip(StringUtil.getLocalIp());
        thirdlog.setReqmethod(reqmethod);
        thirdlog.setServerpath(serverpath);
        thirdlog.setReqtime(reqtime);
        thirdlog.setReqinfo(reqinfo);
        thirdlog.setRettime(rettime);
        thirdlog.setRetinfo(retinfo);
        thirdlog.setStatus(status);
        CommonInit.staticTLmThirdlogDao.insert(thirdlog);
    }

    /**
     * 获取用户会员权益
     *
     */
    public static FUserinfo getFUserInfo(String userId){
        FUserinfo user = new FUserinfo();
        user.setUserCode(userId);
        user=CommonInit.staticFUserinfoDao.queryDetail(user);
        return user;

    }

    /**
     * 获取支付限额配置
     *
     */
    public static ShoppingPayLimit getPayLimit(){
        HashMap<String, Object> reqMap = new HashMap<>();
        List<ShoppingPayLimit> limits= CommonInit.staticShoppingPayLimitDao.queryList(reqMap);
        if(limits.size()==1){
            return limits.get(0);
        }
        return null;

    }

    /**
     * 获取会员资产限额规则
     *
     */
    public static ShoppingAssetRule getAssetRule(){
        HashMap<String, Object> reqMap = new HashMap<>();
        List<ShoppingAssetRule> limits= CommonInit.staticShoppingAssetRuleDao.queryList(reqMap);
        if(limits.size()==1){
            return limits.get(0);
        }
        return null;

    }

    /**
     * 获取诺诺发票平台accessToken
     *
     */
    public static String  getNNAccessToken(){
        TInvoiceToken invoiceToken = new TInvoiceToken();
        invoiceToken.setAppKey(Const.INVOICE_APPKEY);
        invoiceToken.setAppSecret(Const.INVOICE_APPSECRET);
        invoiceToken = CommonInit.staticTInvoiceTokenDao.queryDetail(invoiceToken);
        if(null ==invoiceToken){
            String accessToken = NNService.getAccessToken();
            invoiceToken = new TInvoiceToken();
            invoiceToken.setAppKey(Const.INVOICE_APPKEY);
            invoiceToken.setAppSecret(Const.INVOICE_APPSECRET);
            invoiceToken.setAccessToken(accessToken);
            invoiceToken.setExpiresTime(StringUtil.nowTimePlusHours(23));   //当前时间加上23小时作为失效时间
            CommonInit.staticTInvoiceTokenDao.insert(invoiceToken);
            return accessToken;
        }else{
            String accessToken =invoiceToken.getAccessToken();
            String expiresTime = invoiceToken.getExpiresTime();
            if(StringUtil.isNotNull(accessToken)&&StringUtil.isNotNull(expiresTime)&&StringUtil.differentMinutesByMillisecond(expiresTime)<=0){
                return accessToken;
            }else{
                accessToken = NNService.getAccessToken();
                invoiceToken.setAccessToken(accessToken);
                invoiceToken.setExpiresTime(StringUtil.nowTimePlusHours(23));   //当前时间加上23小时作为失效时间
                CommonInit.staticTInvoiceTokenDao.update(invoiceToken);
                return accessToken;
            }
        }
    }

    /**
     * 获取文创赠送积分赠送开关
     *
     */
    public static Boolean getShoppingIntegralSwitch(){
        HashMap<String, Object> reqMap = new HashMap<>();
        List<ShoppingIntegralTotal> limits= CommonInit.staticShoppingIntegralTotalDao.queryList(reqMap);
        if(limits.size()==1){
            ShoppingIntegralTotal shoppingIntegralTotal= limits.get(0);
            if(shoppingIntegralTotal.getShoppingIntegralSwitch().equals("1"))
                return true;
        }
        return false;

    }

    /**
     * 获取数据字典值
     *
     */
    public static FDatadictionary getFDatadictionary(String catalogCode,String dataCode){
        FDatadictionary fDatadictionary =new FDatadictionary();
        fDatadictionary.setCatalogCode(catalogCode);
        fDatadictionary.setDataCode(dataCode);
        fDatadictionary=CommonInit.staticFDatadictionaryDao.queryDetail(fDatadictionary);
        return fDatadictionary;

    }

    public static ShoppingSysconfig getSysConfig() {
        List configs = CommonInit.staticShoppingSysconfigDao.queryList(new HashMap<>());
        if ((configs != null) && (configs.size() > 0)) {
            ShoppingSysconfig sc = (ShoppingSysconfig) configs.get(0);
            if (sc.getUploadFilePath() == null) {
                sc.setUploadFilePath("upload");
            }
            if (sc.getSysLanguage() == null) {
                sc.setSysLanguage("zh_cn");
            }
            if ((sc.getWebsiteName() == null) || ("".equals(sc.getWebsiteName()))) {
                sc.setWebsiteName("shopping");
            }
            if ((sc.getCloseReason() == null) || ("".equals(sc.getCloseReason()))) {
                sc.setCloseReason("系统维护中...");
            }
            if ((sc.getTitle() == null) || ("".equals(sc.getTitle()))) {
                sc.setTitle("shopping多用户商城系统V2.0版");
            }
            if ((sc.getImageSaveType() == null) ||
                    ("".equals(sc.getImageSaveType()))) {
                sc.setImageSaveType("sidImg");
            }

            if (sc.getImageFilesize() == 0) {
                sc.setImageFilesize(1024);
            }
            if (sc.getSmallWidth() == 0) {
                sc.setSmallWidth(160);
            }
            if (sc.getSmallHeight() == 0) {
                sc.setSmallHeight(160);
            }
            if (sc.getMiddleWidth() == 0) {
                sc.setMiddleWidth(300);
            }
            if (sc.getMiddleHeight() == 0) {
                sc.setMiddleHeight(300);
            }
            if (sc.getBigHeight() == 0) {
                sc.setBigHeight(1024);
            }
            if (sc.getBigWidth() == 0) {
                sc.setBigWidth(1024);
            }
            if ((sc.getImageSuffix() == null) || ("".equals(sc.getImageSuffix()))) {
                sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
            }

//            if (sc.getStoreImage() == null) {
//                Accessory storeImage = new Accessory();
//                storeImage.setPath("resources/style/common/images");
//                storeImage.setName("store.jpg");
//                sc.setStoreImage(storeImage);
//            }
//            if (sc.getGoodsImage() == null) {
//                Accessory goodsImage = new Accessory();
//                goodsImage.setPath("resources/style/common/images");
//                goodsImage.setName("good.jpg");
//                sc.setGoodsImage(goodsImage);
//            }
//            if (sc.getMemberIcon() == null) {
//                Accessory memberIcon = new Accessory();
//                memberIcon.setPath("resources/style/common/images");
//                memberIcon.setName("member.jpg");
//                sc.setMemberIcon(memberIcon);
//            }
            if ((sc.getSecurityCodeType() == null) ||
                    ("".equals(sc.getSecurityCodeType()))) {
                sc.setSecurityCodeType("normal");
            }
            if ((sc.getWebsiteCss() == null) || ("".equals(sc.getWebsiteCss()))) {
                sc.setWebsiteCss("default");
            }
            return sc;
        }
        ShoppingSysconfig sc = new ShoppingSysconfig();
        sc.setUploadFilePath("upload");
        sc.setWebsiteName("shopping");
        sc.setSysLanguage("zh_cn");
        sc.setTitle("shopping多用户商城系统V2.0版");
        sc.setSecurityCodeType("normal");
        sc.setEmailEnable(true);
        sc.setCloseReason("系统维护中...");
        sc.setImageSaveType("sidImg");
        sc.setImageFilesize(1024);
        sc.setSmallWidth(160);
        sc.setSmallHeight(160);
        sc.setMiddleHeight(300);
        sc.setMiddleWidth(300);
        sc.setBigHeight(1024);
//     sc.setFenxiao_type(2);
        sc.setBigWidth(1024);
        sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
//        sc.setComplaint_time(30);
        sc.setWebsiteCss("default");

//        Accessory goodsImage = new Accessory();
//        goodsImage.setPath("resources/style/common/images");
//        goodsImage.setName("good.jpg");
//        sc.setGoodsImage(goodsImage);
//        Accessory storeImage = new Accessory();
//        storeImage.setPath("resources/style/common/images");
//        storeImage.setName("store.jpg");
//        sc.setStoreImage(storeImage);
//        Accessory memberIcon = new Accessory();
//        memberIcon.setPath("resources/style/common/images");
//        memberIcon.setName("member.jpg");
//        sc.setMemberIcon(memberIcon);
        return sc;
    }
}