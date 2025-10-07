package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.*;
import com.centit.shopping.webmgr.service.SellerOrderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>订单管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-25
 **/
@Transactional
@Service
public class SellerOrderServiceImpl implements SellerOrderService {
    public static final Log log = LogFactory.getLog(SellerOrderService.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;

    @Resource
    private ShoppingExpressCompanyDao shoppingExpressCompanyDao;

    @Resource
    private ShoppingAddressDao shoppingAddressDao;

    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;

    @Resource
    private ShoppingRefundDao shoppingRefundDao;

    @Resource
    private ShoppingRefundPhotoDao shoppingRefundPhotoDao;

    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private ShoppingOrderExceptionDao shoppingOrderExceptionDao;

    @Resource
    private ShoppingAssetDao shoppingAssetDao;

    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;

    @Resource
    private ShoppingRechargeDao shoppingRechargeDao;

    @Resource
    private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    @Resource
    private ShoppingCouponDao shoppingCouponDao;

    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;

    @Resource
    private ShoppingIntegralRecordDao shoppingIntegralRecordDao;

    @Resource
    private ShoppingIntegralTotalDao shoppingIntegralTotalDao;

    @Resource
    private TExportFileDao tExportFileDao;

    @Value("${order.orderState.toPay}")
    private int orderStateToPay;

    @Value("${order.orderState.toRefund}")
    private int orderStateToRefund;

    @Value("${order.orderState.anomalous}")
    private int orderStateAnomalous;

    @Value("${order.orderState.handAnomalous}")
    private int orderStateHandAnomalous;

    @Value("${order.orderState.cancel}")
    private int orderStateCancel;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;
    @Resource
    private ShoppingBalanceRecordDao shoppingBalanceRecordDao;

    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;

    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;

    @Resource
    private ShoppingWriteoffDao shoppingWriteoffDao;

    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY = "REDIS_KEY:STOCK:";
    public static final String REDIS_KEY_GOODS = "REDIS_KEY:STOCK:GOODS";
    public static final String REDIS_KEY_PLAN = "REDIS_KEY:STOCK:PLAN";
    @Resource
    private RedisStockService redisStockService;

    /**
     * 查询商户订单分页列表
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
            //所属商户，默认官方商户
            if(null==reqJson.get("storeId")){
                reqMap.put("storeId", CommonUtil.getSystemStore().getId());
            }
            List<ShoppingOrderform> orderList = new ArrayList<>();

            bizDataJson.put("total",shoppingOrderformDao.queryTotalCount(reqMap));
            orderList = shoppingOrderformDao.queryList(reqMap);

            for(ShoppingOrderform shoppingOrderform:orderList){
                shoppingOrderform.setOrderGoods(CommonUtil.getOrderGoodsForSeller(shoppingOrderform.getId()));
            }
            bizDataJson.put("objList",orderList);
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
     * 导出订单明细列表
     */
    @Override
    public JSONObject exportOrderList(JSONObject reqJson, HttpServletResponse response) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TExportFile tExportFile = new TExportFile();
            String fileId = String.valueOf(System.currentTimeMillis());
            tExportFile.setId(fileId);
            tExportFile.setDataType("订单数据");
            tExportFileDao.insert(tExportFile);

            ExecutorService fixPool = Executors.newFixedThreadPool(1);
            fixPool.execute(new Runnable() {
                @Override
                public void run() {

                    HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                    reqMap.put("deleteStatus","0");
                    //所属商户，默认官方商户
                    if(null==reqJson.get("storeId")){
                        reqMap.put("storeId", CommonUtil.getSystemStore().getId());
                    }
                    List<ShoppingOrderform> orderforms = new ArrayList<>();

                    orderforms = shoppingOrderformDao.queryList(reqMap);

                    List<OrderDetail> objList = new ArrayList<>();
                    BigDecimal couponException = BigDecimal.ZERO;
                    BigDecimal memberException = BigDecimal.ZERO;
                    BigDecimal integralException = BigDecimal.ZERO;
                    BigDecimal balanceException = BigDecimal.ZERO;
                    BigDecimal cashException = BigDecimal.ZERO;
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

                        orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());
                        orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
                        orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
                        orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
                        orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
                        orderDetail.setPayPrice(shoppingOrderform.getPayPrice());                  //现金支付金额

                        if(shoppingOrderform.getOrderStatus()==orderStateAnomalous||shoppingOrderform.getOrderStatus()==orderStateHandAnomalous){  //异常订单，需要到订单支付表中查询实际支付情况

                            if(shoppingOrderPay.getCouponStatus()==0||shoppingOrderPay.getCouponStatus()==2){
                                orderDetail.setDeductionCouponPriceState(1);
                                couponException = couponException.add(shoppingOrderform.getDeductionCouponPrice());
                            }

                            if(orderDetail.getDeductionMemberPrice().compareTo(BigDecimal.ZERO)==1){
                                orderDetail.setDeductionMemberPriceState(1);
                                memberException = memberException.add(shoppingOrderform.getDeductionMemberPrice());
                            }

                            if(shoppingOrderPay.getIntegralStatus()==0||shoppingOrderPay.getIntegralStatus()==2){
                                orderDetail.setDeductionIntegralState(1);
                                orderDetail.setDeductionBalancePriceState(1);
                                integralException = integralException.add(shoppingOrderform.getDeductionIntegralPrice());
                            }
                            if(shoppingOrderPay.getBalanceStatus()==0||shoppingOrderPay.getIntegralStatus()==2){
                                orderDetail.setDeductionBalancePriceState(1);    //余额抵扣金额
                                balanceException = balanceException.add(shoppingOrderform.getDeductionBalancePrice());
                            }
                            if(shoppingOrderPay.getCashStatus()==0||shoppingOrderPay.getCashStatus()==2){
                                orderDetail.setPayPriceState(1);
                                cashException = cashException.add(shoppingOrderform.getPayPrice());
                            }
                        }
                        orderDetail.setPaymentId(shoppingOrderform.getPaymentId());   //现金支付方式Id

                        if(null !=shoppingOrderform.getPaymentId()&&shoppingOrderform.getPaymentId().equals("20")){   //微信支付
                            orderDetail.setWxPayPrice_park(orderDetail.getPayPrice());
                        }
                        if(null !=shoppingOrderform.getPaymentId()&&shoppingOrderform.getPaymentId().equals("21")){   //微信支付
                            orderDetail.setWxPayPrice(orderDetail.getPayPrice());
                        }
                        if(null !=shoppingOrderform.getPaymentId()&&shoppingOrderform.getPaymentId().equals("22")){   //支付宝支付
                            orderDetail.setAliPayPrice(orderDetail.getPayPrice());
                        }

                        orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
                        orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）
                        orderDetail.setOutOrderId(shoppingOrderform.getOutOrderId());  //第三方交互订单号 （麦座订单号/速停车账单号）
                        //商品信息
                        List<HashMap<String,Object>> goodsInfoList = CommonUtil.getOrderGoodsForSeller(shoppingOrderform.getId());
//                orderDetail.setGoodsInfo(goodsInfoList);
                        String goodsName ="";
                        String eventName ="";
                        String signInfo="";
                        String specInfo="";
                        int goodsCount=0;
                        for(HashMap<String,Object> goodsMap:goodsInfoList){
                            goodsName +=goodsMap.get("goodsName")+";";
                            if(null !=goodsMap.get("eventName")){
                                eventName +=goodsMap.get("eventName")+";";
                            }
                            if(null !=goodsMap.get("spec")){
                                specInfo +=goodsMap.get("spec")+";";
                            }
                            goodsCount+=Integer.valueOf(goodsMap.get("goodsCount").toString());

                            if(null !=goodsMap.get("signupInfos")){   //报名信息
                                int cartType= Integer.valueOf(goodsMap.get("cartType").toString());
                                if(cartType==Const.SHOPPING_ACT_CART_TYPE){
                                    List<ShoppingArtactivitySignupinfo> signupInfos = (List<ShoppingArtactivitySignupinfo>) goodsMap.get("signupInfos");
                                    if(!signupInfos.isEmpty()){
                                        for(ShoppingArtactivitySignupinfo shoppingArtactivitySignupinfo:signupInfos){
                                            String signupInfo =shoppingArtactivitySignupinfo.getSignupInfo();
                                            JSONArray array = JSONArray.parseArray(signupInfo);
                                            String str ="";
                                            for(int i=0;i<array.size();i++){
                                                JSONObject signObj = array.getJSONObject(i);
                                                String oneStr = signObj.getString("inforName")+":"+signObj.getString("inforValue");
                                                str+=oneStr+",";
                                            }
                                            if(str.length()>0){
                                                str=str.substring(0,str.length()-1);
                                            }
                                            signInfo +=str+";";
                                        }
                                        if(signInfo.length()>0){
                                            signInfo=signInfo.substring(0,signInfo.length()-1);
                                        }
                                    }

                                }else if(cartType==Const.SHOPPING_PLAN_CART_TYPE){
                                    List<ShoppingArtplanSignupinfo> signupInfos = (List<ShoppingArtplanSignupinfo>) goodsMap.get("signupInfos");
                                    if(!signupInfos.isEmpty()){
                                        for(ShoppingArtplanSignupinfo shoppingArtplanSignupinfo:signupInfos){
                                            String signupInfo =shoppingArtplanSignupinfo.getSignupInfo();
                                            JSONArray array = JSONArray.parseArray(signupInfo);
                                            String str ="";
                                            for(int i=0;i<array.size();i++){
                                                JSONObject signObj = array.getJSONObject(i);
                                                String oneStr = signObj.getString("inforName")+":"+signObj.getString("inforValue");
                                                str+=oneStr+",";
                                            }
                                            if(str.length()>0){
                                                str=str.substring(0,str.length()-1);
                                            }
                                            signInfo +=str+";";
                                        }
                                        if(signInfo.length()>0){
                                            signInfo=signInfo.substring(0,signInfo.length()-1);
                                        }
                                    }

                                }
                            }
                        }
                        orderDetail.setSignInfo(signInfo);
                        if(goodsName.length()>0){
                            goodsName = goodsName.substring(0,goodsName.length() - 1);
                        }
                        if(eventName.length()>0){
                            eventName = eventName.substring(0,eventName.length() - 1);
                        }
                        if(specInfo.length()>0){
                            specInfo = specInfo.substring(0,specInfo.length() - 1);
                        }
                        orderDetail.setGoodsName(goodsName);
                        orderDetail.setEventName(eventName);
                        orderDetail.setSpecInfo(specInfo);
                        orderDetail.setGoodsCount(goodsCount);
                        orderDetail.setUserId(shoppingOrderform.getUserId());
                        orderDetail.setUserName(shoppingOrderform.getUserName());
                        orderDetail.setMobile(shoppingOrderform.getMobile());

                        //收货人信息
                        if(null !=shoppingOrderform.getAddrId()&&!"".equals(shoppingOrderform.getAddrId())){
                            String mzUserId= CommonUtil.getMzUserId(shoppingOrderform.getUserId());
                            JSONObject addressObj = MZService.getAddressDetail(mzUserId,shoppingOrderform.getAddrId());

                            if(null !=addressObj){
                                orderDetail.setShipPerson(addressObj.get("receiver_name")==null?"":addressObj.getString("receiver_name"));
                                orderDetail.setShipPhone(addressObj.get("receiver_phone")==null?"":addressObj.getString("receiver_phone"));
                                String province_name = addressObj.get("province_name")==null?"":addressObj.getString("province_name");
                                String area_name = addressObj.get("area_name")==null?"":addressObj.getString("area_name");
                                String address = addressObj.get("address")==null?"":addressObj.getString("address");
                                orderDetail.setShipAddress(province_name+area_name+address);


                            }

                        }

                        //查询核销信息
                        String ofId = shoppingOrderform.getId();
                        List<HashMap<String, Object>> writeOffList = shoppingWriteoffDao.queryOrderWriteoff(ofId);
//                        for(HashMap<String, Object> wMap:writeOffList){
//                            int offStatus =wMap.get("OFF_STATUS");
//                        }

                        objList.add(orderDetail);
                    }
//            //统计总数
//            HashMap<String, Object> sum= shoppingOrderformDao.querySumList(reqMap);
//
//            String sumStr = "订单总金额："+sum.get("totalPrice")
//                    +"  优惠券应扣："+sum.get("deduction_coupon_price")+"  会员权益抵扣："+sum.get("deduction_member_price")+"  积分支付抵扣："
//                    +sum.get("deduction_integral_price")+"  余额支付抵扣："+sum.get("deduction_balance_price")+"  现金支付金额："+sum.get("pay_price");
                    String sumStr ="订单数据";
                    // 导出表的标题
                    String title =sumStr;
                    // 导出表的列名
                    String[] rowsName =new String[]{"订单编号","商品名称","商品数量","商品规格","演出日期","下单时间","订单类型","订单状态","订单金额","商品金额","运费金额","优惠券抵扣"
                            ,"优惠券信息","会员权益抵扣","积分支付抵扣","余额支付抵扣",
//                    "现金支付金额","现金支付方式",
                            "微信支付(大剧院)","微信支付(停车场)","支付宝支付",
                            "现金支付流水号","支付时间"
                            ,"会员姓名","会员手机号","第三方订单号","报名信息","收货人","收货电话","收货地址"};
                    List<Object[]> dataList = new ArrayList<Object[]>();
                    for(OrderDetail orderDetail:objList){
                        Object[] obj = new Object[28];
                        obj[0] = orderDetail.getOrderId();
                        obj[1] = orderDetail.getGoodsName();
                        obj[2] = orderDetail.getGoodsCount();
                        obj[3] = orderDetail.getSpecInfo();
                        obj[4] = orderDetail.getEventName();
                        obj[5] = orderDetail.getAddTime();
                        obj[6] = getOrderType(orderDetail.getOrderType());
                        obj[7] = getOrderStatus(orderDetail.getOrderStatus());
                        obj[8] = orderDetail.getTotalPrice();
                        obj[9] = orderDetail.getGoodsPrice();
                        obj[10] = orderDetail.getShipPrice();
                        obj[11] = orderDetail.getDeductionCouponPrice();
                        if(null !=orderDetail.getCouponInfo()){
                            obj[12] = orderDetail.getCouponInfo().getRight_Display();
                        }else{
                            obj[12] ="";
                        }
                        obj[13] = orderDetail.getDeductionMemberPrice();
                        obj[14] = orderDetail.getDeductionIntegralPrice();
                        obj[15] = orderDetail.getDeductionBalancePrice();
//                obj[13] = orderDetail.getPayPrice();
//                if(null !=orderDetail.getPaymentId()){
//                    if(orderDetail.getPaymentId().equals("21")){
//                        obj[14] = "微信支付";
//                    }else if(orderDetail.getPaymentId().equals("22")){
//                        obj[14] = "支付宝支付";
//                    }else{
//                        obj[14] = "";
//                    }
//                }else{
//                    obj[14] = "";
//                }
                        obj[16] = orderDetail.getWxPayPrice();
                        obj[17] = orderDetail.getWxPayPrice_park();
                        obj[18] = orderDetail.getAliPayPrice();
                        obj[19] = orderDetail.getPayOrderId();
                        obj[20] = orderDetail.getPayTime();
                        obj[21] = orderDetail.getUserName();
                        obj[22] = orderDetail.getMobile();
                        obj[23] = orderDetail.getOutOrderId();
                        obj[24] = orderDetail.getSignInfo();
                        obj[25] = orderDetail.getShipPerson();
                        obj[26] = orderDetail.getShipPhone();
                        obj[27] = orderDetail.getShipAddress();
                        dataList.add(obj);
                    }


                    String fileName = fileId + ".xls";
                    ShoppingSysconfig config = CommonUtil.getSysConfig();
                    String uploadFilePath = config.getUploadFilePath();
                    File file = new File(uploadFilePath + File.separator + "exportFile" +
                            File.separator + fileName);
                    try {
                        OutputStream out = new FileOutputStream(file);
                        ExportExcel ex = new ExportExcel(title, rowsName, dataList);
                        ex.export(out);

                        tExportFile.setFileName(fileName);
                        tExportFile.setFinishTime(StringUtil.nowTimeString());
                        tExportFile.setTaskStatus(1);  //已完成

                        tExportFileDao.update(tExportFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tExportFile.setTaskStatus(-1);
                        tExportFileDao.update(tExportFile);
                    }
                }
            });
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
//     * 导出订单明细列表
//     */
//    @Override
//    public void exportOrderList(JSONObject reqJson, HttpServletResponse response) {
//        JSONObject retJson = new JSONObject();
//        try {
//
//            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
//            reqMap.put("deleteStatus","0");
//            //所属商户，默认官方商户
//            if(null==reqJson.get("storeId")){
//                reqMap.put("storeId", CommonUtil.getSystemStore().getId());
//            }
//            List<ShoppingOrderform> orderforms = new ArrayList<>();
//
//            orderforms = shoppingOrderformDao.queryList(reqMap);
//
//            List<OrderDetail> objList = new ArrayList<>();
//            BigDecimal couponException = BigDecimal.ZERO;
//            BigDecimal memberException = BigDecimal.ZERO;
//            BigDecimal integralException = BigDecimal.ZERO;
//            BigDecimal balanceException = BigDecimal.ZERO;
//            BigDecimal cashException = BigDecimal.ZERO;
//            for(ShoppingOrderform shoppingOrderform:orderforms){
//                OrderDetail orderDetail = new OrderDetail();
//                orderDetail.setOfId(shoppingOrderform.getId());
//                orderDetail.setOrderId(shoppingOrderform.getOrderId());
//                orderDetail.setAddTime(shoppingOrderform.getAddTime());
//                orderDetail.setOrderType(shoppingOrderform.getOrderType());
//                orderDetail.setOrderStatus(shoppingOrderform.getOrderStatus());
//                orderDetail.setTotalPrice(shoppingOrderform.getTotalPrice());
//                orderDetail.setShipPrice(shoppingOrderform.getShipPrice());
//                orderDetail.setGoodsPrice(shoppingOrderform.getTotalPrice().subtract(shoppingOrderform.getShipPrice()));
//                orderDetail.setCouponId(shoppingOrderform.getCouponId());
//
//                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
//                shoppingOrderPay.setOfId(shoppingOrderform.getId());
//                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
//                if(null !=shoppingOrderform.getCouponId()){
//                    ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
//                    shoppingCoupon.setRight_No(shoppingOrderform.getCouponId());
//                    shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
//                    orderDetail.setCouponInfo(shoppingCoupon);
//                }
//
//                orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());
//                orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
//                orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
//                orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
//                orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
//                orderDetail.setPayPrice(shoppingOrderform.getPayPrice());                  //现金支付金额
//
//                if(shoppingOrderform.getOrderStatus()==orderStateAnomalous||shoppingOrderform.getOrderStatus()==orderStateHandAnomalous){  //异常订单，需要到订单支付表中查询实际支付情况
//
//                    if(shoppingOrderPay.getCouponStatus()==0||shoppingOrderPay.getCouponStatus()==2){
//                        orderDetail.setDeductionCouponPriceState(1);
//                        couponException = couponException.add(shoppingOrderform.getDeductionCouponPrice());
//                    }
//
//                    if(orderDetail.getDeductionMemberPrice().compareTo(BigDecimal.ZERO)==1){
//                        orderDetail.setDeductionMemberPriceState(1);
//                        memberException = memberException.add(shoppingOrderform.getDeductionMemberPrice());
//                    }
//
//                    if(shoppingOrderPay.getIntegralStatus()==0||shoppingOrderPay.getIntegralStatus()==2){
//                        orderDetail.setDeductionIntegralState(1);
//                        orderDetail.setDeductionBalancePriceState(1);
//                        integralException = integralException.add(shoppingOrderform.getDeductionIntegralPrice());
//                    }
//                    if(shoppingOrderPay.getBalanceStatus()==0||shoppingOrderPay.getIntegralStatus()==2){
//                        orderDetail.setDeductionBalancePriceState(1);    //余额抵扣金额
//                        balanceException = balanceException.add(shoppingOrderform.getDeductionBalancePrice());
//                    }
//                    if(shoppingOrderPay.getCashStatus()==0||shoppingOrderPay.getCashStatus()==2){
//                        orderDetail.setPayPriceState(1);
//                        cashException = cashException.add(shoppingOrderform.getPayPrice());
//                    }
//                }
//                orderDetail.setPaymentId(shoppingOrderform.getPaymentId());   //现金支付方式Id
//
//                if(null !=shoppingOrderform.getPaymentId()&&shoppingOrderform.getPaymentId().equals("20")){   //微信支付
//                    orderDetail.setWxPayPrice_park(orderDetail.getPayPrice());
//                }
//                if(null !=shoppingOrderform.getPaymentId()&&shoppingOrderform.getPaymentId().equals("21")){   //微信支付
//                    orderDetail.setWxPayPrice(orderDetail.getPayPrice());
//                }
//                if(null !=shoppingOrderform.getPaymentId()&&shoppingOrderform.getPaymentId().equals("22")){   //支付宝支付
//                    orderDetail.setAliPayPrice(orderDetail.getPayPrice());
//                }
//
//                orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
//                orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）
//                orderDetail.setOutOrderId(shoppingOrderform.getOutOrderId());  //第三方交互订单号 （麦座订单号/速停车账单号）
//                //商品信息
//                List<HashMap<String,Object>> goodsInfoList = CommonUtil.getOrderGoodsForSeller(shoppingOrderform.getId());
////                orderDetail.setGoodsInfo(goodsInfoList);
//                String goodsName ="";
//                String eventName ="";
//                String signInfo="";
//                String specInfo="";
//                int goodsCount=0;
//                for(HashMap<String,Object> goodsMap:goodsInfoList){
//                    goodsName +=goodsMap.get("goodsName")+";";
//                    if(null !=goodsMap.get("eventName")){
//                        eventName +=goodsMap.get("eventName")+";";
//                    }
//                    if(null !=goodsMap.get("spec")){
//                        specInfo +=goodsMap.get("spec")+";";
//                    }
//                    goodsCount+=Integer.valueOf(goodsMap.get("goodsCount").toString());
//
//                    if(null !=goodsMap.get("signupInfos")){   //报名信息
//                        int cartType= Integer.valueOf(goodsMap.get("cartType").toString());
//                        if(cartType==Const.SHOPPING_ACT_CART_TYPE){
//                            List<ShoppingArtactivitySignupinfo> signupInfos = (List<ShoppingArtactivitySignupinfo>) goodsMap.get("signupInfos");
//                            if(!signupInfos.isEmpty()){
//                                for(ShoppingArtactivitySignupinfo shoppingArtactivitySignupinfo:signupInfos){
//                                    String signupInfo =shoppingArtactivitySignupinfo.getSignupInfo();
//                                    JSONArray array = JSONArray.parseArray(signupInfo);
//                                    String str ="";
//                                    for(int i=0;i<array.size();i++){
//                                        JSONObject signObj = array.getJSONObject(i);
//                                        String oneStr = signObj.getString("inforName")+":"+signObj.getString("inforValue");
//                                        str+=oneStr+",";
//                                    }
//                                    if(str.length()>0){
//                                        str=str.substring(0,str.length()-1);
//                                    }
//                                    signInfo +=str+";";
//                                }
//                                if(signInfo.length()>0){
//                                    signInfo=signInfo.substring(0,signInfo.length()-1);
//                                }
//                            }
//
//                        }else if(cartType==Const.SHOPPING_PLAN_CART_TYPE){
//                            List<ShoppingArtplanSignupinfo> signupInfos = (List<ShoppingArtplanSignupinfo>) goodsMap.get("signupInfos");
//                            if(!signupInfos.isEmpty()){
//                                for(ShoppingArtplanSignupinfo shoppingArtplanSignupinfo:signupInfos){
//                                    String signupInfo =shoppingArtplanSignupinfo.getSignupInfo();
//                                    JSONArray array = JSONArray.parseArray(signupInfo);
//                                    String str ="";
//                                    for(int i=0;i<array.size();i++){
//                                        JSONObject signObj = array.getJSONObject(i);
//                                        String oneStr = signObj.getString("inforName")+":"+signObj.getString("inforValue");
//                                        str+=oneStr+",";
//                                    }
//                                    if(str.length()>0){
//                                        str=str.substring(0,str.length()-1);
//                                    }
//                                    signInfo +=str+";";
//                                }
//                                if(signInfo.length()>0){
//                                    signInfo=signInfo.substring(0,signInfo.length()-1);
//                                }
//                            }
//
//                        }
//                    }
//                }
//                orderDetail.setSignInfo(signInfo);
//                if(goodsName.length()>0){
//                    goodsName = goodsName.substring(0,goodsName.length() - 1);
//                }
//                if(eventName.length()>0){
//                    eventName = eventName.substring(0,eventName.length() - 1);
//                }
//                if(specInfo.length()>0){
//                    specInfo = specInfo.substring(0,specInfo.length() - 1);
//                }
//                orderDetail.setGoodsName(goodsName);
//                orderDetail.setEventName(eventName);
//                orderDetail.setSpecInfo(specInfo);
//                orderDetail.setGoodsCount(goodsCount);
//                orderDetail.setUserId(shoppingOrderform.getUserId());
//                orderDetail.setUserName(shoppingOrderform.getUserName());
//                orderDetail.setMobile(shoppingOrderform.getMobile());
//
//                objList.add(orderDetail);
//            }
////            //统计总数
////            HashMap<String, Object> sum= shoppingOrderformDao.querySumList(reqMap);
////
////            String sumStr = "订单总金额："+sum.get("totalPrice")
////                    +"  优惠券应扣："+sum.get("deduction_coupon_price")+"  会员权益抵扣："+sum.get("deduction_member_price")+"  积分支付抵扣："
////                    +sum.get("deduction_integral_price")+"  余额支付抵扣："+sum.get("deduction_balance_price")+"  现金支付金额："+sum.get("pay_price");
//            String sumStr ="订单数据";
//            // 导出表的标题
//            String title =sumStr;
//            // 导出表的列名
//            String[] rowsName =new String[]{"订单编号","商品名称","商品数量","商品规格","演出日期","下单时间","订单类型","订单状态","订单金额","商品金额","运费金额","优惠券抵扣"
//                    ,"优惠券信息","会员权益抵扣","积分支付抵扣","余额支付抵扣",
////                    "现金支付金额","现金支付方式",
//                    "微信支付(大剧院)","微信支付(停车场)","支付宝支付",
//                    "现金支付流水号","支付时间"
//                    ,"会员姓名","会员手机号","第三方订单号","报名信息"};
//            List<Object[]> dataList = new ArrayList<Object[]>();
//            for(OrderDetail orderDetail:objList){
//                Object[] obj = new Object[25];
//                obj[0] = orderDetail.getOrderId();
//                obj[1] = orderDetail.getGoodsName();
//                obj[2] = orderDetail.getGoodsCount();
//                obj[3] = orderDetail.getSpecInfo();
//                obj[4] = orderDetail.getEventName();
//                obj[5] = orderDetail.getAddTime();
//                obj[6] = getOrderType(orderDetail.getOrderType());
//                obj[7] = getOrderStatus(orderDetail.getOrderStatus());
//                obj[8] = orderDetail.getTotalPrice();
//                obj[9] = orderDetail.getGoodsPrice();
//                obj[10] = orderDetail.getShipPrice();
//                obj[11] = orderDetail.getDeductionCouponPrice();
//                if(null !=orderDetail.getCouponInfo()){
//                    obj[12] = orderDetail.getCouponInfo().getRight_Display();
//                }else{
//                    obj[12] ="";
//                }
//                obj[13] = orderDetail.getDeductionMemberPrice();
//                obj[14] = orderDetail.getDeductionIntegralPrice();
//                obj[15] = orderDetail.getDeductionBalancePrice();
////                obj[13] = orderDetail.getPayPrice();
////                if(null !=orderDetail.getPaymentId()){
////                    if(orderDetail.getPaymentId().equals("21")){
////                        obj[14] = "微信支付";
////                    }else if(orderDetail.getPaymentId().equals("22")){
////                        obj[14] = "支付宝支付";
////                    }else{
////                        obj[14] = "";
////                    }
////                }else{
////                    obj[14] = "";
////                }
//                obj[16] = orderDetail.getWxPayPrice();
//                obj[17] = orderDetail.getWxPayPrice_park();
//                obj[18] = orderDetail.getAliPayPrice();
//                obj[19] = orderDetail.getPayOrderId();
//                obj[20] = orderDetail.getPayTime();
//                obj[21] = orderDetail.getUserName();
//                obj[22] = orderDetail.getMobile();
//                obj[23] = orderDetail.getOutOrderId();
//                obj[24] = orderDetail.getSignInfo();
//                dataList.add(obj);
//            }
////            String fileName =new String(("订单明细_"+String.valueOf(System.currentTimeMillis()) + ".xls").getBytes(),"ISO-8859-1");
//            String fileName =String.valueOf(System.currentTimeMillis()) + ".xls";
//
//            String headStr = "attachment; filename=\"" + fileName + "\"";
//            response.setContentType("APPLICATION/OCTET-STREAM");
//            response.setHeader("Content-Disposition", headStr);
//            OutputStream out = response.getOutputStream();
//            ExportExcel ex = new ExportExcel(title, rowsName, dataList);
//            try {
//                ex.export(out);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            log.error(e);
//        }
//    }

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

            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(id);
            //订单详情
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
            shoppingOrderform.setOrderGoods(CommonUtil.getOrderGoodsForSeller(shoppingOrderform.getId()));
            String mzUserId= CommonUtil.getMzUserId(shoppingOrderform.getUserId());
            bizDataJson.put("orderInfo",shoppingOrderform);
            //操作日志
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("ofId", id);
            List<ShoppingOrderLog> orderLogs = shoppingOrderLogDao.queryList(reqMap);
            bizDataJson.put("orderLogs",orderLogs);
            //收货人信息
            if(null !=shoppingOrderform.getAddrId()&&!"".equals(shoppingOrderform.getAddrId())){
                JSONObject addressObj = MZService.getAddressDetail(mzUserId,shoppingOrderform.getAddrId());
                bizDataJson.put("orderAddrInfo",addressObj);
            }
            //物流信息
            if(null !=shoppingOrderform.getShipCode()&&!"".equals(shoppingOrderform.getShipCode())){
//                JSONObject js = CommonUtil.queryTransportInfo(shoppingOrderform.getEcId(),shoppingOrderform.getShipCode());
//                js.put("orderId",shoppingOrderform.getId());  //订单编号
//                js.put("shipCode",shoppingOrderform.getShipCode());  //物流单号
//                js.put("ecvalue",shoppingOrderform.getecvalue());  //快递公司名称
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

            if(shoppingOrderform.getOrderStatus()==orderStateHandAnomalous){
                ShoppingOrderException shoppingOrderException = new ShoppingOrderException();
                shoppingOrderException.setOfId(shoppingOrderform.getId());
                shoppingOrderException = shoppingOrderExceptionDao.queryDetail(shoppingOrderException);
                bizDataJson.put("handInfo",shoppingOrderException);
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
     * @Description 调整订单费用
     **/
    @Override
    public JSONObject changeOrderPrice(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = reqJson.getString("id");
            BigDecimal price = reqJson.getBigDecimal("price");
            String userId = reqJson.getString("userId");
            String info = reqJson.get("info")==null?"":reqJson.getString("info");

            ShoppingOrderform shoppingOrderform =new ShoppingOrderform();
            shoppingOrderform.setId(id);
            shoppingOrderform=shoppingOrderformDao.queryDetail(shoppingOrderform);

            // 添加订单日志
            ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
            shoppingOrderLog.setLogInfo("调整费用");
//            shoppingOrderLog.setStateInfo(shoppingOrderform.getOrderType());
            shoppingOrderLog.setStateInfo(info);
            shoppingOrderLog.setLogUserId(userId);
            shoppingOrderLog.setLogUserType("1");
            shoppingOrderLog.setOfId(shoppingOrderform.getId());
            shoppingOrderLog.setBeforeInfo(shoppingOrderform.getTotalPrice().toString());
            shoppingOrderLog.setAfterInfo(price.toString());
            shoppingOrderLogDao.insert(shoppingOrderLog);

            //调整费用
            shoppingOrderform.setPayPrice(price);
            shoppingOrderformDao.update(shoppingOrderform);

            //发送短息
            //发送应用内提醒

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
     * @Description 取消订单
     **/
    @Override
    public JSONObject cancelOrder(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = reqJson.getString("id");
            String logUserId = reqJson.getString("userId");
            //取消原因
            String reason = reqJson.getString("info");

            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(id);
            //查询订单主体信息
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
            String userId = shoppingOrderform.getUserId();
            String mzUserId = CommonUtil.getMzUserId(userId);

            if(shoppingOrderform.getOrderStatus()!=orderStateToPay){
                retCode = "1";
                retMsg = "当前订单不可取消，青重新确认订单状态！";
            }else{

                //如果是演出票订单，需要调麦座的取消订单接口
                if(shoppingOrderform.getOrderId().startsWith(Const.TICKET_ORDER)){
                    String mz_order_id = shoppingOrderform.getOutOrderId();  //麦座订单号
                    MZService.cancelOrder(mzUserId,mz_order_id,reason);
                    // 取消订单
                    shoppingOrderform.setOrderStatus(orderStateCancel);
                    shoppingOrderformDao.cancelOrder(shoppingOrderform);

                    // 添加订单日志
                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                    shoppingOrderLog.setLogInfo("取消订单");
                    shoppingOrderLog.setStateInfo(reason);
                    shoppingOrderLog.setLogUserId(logUserId);
                    shoppingOrderLog.setOfId(shoppingOrderform.getId());
                    shoppingOrderLogDao.insert(shoppingOrderLog);
                }else{
                    // 取消订单
                    shoppingOrderform.setOrderStatus(orderStateCancel);
                    shoppingOrderformDao.cancelOrder(shoppingOrderform);

                    // 添加订单日志
                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                    shoppingOrderLog.setLogInfo("取消订单");
                    shoppingOrderLog.setStateInfo(reason);
                    shoppingOrderLog.setLogUserId(logUserId);
                    shoppingOrderLog.setOfId(shoppingOrderform.getId());
                    shoppingOrderLogDao.insert(shoppingOrderLog);
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
                }
            }


            //发送短息
            //发送应用内提醒

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
     * 获取所有快递公司
     */
    @Override
    public JSONObject queryExpressCompanys(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("deleteStatus","0");
            reqMap.put("companyStatus","0");

            bizDataJson.put("objList",shoppingExpressCompanyDao.queryList(reqMap));
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
     * @Description 确认发货
     **/
    @Override
    public JSONObject confirmDelivery(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = reqJson.getString("id");
            String userId = reqJson.getString("userId");
            String info = reqJson.getString("info");

            ShoppingOrderform shoppingOrderform =new ShoppingOrderform();
            shoppingOrderform.setId(id);
            shoppingOrderform=shoppingOrderformDao.queryDetail(shoppingOrderform);

            // 添加订单日志
            ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();

            shoppingOrderLog.setStateInfo(info);
            shoppingOrderLog.setLogUserId(userId);
            shoppingOrderLog.setLogUserType("1");
            shoppingOrderLog.setOfId(shoppingOrderform.getId());
            shoppingOrderLog.setAfterInfo(reqJson.get("shipCode")==null?"0":reqJson.getString("shipCode"));

            if(null !=reqJson.get("ecId")&&!"".equals(reqJson.getString("ecId"))){
                //修改订单状态，保存物流信息
                String transport = reqJson.getString("transport");
                shoppingOrderform.setTransport(transport);

                shoppingOrderLog.setLogInfo("确认发货");
                shoppingOrderform.setShipCode(reqJson.getString("shipCode"));
                shoppingOrderform.setEcId(reqJson.getString("ecId"));
                shoppingOrderform.setOrderStatus(30);
                shoppingOrderform.setShipTime(StringUtil.nowTimeString());

                shoppingOrderformDao.update(shoppingOrderform);
            }else {  //如果是无需快递的商品，确认发货即代表订单完成
                shoppingOrderLog.setLogInfo("订单完成");
                shoppingOrderform.setOrderStatus(50);

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



                //发送短息
                //发送应用内提醒
            }
            shoppingOrderLogDao.insert(shoppingOrderLog);
            shoppingOrderformDao.update(shoppingOrderform);
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
     * @Description 修改物流
     **/
    @Override
    public JSONObject modifyLogistics(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = reqJson.getString("id");
            String userId = reqJson.getString("userId");
            String info = reqJson.getString("info");

            ShoppingOrderform shoppingOrderform =new ShoppingOrderform();
            shoppingOrderform.setId(id);
            shoppingOrderform=shoppingOrderformDao.queryDetail(shoppingOrderform);

            // 添加订单日志
            ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
            shoppingOrderLog.setLogInfo("修改物流");
//            shoppingOrderLog.setStateInfo(shoppingOrderform.getOrderType());
            shoppingOrderLog.setStateInfo(info);
            shoppingOrderLog.setLogUserId(userId);
            shoppingOrderLog.setLogUserType("1");
            shoppingOrderLog.setOfId(shoppingOrderform.getId());
            shoppingOrderLog.setBeforeInfo(shoppingOrderform.getShipCode());
            shoppingOrderLog.setAfterInfo(reqJson.get("shipCode")==null?"0":reqJson.getString("shipCode"));
            shoppingOrderLogDao.insert(shoppingOrderLog);

            //修改物流信息
            if(null !=reqJson.get("ecId")&&!"".equals(reqJson.getString("ecId"))){
                shoppingOrderform.setEcId(reqJson.getString("ecId"));
                shoppingOrderform.setShipCode(reqJson.getString("shipCode"));
            }

            shoppingOrderformDao.update(shoppingOrderform);

            //发送短息
            //发送应用内提醒

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
     * @Description 查询物流
     **/
    @Override
    public JSONObject queryLogistics(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {

            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(id);
            //订单详情
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
            String mzUserId= CommonUtil.getMzUserId(shoppingOrderform.getUserId());

            //收货人信息
            if(null !=shoppingOrderform.getAddrId()&&!"".equals(shoppingOrderform.getAddrId())){
//                bizDataJson.put("orderAddrInfo",CommonUtil.getShoppingAddress(shoppingOrderform.getAddrId()));
                JSONObject addressObj = MZService.getAddressDetail(mzUserId,shoppingOrderform.getAddrId());
                bizDataJson.put("orderAddrInfo",addressObj);
            }
            //物流信息
            if(null !=shoppingOrderform.getShipCode()&&!"".equals(shoppingOrderform.getShipCode())){
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

//                JSONObject js = CommonUtil.queryTransportInfo(shoppingOrderform.getEcId(),shoppingOrderform.getShipCode());
//                js.put("orderId",shoppingOrderform.getOrderId());  //订单编号
//                js.put("shipCode",shoppingOrderform.getShipCode());  //物流单号
//                js.put("ecvalue",shoppingOrderform.getecvalue());  //快递公司名称
//                bizDataJson.put("orderTransportInfo",js);
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

            shoppingOrderform.setId(ofId);
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
     * 异常处理
     */
    @Override
    public JSONObject handException(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingOrderException shoppingOrderException = JSON.parseObject(reqJson.toJSONString(), ShoppingOrderException.class);
            shoppingOrderExceptionDao.insert(shoppingOrderException);

            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(shoppingOrderException.getOfId());
            shoppingOrderform =shoppingOrderformDao.queryDetail(shoppingOrderform);

            //将退还的积分与余额数值存入用户待领取的数据表
            ShoppingAsset shoppingAsset=new ShoppingAsset();
            shoppingAsset.setUserId(shoppingOrderform.getUserId());
            shoppingAsset = shoppingAssetDao.queryDetail(shoppingAsset);
            if(shoppingAsset==null){
                shoppingAsset=new ShoppingAsset();
                shoppingAsset.setUserId(shoppingOrderform.getUserId());
                shoppingAsset.setIntegralValue(shoppingOrderException.getRefundIntegral());
                int balanceValue = shoppingOrderException.getRefundBalance().multiply(new BigDecimal(100)).intValue();
                shoppingAsset.setBalanceValue(balanceValue);
                shoppingAssetDao.insert(shoppingAsset);
            }else{
                shoppingAsset.setIntegralValue(shoppingAsset.getIntegralValue()+shoppingOrderException.getRefundIntegral());
                int balanceValue = shoppingOrderException.getRefundBalance().multiply(new BigDecimal(100)).intValue();
                shoppingAsset.setBalanceValue(shoppingAsset.getBalanceValue()+balanceValue);
                shoppingAssetDao.update(shoppingAsset);
            }

            if(shoppingOrderException.getRefundIntegral()>0){
                //用户积分新增记录
                ShoppingIntegralRecord shoppingIntegralRecord = new ShoppingIntegralRecord();
                shoppingIntegralRecord.setUserId(shoppingOrderform.getUserId());
                shoppingIntegralRecord.setIntegralCount(shoppingOrderException.getRefundIntegral());
                shoppingIntegralRecord.setRemark("退还积分，订单号"+shoppingOrderform.getOrderId());
                shoppingIntegralRecordDao.insert(shoppingIntegralRecord);
            }
            if(shoppingOrderException.getRefundBalance().compareTo(BigDecimal.ZERO)>0){
                //用户余额新增记录
                ShoppingBalanceRecord shoppingBalanceRecord = new ShoppingBalanceRecord();
                shoppingBalanceRecord.setUserId(shoppingOrderform.getUserId());
                shoppingBalanceRecord.setBalanceCount(shoppingOrderException.getRefundBalance().multiply(new BigDecimal(100)).intValue());
                shoppingBalanceRecord.setRemark("退还余额，订单号"+shoppingOrderform.getOrderId());
                shoppingBalanceRecordDao.insert(shoppingBalanceRecord);
            }

            shoppingOrderform.setOrderStatus(orderStateHandAnomalous);
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

    public String getOrderType(int orderType){
        switch(orderType){
            case 0:
                return "合并支付";
            case 11:
                return "文创商品";
            case 12:
                return "积分商品";
            case 13:
                return "艺术活动";
            case 14:
                return "艺术培训";
            case 15:
                return "爱艺计划";
            case 2:
                return "演出";
            case 3:
                return "停车";
            case 4:
                return "点播";
            case 5:
                return "充值";
            default:
                return "";
        }
    }

    public String getOrderStatus(int orderStatus){
        switch(orderStatus){
            case 20:
                return "已付款";
            case 30:
                return "已发货";
            case 50:
                return "已完成";
            case 60:
                return "申请退款中";
            case 70:
                return "已退款";
            case -1:
                return "异常";
            case -10:
                return "已处理异常";
            default:
                return "";
        }
    }
}
