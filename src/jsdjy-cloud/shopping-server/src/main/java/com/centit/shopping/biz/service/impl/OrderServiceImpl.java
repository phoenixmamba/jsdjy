package com.centit.shopping.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.OrderService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.CommonInit;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.MZService;
import com.centit.shopping.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>订单管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-25
 **/
@Transactional
@Service
public class OrderServiceImpl implements OrderService {
    public static final Log log = LogFactory.getLog(OrderService.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;

    @Resource
    private TicketExchangePlaceDao ticketExchangePlaceDao;

    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;

    @Resource
    private ShoppingEvaluateDao shoppingEvaluateDao;

    @Resource
    private ShoppingEvaluatePhotoDao shoppingEvaluatePhotoDao;

    @Resource
    private ShoppingRefundDao shoppingRefundDao;

    @Resource
    private ShoppingRefundPhotoDao shoppingRefundPhotoDao;

    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;

    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;

    @Resource
    private ShoppingRechargeDao shoppingRechargeDao;

    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;

    @Resource
    private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    @Resource
    private ShoppingIntegralRecordDao shoppingIntegralRecordDao;

    @Resource
    private ShoppingIntegralTotalDao shoppingIntegralTotalDao;

    @Resource
    private ShoppingAssetDao shoppingAssetDao;

    @Value("${order.orderState.cancel}")
    private int orderStateCancel;

    @Value("${order.orderState.hasSend}")
    private int orderStateSend;

    @Value("${order.orderState.toRefund}")
    private int orderStateToRefund;

    @Value("${order.orderState.hasDone}")
    private int orderStateDone;

    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;

    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;

    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY = "REDIS_KEY:STOCK:";
    public static final String REDIS_KEY_GOODS = "REDIS_KEY:STOCK:GOODS";
    public static final String REDIS_KEY_PLAN = "REDIS_KEY:STOCK:PLAN";
    @Resource
    private RedisStockService redisStockService;

    /**
     * 查询订单分页列表
     */
    @Override
    public JSONObject queryPageList(JSONObject reqJson) {
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
            reqMap.put("deleteStatus","0");

            JSONArray objArray = new JSONArray();
            if(StringUtil.isNotNull(reqMap.get("userId"))){
                bizDataJson.put("total",shoppingOrderformDao.queryTotalCount(reqMap));
                List<ShoppingOrderform> orderList = shoppingOrderformDao.queryList(reqMap);

                for(ShoppingOrderform shoppingOrderform:orderList){
                    JSONObject obj = new JSONObject();
                    //订单基本信息
                    obj.put("id",shoppingOrderform.getId());   //订单id
                    obj.put("orderId",shoppingOrderform.getOrderId());   //订单id
                    obj.put("orderTime",shoppingOrderform.getAddTime()); //订单时间，取下单时间
                    obj.put("orderType",shoppingOrderform.getOrderType());   //订单类型
                    obj.put("orderStatus",shoppingOrderform.getOrderStatus());   //订单状态
//                obj.put("transport",shoppingOrderform.getTransport());   //快递/自提
                    obj.put("shipPrice",shoppingOrderform.getShipPrice());   //订单运费
                    obj.put("totalPrice",shoppingOrderform.getTotalPrice());  //订单总价
                    obj.put("deductionMemberPrice",shoppingOrderform.getDeductionMemberPrice()); //会员权益抵扣
                    obj.put("deductionCouponPrice",shoppingOrderform.getDeductionCouponPrice()); //优惠券抵扣
                    obj.put("deductionIntegral",shoppingOrderform.getDeductionIntegral()); //积分抵扣值
                    obj.put("deductionIntegralPrice",shoppingOrderform.getDeductionIntegralPrice()); //积分抵扣金额
                    obj.put("deductionBalancePrice",shoppingOrderform.getDeductionBalancePrice()); //余额抵扣
                    obj.put("payPrice",shoppingOrderform.getPayPrice()); //现金支付
                    obj.put("storeId",shoppingOrderform.getStoreId()); //商户id
                    if(null !=shoppingOrderform.getStoreId()){
                        obj.put("storeName",CommonUtil.getStoreInfo(shoppingOrderform.getStoreId()).getStoreName()); //商户名称
                    }

                    //支付状态
                    ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                    shoppingOrderPay.setOfId(shoppingOrderform.getId());
                    shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
                    if(null !=shoppingOrderPay){
                        obj.put("cashStatus",shoppingOrderPay.getCashStatus()); //现金支付状态
                        obj.put("integralStatus",shoppingOrderPay.getIntegralStatus()); //积分支付状态
                        obj.put("balanceStatus",shoppingOrderPay.getBalanceStatus()); //余额支付状态
                    }


                    //订单关联商品
                    obj.put("orderGoods",CommonUtil.getOrderGoods(shoppingOrderform.getId()));
                    objArray.add(obj);

                    //是否可确认收货
                    obj.put("toConfirmReceipt",false);
                    if(shoppingOrderform.getOrderStatus()==orderStateSend){
                        obj.put("toConfirmReceipt",true);
                    }

                    //是否可评价
                    obj.put("toEvaluate",false);
                    if(shoppingOrderform.getOrderStatus()==orderStateDone){   //只有状态为已完成的订单才可以评价
                        List<HashMap<String,Object>> orderGoodsList= CommonUtil.getOrderGoods(shoppingOrderform.getId());
                        boolean isGood = false;
                        for(HashMap<String,Object> goods:orderGoodsList){
                            if(goods.get("goodsType")==Const.SHOPPING_CUL_CART_TYPE||goods.get("goodsType")==Const.SHOPPING_INT_CART_TYPE){
                                isGood = true;
                                break;
                            }
                        }
                        //只有文创或积分商品才可以评价
                        if(isGood){
                            reqMap.clear();
                            reqMap.put("deleteStatus","0");
                            reqMap.put("ofId",shoppingOrderform.getId());
                            if(shoppingEvaluateDao.queryList(reqMap).isEmpty()){
                                obj.put("toEvaluate",true); //可评价
                            }
                        }
                    }

                }
            }else{
                bizDataJson.put("total",0);
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
     * @Description 获取订单推荐商品
     **/
    @Override
    public JSONObject orderRecGoods(String orderId) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setOrderId(orderId);
            shoppingOrderform = shoppingOrderformDao.queryDetailByOrderId(shoppingOrderform);

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("ofId",shoppingOrderform.getId());
            List<HashMap<String, Object>> mapList = shoppingOrderformDao.queryRecList(reqMap);
            JSONArray resArray = new JSONArray();
            for (HashMap<String, Object> objMap : mapList) {
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", objMap.get("goods_id"));  //商品id
                resObj.put("goodsType", objMap.get("type"));  //
                resObj.put("goodsName", objMap.get("goodsName"));  //商品名称
                resObj.put("photoId", objMap.get("photoId"));  //商品图id
                resObj.put("goodsPrice", objMap.get("goodsPrice"));  //商品价格
                //付款人数，目前简单计算为该商品的订单数
                reqMap.clear();
                reqMap.put("deleteStatus", "0");
                reqMap.put("goodsId", objMap.get("goods_id"));
                resObj.put("orderCount", shoppingOrderformDao.queryTotalCount(reqMap));

                resArray.add(resObj);
            }
            bizDataJson.put("objList",resArray);

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
     * @Description 获取订单详情
     **/
    @Override
    public JSONObject orderDetail(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {

            //查询订单主体信息
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setOrderId(id);
            shoppingOrderform = shoppingOrderformDao.queryDetailByOrderId(shoppingOrderform);

            if(null ==shoppingOrderform){
                shoppingOrderform = new ShoppingOrderform();
                shoppingOrderform.setId(id);
                shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
            }

            String mzUserId= CommonUtil.getMzUserId(shoppingOrderform.getUserId());
            //物流信息
            if(null !=shoppingOrderform.getShipCode()&&!"".equals(shoppingOrderform.getShipCode())){
//                JSONObject js = CommonUtil.queryTransportInfo(shoppingOrderform.getEcId(),shoppingOrderform.getShipCode());
//                bizDataJson.put("orderTransportInfo",js);
                JSONArray transportInfoArray = new JSONArray();
                String ecIdStr = shoppingOrderform.getEcId();
                String shipCodeStr = shoppingOrderform.getShipCode();
                String ecvalueStr = shoppingOrderform.getecvalue();
                String[] ecIds = ecIdStr.split(";");
                String[] shipCodes = shipCodeStr.split(";");
                String[] ecvalues = ecvalueStr.split(";");
                for(int i=0;i<ecIds.length;i++){
                    String ecId = ecIds[i];
                    String shipCode = shipCodes[i];
                    String ecvalue = ecvalues[i];
                    String receiver_phone =null;
                    if(ecId.equals("1")){
                        JSONObject addressObj = MZService.getAddressDetail(mzUserId,shoppingOrderform.getAddrId());
                        receiver_phone = addressObj.getString("receiver_phone");
                    }
                    JSONObject js = CommonUtil.queryTransportInfo(ecId,shipCode,receiver_phone);
                    js.put("orderId",shoppingOrderform.getOrderId());  //订单编号
                    js.put("shipCode",shipCode);  //物流单号
                    js.put("ecvalue",ecvalue);  //快递公司名称
                    transportInfoArray.add(js);
                }
                bizDataJson.put("orderTransportInfo",transportInfoArray);
            }

            //收货人信息
            if(null !=shoppingOrderform.getAddrId()&&!"".equals(shoppingOrderform.getAddrId())){
//                bizDataJson.put("orderAddrInfo",CommonUtil.getShoppingAddress(shoppingOrderform.getAddrId()));
                JSONObject addressObj = MZService.getAddressDetail(mzUserId,shoppingOrderform.getAddrId());
                bizDataJson.put("orderAddrInfo",addressObj);
            }

            //商品信息
            bizDataJson.put("orderGoods",CommonUtil.getOrderGoods(shoppingOrderform.getId()));

            //店铺名称
            bizDataJson.put("storeName",CommonUtil.getStoreInfo(shoppingOrderform.getStoreId()).getStoreName());

            //费用信息
            JSONObject priceObj = new JSONObject();
            priceObj.put("shipPrice",shoppingOrderform.getShipPrice());   //订单运费
            priceObj.put("totalPrice",shoppingOrderform.getTotalPrice());  //订单总价
            priceObj.put("deductionMemberPrice",shoppingOrderform.getDeductionMemberPrice()); //会员权益抵扣
            priceObj.put("deductionCouponPrice",shoppingOrderform.getDeductionCouponPrice()); //优惠券抵扣
            priceObj.put("deductionIntegral",shoppingOrderform.getDeductionIntegral()); //积分抵扣值
            priceObj.put("deductionIntegralPrice",shoppingOrderform.getDeductionIntegralPrice()); //积分抵扣金额
            priceObj.put("deductionBalancePrice",shoppingOrderform.getDeductionBalancePrice()); //余额抵扣
            priceObj.put("payPrice",shoppingOrderform.getPayPrice()); //现金支付
            //支付状态
            ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
            shoppingOrderPay.setOfId(shoppingOrderform.getId());
            shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
            if(null !=shoppingOrderPay){
                priceObj.put("cashStatus",shoppingOrderPay.getCashStatus()); //现金支付状态
                priceObj.put("integralStatus",shoppingOrderPay.getIntegralStatus()); //积分支付状态
                priceObj.put("balanceStatus",shoppingOrderPay.getBalanceStatus()); //余额支付状态
            }

            bizDataJson.put("priceInfo",priceObj);

            //订单信息
            JSONObject orderObj = new JSONObject();
            orderObj.put("ofId",shoppingOrderform.getId());  //订单唯一标识
            orderObj.put("orderStatus",shoppingOrderform.getOrderStatus());  //订单状态
            orderObj.put("orderId",shoppingOrderform.getOrderId());  //订单编号
            orderObj.put("outOrderId",shoppingOrderform.getOutOrderId());  //第三方支付交易号
            orderObj.put("addTime",shoppingOrderform.getAddTime());  //创建时间
            orderObj.put("payTime",shoppingOrderform.getPayTime());  //付款时间
            orderObj.put("shipTime",shoppingOrderform.getShipTime());  //发货时间
            orderObj.put("finishTime",shoppingOrderform.getFinishTime());  //完成时间
            bizDataJson.put("orderInfo",orderObj);

            //是否可确认收货
            bizDataJson.put("toConfirmReceipt",false);
            if(shoppingOrderform.getOrderStatus()==orderStateSend){
                bizDataJson.put("toConfirmReceipt",true);
            }

            //是否可评价
            bizDataJson.put("toEvaluate",false);
            if(shoppingOrderform.getOrderStatus()==orderStateDone){   //只有状态为已完成的订单才可以评价
                List<HashMap<String,Object>> orderGoodsList= CommonUtil.getOrderGoods(shoppingOrderform.getId());
                boolean isGood = false;
                for(HashMap<String,Object> goods:orderGoodsList){
                    if(goods.get("goodsType")==Const.SHOPPING_CUL_CART_TYPE||goods.get("goodsType")==Const.SHOPPING_INT_CART_TYPE){
                        isGood = true;
                        break;
                    }
                }
                //只有文创或积分商品才可以评价
                if(isGood){
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("deleteStatus","0");
                    reqMap.put("ofId",shoppingOrderform.getId());
                    if(shoppingEvaluateDao.queryList(reqMap).isEmpty()){
                        bizDataJson.put("toEvaluate",true); //可评价
                    }
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
     * @Description 获取麦座订单详情
     **/
    @Override
    public JSONObject orderMzDetail(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //查询订单主体信息
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setOrderId(id);
            shoppingOrderform = shoppingOrderformDao.queryDetailByOrderId(shoppingOrderform);

            if(null ==shoppingOrderform){
                shoppingOrderform = new ShoppingOrderform();
                shoppingOrderform.setId(id);
                shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
            }
            String outOrderId = shoppingOrderform.getOutOrderId();
            String mzUserId= CommonUtil.getMzUserId(shoppingOrderform.getUserId());
            JSONObject dataObj = MZService.getMzOrderDetail(mzUserId,outOrderId);
            dataObj.put("ofId",shoppingOrderform.getId());
            if(null !=dataObj){
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("ofId",shoppingOrderform.getId());
                reqMap.put("deleteStatus","0");
                List<ShoppingGoodscart> cartGoods = CommonInit.staticShoppingGoodscartDao.queryList(reqMap);

                //查询该场次取票点信息
                HashMap<String,Object> placeMap = new HashMap<>();
                placeMap.put("eventId",cartGoods.get(0).getGoodsId());
                List<TicketExchangePlace> places = ticketExchangePlaceDao.queryList(placeMap);

                if(null !=dataObj.get("order_receive_info")){
                    JSONObject order_receive_info = dataObj.getJSONObject("order_receive_info");
                    order_receive_info.put("places",places);
                    dataObj.put("order_receive_info",order_receive_info);
                }
                dataObj.put("orderStatus",shoppingOrderform.getOrderStatus());
                bizDataJson.put("orderInfo",dataObj);
            }else{
                retCode = "1";
                retMsg = "获取麦座订单详情失败，请稍后再试！";
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
     * 取消订单
     */
    @Override
    public JSONObject cancelOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));
            //userId
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            //订单id
            String id = reqJson.getString("ofId");
            //取消原因
            String reason = reqJson.getString("reason");
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(id);
            //查询订单主体信息
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);

            //如果是演出票订单，需要调麦座的取消订单接口
            if(shoppingOrderform.getOrderId().startsWith(Const.TICKET_ORDER)){
                String mz_order_id = shoppingOrderform.getOutOrderId();  //麦座订单号
//                if(MZService.cancelOrder(mzUserId,mz_order_id,reason)){
//
//                }
//                else{
//                    retMsg = "调用麦座接口取消订单失败！";
//                }
                MZService.cancelOrder(mzUserId,mz_order_id,reason);
                // 添加订单日志
                ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                shoppingOrderLog.setLogInfo("取消订单");
                shoppingOrderLog.setStateInfo(reason);
                shoppingOrderLog.setLogUserId(userId);
                shoppingOrderLog.setOfId(shoppingOrderform.getId());
                shoppingOrderLogDao.insert(shoppingOrderLog);

                // 取消订单
                shoppingOrderformDao.cancelOrder(shoppingOrderform);
            }else{
                //如果订单使用了优惠券，需要解除优惠券的绑定状态
                if (null != shoppingOrderform.getCiId()) {
                    ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
                    shoppingCouponUsertemp.setUserId(shoppingOrderform.getUserId());
                    shoppingCouponUsertemp.setCouponId(shoppingOrderform.getCiId());
                    shoppingCouponUsertempDao.delete(shoppingCouponUsertemp);
                }

                //如果是充值订单，需要同时关闭充值记录表中的记录
                if(shoppingOrderform.getOrderId().startsWith(Const.RECHARGE_ORDER)){
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("status",2);  //交易关闭
                    reqMap.put("ofId",shoppingOrderform.getId());
                    reqMap.put("userId",shoppingOrderform.getUserId());
                    shoppingRechargeDao.setRechargeStatus(reqMap);
                }

                //更新商品库存
                //获取订单关联物品
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("ofId",id);
                reqMap.put("deleteStatus","0");
                List<ShoppingGoodscart> cartGoods = CommonInit.staticShoppingGoodscartDao.queryList(reqMap);
                for(ShoppingGoodscart shoppingGoodscart:cartGoods){
                    if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_CUL_CART_TYPE)||shoppingGoodscart.getCartType().equals(Const.SHOPPING_INT_CART_TYPE))){
                        ShoppingGoods shoppingGoods = new ShoppingGoods();
                        shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                        //商品主体信息
                        shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                        if(shoppingGoodscart.getPropertys()!=null&&!shoppingGoodscart.getPropertys().equals("")){
                            ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                            shoppingGoodsInventory.setGoodsId(shoppingGoodscart.getGoodsId());
                            shoppingGoodsInventory.setPropertys(shoppingGoodscart.getPropertys());

                            shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                            int inventoryCount = shoppingGoodsInventory.getCount();
                            shoppingGoodsInventory.setCount(inventoryCount + shoppingGoodscart.getCount());
                            shoppingGoodsInventoryDao.update(shoppingGoodsInventory);

                            String key = REDIS_KEY_GOODS + shoppingGoods.getId()+shoppingGoodscart.getPropertys();
                            int currentStock= redisStockService.currentStock(key);
                            redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());
                        }
                        shoppingGoods.setGoodsInventory(shoppingGoods.getGoodsInventory() + shoppingGoodscart.getCount());
                        shoppingGoodsDao.updateGoodsInventory(shoppingGoods);
                        String key = REDIS_KEY_GOODS + shoppingGoods.getId();
                        int currentStock= redisStockService.currentStock(key);
                        redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());
                    }
                    if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_ACT_CART_TYPE))){
                        ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                        shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                        //活动主体信息
                        shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                        shoppingArtactivity.setLeftnum(shoppingArtactivity.getLeftnum() + shoppingGoodscart.getCount());
                        shoppingArtactivityDao.updateActivityNum(shoppingArtactivity);
                        String key = REDIS_KEY + shoppingArtactivity.getId();
                        int currentStock= redisStockService.currentStock(key);
                        redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());

                        if(shoppingGoodscart.getPropertys()!=null&&!shoppingGoodscart.getPropertys().equals("")){
                            ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                            shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                            shoppingArtactivityInventory.setPropertys(shoppingGoodscart.getPropertys());
                            shoppingArtactivityInventory = shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                            int inventoryCount = shoppingArtactivityInventory.getCount();
                            shoppingArtactivityInventory.setCount(inventoryCount + shoppingGoodscart.getCount());
                            shoppingArtactivityInventoryDao.update(shoppingArtactivityInventory);

                            key = REDIS_KEY + shoppingArtactivity.getId()+shoppingGoodscart.getPropertys();
                            currentStock= redisStockService.currentStock(key);
                            redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());
                        }
                    }
                    if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_PLAN_CART_TYPE))){
                        ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                        shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                        //爱艺计划主体信息
                        shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                        shoppingArtplan.setLeftnum(shoppingArtplan.getLeftnum() + shoppingGoodscart.getCount());
                        shoppingArtplanDao.updatePlanNum(shoppingArtplan);
                        String key = REDIS_KEY_PLAN + shoppingArtplan.getId();
                        int currentStock= redisStockService.currentStock(key);
                        redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());

                        if(shoppingGoodscart.getPropertys()!=null&&!shoppingGoodscart.getPropertys().equals("")){
                            ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                            shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                            shoppingArtplanInventory.setPropertys(shoppingGoodscart.getPropertys());
                            shoppingArtplanInventory = shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                            int inventoryCount = shoppingArtplanInventory.getCount();
                            shoppingArtplanInventory.setCount(inventoryCount + shoppingGoodscart.getCount());
                            shoppingArtplanInventoryDao.update(shoppingArtplanInventory);

                            key = REDIS_KEY_PLAN + shoppingArtplan.getId()+shoppingGoodscart.getPropertys();
                            currentStock= redisStockService.currentStock(key);
                            redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());
                        }
                    }
                }


                // 添加订单日志
                ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                shoppingOrderLog.setLogInfo("取消订单");
                shoppingOrderLog.setStateInfo(reason);
                shoppingOrderLog.setLogUserId(userId);
                shoppingOrderLog.setOfId(shoppingOrderform.getId());
                shoppingOrderLogDao.insert(shoppingOrderLog);

                // 取消订单
                shoppingOrderform.setOrderStatus(orderStateCancel);
                shoppingOrderformDao.cancelOrder(shoppingOrderform);
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
     * 确认收货
     */
    @Override
    public JSONObject confirmReceipt(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));
            //userId
            String userId = reqJson.getString("userId");
//            String mzUserId = CommonUtil.getMzUserId(userId);
            //订单id
            String id = reqJson.getString("ofId");

            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(id);
//            //查询订单主体信息
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);

            // 添加订单日志
            ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
            shoppingOrderLog.setLogInfo("确认收货");;
            shoppingOrderLog.setLogUserId(userId);
            shoppingOrderLog.setOfId(shoppingOrderform.getId());
            shoppingOrderLogDao.insert(shoppingOrderLog);

            // 确认收货
            shoppingOrderform.setOrderStatus(orderStateDone);
            shoppingOrderformDao.confimOrder(shoppingOrderform);

            if(CommonUtil.getShoppingIntegralSwitch()){
                //如果赠送积分的开关打开了，则需要根据实际支付的金额赠送积分
                BigDecimal payPrice = shoppingOrderform.getPayPrice();
                int integralValue = payPrice.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();  //按付款金额1:1赠送积分

                if(integralValue>0){
                    List<ShoppingIntegralTotal> totalList = shoppingIntegralTotalDao.queryList(new HashMap<>());
                    int total = totalList.isEmpty() ? 0 : totalList.get(0).getDailyTotalIntegral();
                    //获取当天已赠送的积分总数
                    String dayStr = StringUtil.nowDateString();
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("dayStr", dayStr);
                    reqMap.put("userId",userId);
                    int dailySum = shoppingIntegralRecordDao.queryDailySum(reqMap);
                    if (dailySum < total||total==0) {
                        if (total>0&&dailySum + integralValue > total) {
                            integralValue = total - dailySum;
                        }

                        ShoppingAsset shoppingAsset = new ShoppingAsset();
                        shoppingAsset.setUserId(userId);
                        shoppingAsset = shoppingAssetDao.queryDetail(shoppingAsset);
                        if (shoppingAsset == null) {
                            shoppingAsset = new ShoppingAsset();
                            shoppingAsset.setUserId(userId);
                            shoppingAsset.setIntegralValue(integralValue);
                            shoppingAssetDao.insert(shoppingAsset);
                        } else {
                            shoppingAsset.setIntegralValue(shoppingAsset.getIntegralValue() + integralValue);
                            shoppingAssetDao.update(shoppingAsset);
                        }

                        //用户积分新增记录
                        ShoppingIntegralRecord shoppingIntegralRecord = new ShoppingIntegralRecord();
                        shoppingIntegralRecord.setUserId(userId);
                        shoppingIntegralRecord.setIntegralCount(integralValue);
                        shoppingIntegralRecord.setRemark("购买文创商品");
                        shoppingIntegralRecordDao.insert(shoppingIntegralRecord);
                    }
                }

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
     * @Description 获取订单待评价商品列表
     **/
    @Override
    public JSONObject orderEvaluateGoods(String ofId) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            bizDataJson.put("objList",CommonUtil.getOrderEvaluateGoods(ofId));
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
     * 评价
     */
    @Override
    public JSONObject evaluateOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            JSONArray evaluateList = reqJson.getJSONArray("evaluates");
            for(int i=0;i<evaluateList.size();i++){
                JSONObject evaluateObj = evaluateList.getJSONObject(i);
                ShoppingEvaluate shoppingEvaluate = JSON.parseObject(evaluateObj.toJSONString(), ShoppingEvaluate.class);
                shoppingEvaluateDao.insert(shoppingEvaluate);
                if(null !=evaluateObj.get("photos")){
                    JSONArray photoArray = evaluateObj.getJSONArray("photos");
                    for(int j=0;j<photoArray.size();j++){
                        JSONObject photoObj = photoArray.getJSONObject(j);
                        ShoppingEvaluatePhoto shoppingEvaluatePhoto = JSON.parseObject(photoObj.toJSONString(), ShoppingEvaluatePhoto.class);
                        shoppingEvaluatePhoto.setEvaluateId(shoppingEvaluate.getId());
                        shoppingEvaluatePhotoDao.insert(shoppingEvaluatePhoto);
                    }
                }

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
     * 发起退款
     */
    @Override
    public JSONObject addRefund(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRefund shoppingRefund = JSON.parseObject(reqJson.toJSONString(), ShoppingRefund.class);

            ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
            shoppingGoodscart.setId(shoppingRefund.getGcId());
            shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);
            String ofId = shoppingGoodscart.getOfId();
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(ofId);
            shoppingOrderform =shoppingOrderformDao.queryDetail(shoppingOrderform);
            shoppingRefund.setOfId(ofId);
            shoppingRefund.setPreOrderStatus(shoppingOrderform.getOrderStatus());
            shoppingRefundDao.insert(shoppingRefund);
            if(null !=reqJson.get("photos")){
                JSONArray photoArray = reqJson.getJSONArray("photos");
                for(int j=0;j<photoArray.size();j++){
                    JSONObject photoObj = photoArray.getJSONObject(j);
                    ShoppingRefundPhoto shoppingEvaluatePhoto = JSON.parseObject(photoObj.toJSONString(), ShoppingRefundPhoto.class);
                    shoppingEvaluatePhoto.setRefundId(shoppingRefund.getId());
                    shoppingRefundPhotoDao.insert(shoppingEvaluatePhoto);
                }
            }



            shoppingOrderform.setOrderStatus(orderStateToRefund);
            shoppingOrderformDao.update(shoppingOrderform);
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
     * 查询我的退款列表
     */
    @Override
    public JSONObject queryMyRefundPageList(JSONObject reqJson) {
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

            reqMap.put("deleteStatus","0");
            bizDataJson.put("total",shoppingRefundDao.queryUserRefundCount(reqMap));
            List<ShoppingRefund> shoppingRefundList= shoppingRefundDao.queryUserRefundList(reqMap);

            JSONArray resArray = new JSONArray();
            for(ShoppingRefund shoppingRefund:shoppingRefundList){
                JSONObject obj = new JSONObject();
                obj.put("refundId",shoppingRefund.getId());
                String gcId = shoppingRefund.getGcId();
                ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
                shoppingGoodscart.setId(gcId);
                shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);
                //订单信息
                ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
                shoppingOrderform.setId(shoppingGoodscart.getOfId());
                shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
                obj.put("ofId",shoppingOrderform.getId());
                obj.put("orderId",shoppingOrderform.getOrderId());    //订单编号
                obj.put("orderTime",shoppingOrderform.getAddTime());  //下单时间
                obj.put("totalPrice",shoppingOrderform.getTotalPrice());  //订单总价

                obj.put("cartType",shoppingGoodscart.getCartType());   //商品类型
                obj.put("gcId",shoppingGoodscart.getId());
                if(shoppingGoodscart.getCartType()== Const.SHOPPING_ACT_CART_TYPE){   //艺教活动
                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                    shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                    shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);

                    obj.put("goodsId",shoppingArtactivity.getId());
                    obj.put("goodsName",shoppingArtactivity.getActivityName());
                    obj.put("photo",shoppingArtactivity.getMainPhotoId());
                    obj.put("goodsCount",shoppingGoodscart.getCount());
                }else if(shoppingGoodscart.getCartType()== Const.SHOPPING_PLAN_CART_TYPE){   //爱艺计划
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                    shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);

                    obj.put("goodsId",shoppingArtplan.getId());
                    obj.put("goodsName",shoppingArtplan.getActivityName());
                    obj.put("photo",shoppingArtplan.getMainPhotoId());
                    obj.put("goodsCount",shoppingGoodscart.getCount());
                }else if((shoppingGoodscart.getCartType()== Const.SHOPPING_CLASS_CART_TYPE)){   //艺教培训
                    ShoppingArtclass shoppingArtclas = new ShoppingArtclass();
                    shoppingArtclas.setId(shoppingGoodscart.getGoodsId());
                    shoppingArtclas = shoppingArtclassDao.queryDetail(shoppingArtclas);

                    obj.put("goodsId",shoppingArtclas.getId());
                    obj.put("goodsName",shoppingArtclas.getClassName());
                    obj.put("photo",shoppingArtclas.getMainPhotoId());
                    obj.put("goodsCount",shoppingGoodscart.getCount());
                }
                obj.put("refundTIme",shoppingRefund.getAddTime()); //申请退款时间
                obj.put("refundStatus",shoppingRefund.getRefundStatus()); //退款状态
                obj.put("updateTime",shoppingRefund.getUpdateTime()); //最近操作时间

                resArray.add(obj);
            }

            bizDataJson.put("objList",resArray);
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
     * 查看退货详情
     */
    @Override
    public JSONObject refundDetail(String refundId) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRefund shoppingRefund = new ShoppingRefund();
            shoppingRefund.setId(refundId);
            shoppingRefund = shoppingRefundDao.queryDetail(shoppingRefund);

            String gcId = shoppingRefund.getGcId();
            ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
            shoppingGoodscart.setId(gcId);
            shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);

            JSONObject orderInfo = new JSONObject();
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(shoppingGoodscart.getOfId());
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
            orderInfo.put("ofId",shoppingOrderform.getId());
            orderInfo.put("orderId",shoppingOrderform.getOrderId());    //订单编号
            orderInfo.put("addTime",shoppingOrderform.getAddTime());  //下单时间
            orderInfo.put("payTime",shoppingOrderform.getPayTime());  //付款时间
            orderInfo.put("finishTime",shoppingOrderform.getFinishTime());  //完成时间
            orderInfo.put("shipPrice",shoppingOrderform.getShipPrice());   //订单运费
            orderInfo.put("totalPrice",shoppingOrderform.getTotalPrice());  //订单总价
            orderInfo.put("deductionMemberPrice",shoppingOrderform.getDeductionMemberPrice()); //会员权益抵扣
            orderInfo.put("deductionCouponPrice",shoppingOrderform.getDeductionCouponPrice()); //优惠券抵扣
            orderInfo.put("deductionIntegral",shoppingOrderform.getDeductionIntegral()); //积分抵扣值
            orderInfo.put("deductionIntegralPrice",shoppingOrderform.getDeductionIntegralPrice()); //积分抵扣金额
            orderInfo.put("deductionBalancePrice",shoppingOrderform.getDeductionBalancePrice()); //余额抵扣
            orderInfo.put("payPrice",shoppingOrderform.getPayPrice()); //订单商品
            orderInfo.put("orderGoods",CommonUtil.getOrderGoods(shoppingOrderform.getId())); //现金支付
            //用户信息
            bizDataJson.put("userInfo",shoppingOrderform.getUserInfo());
            //订单信息
            bizDataJson.put("orderInfo",orderInfo);
            //退款信息
            JSONObject refundInfo = new JSONObject();
            //退款商品
            JSONObject refundGoods = new JSONObject();
            refundGoods.put("goodsId",shoppingGoodscart.getGoodsId());
            refundGoods.put("goodsType",shoppingGoodscart.getCartType());   //商品类型
            if(shoppingGoodscart.getCartType()== Const.SHOPPING_ACT_CART_TYPE){   //艺教活动
                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);

                refundGoods.put("goodsName",shoppingArtactivity.getActivityName());
                refundGoods.put("photo",shoppingArtactivity.getMainPhotoId());
                refundGoods.put("goodsCount",shoppingGoodscart.getCount());
                refundGoods.put("goodsPrice",shoppingGoodscart.getPrice());
            }else if(shoppingGoodscart.getCartType()== Const.SHOPPING_PLAN_CART_TYPE){   //爱艺计划
                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);

                refundGoods.put("goodsName",shoppingArtplan.getActivityName());
                refundGoods.put("photo",shoppingArtplan.getMainPhotoId());
                refundGoods.put("goodsCount",shoppingGoodscart.getCount());
                refundGoods.put("goodsPrice",shoppingGoodscart.getPrice());
            }else if((shoppingGoodscart.getCartType()== Const.SHOPPING_CLASS_CART_TYPE)){   //艺教培训
                ShoppingArtclass shoppingArtclas = new ShoppingArtclass();
                shoppingArtclas.setId(shoppingGoodscart.getGoodsId());
                shoppingArtclas = shoppingArtclassDao.queryDetail(shoppingArtclas);

                refundGoods.put("goodsName",shoppingArtclas.getClassName());
                refundGoods.put("photo",shoppingArtclas.getMainPhotoId());
                refundGoods.put("goodsCount",shoppingGoodscart.getCount());
                refundGoods.put("goodsPrice",shoppingGoodscart.getPrice());
            }
            refundGoods.put("deductionMemberPrice",shoppingGoodscart.getDeductionMemberPrice()); //会员权益抵扣
            refundGoods.put("deductionCouponPrice",shoppingGoodscart.getDeductionCouponPrice()); //优惠券抵扣
            refundGoods.put("deductionIntegral",shoppingGoodscart.getDeductionIntegral()); //积分抵扣值
            refundGoods.put("deductionIntegralPrice",shoppingGoodscart.getDeductionIntegralPrice()); //积分抵扣金额
            refundGoods.put("deductionBalancePrice",shoppingGoodscart.getDeductionBalancePrice()); //余额抵扣
            refundGoods.put("payPrice",shoppingGoodscart.getPayPrice()); //现金支付
            refundInfo.put("refundGoods",refundGoods);

            refundInfo.put("refundTIme",shoppingRefund.getAddTime()); //申请退款时间
            refundInfo.put("refundReason",shoppingRefund.getReason()); //申请退款理由
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("refundId",refundId);
            refundInfo.put("refundPhotos",shoppingRefundPhotoDao.queryList(reqMap)); //照片
            refundInfo.put("refundStatus",shoppingRefund.getRefundStatus()); //退款状态
            refundInfo.put("adminUser",shoppingRefund.getAdminUser()); //操作人id
            refundInfo.put("adminName",shoppingRefund.getAdminName()); //操作人名称
            refundInfo.put("adminLog",shoppingRefund.getAdminLog()); //操作备注
            refundInfo.put("updateTime",shoppingRefund.getUpdateTime()); //最近操作时间
            refundInfo.put("refundIntegral",shoppingRefund.getRefundIntegral()); //退还积分数额
            refundInfo.put("refundBalance",shoppingRefund.getRefundBalance()); //退还余额数额
            refundInfo.put("refundCash",shoppingRefund.getRefundCash()); //退还现金数额

            bizDataJson.put("refundInfo",refundInfo);
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
     * 撤回退款申请
     */
    @Override
    public JSONObject cancelRefund(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRefund shoppingRefund = JSON.parseObject(reqJson.toJSONString(), ShoppingRefund.class);
            shoppingRefund.setDeleteStatus("1");
            shoppingRefundDao.update(shoppingRefund);

            ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
            shoppingGoodscart.setId(shoppingRefund.getId());
            shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);
            String ofId = shoppingGoodscart.getOfId();
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(ofId);
            shoppingOrderform.setOrderStatus(orderStateDone);
            shoppingOrderformDao.update(shoppingOrderform);

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
