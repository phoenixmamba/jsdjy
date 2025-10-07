package com.centit.logstatistics.logstatisticsserver.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.logstatistics.logstatisticsserver.webmgr.dao.*;
import com.centit.logstatistics.logstatisticsserver.webmgr.po.*;
import com.centit.logstatistics.logstatisticsserver.webmgr.service.ShoppingOrderformService;
import com.centit.logstatistics.logstatisticsserver.webmgr.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  服务实现类
 * @Date : 2021-06-08
 **/
@Transactional
@Service
public class ShoppingOrderformServiceImpl implements ShoppingOrderformService {
    public static final Log log = LogFactory.getLog(ShoppingOrderformService.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingCouponDao shoppingCouponDao;

    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;

    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private ShoppingRechargeDao shoppingRechargeDao;

    @Resource
    private TOnOndemandDao tOnOndemandDao;

    @Resource
    private ParkOrderDao parkOrderDao;

    @Resource
    private TicketProjectDao ticketProjectDao;

    @Resource
    private TicketEventDao ticketEventDao;

    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;

    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    @Resource
    private TExportFileDao tExportFileDao;

    @Value("${order.orderState.anomalous}")
    private int orderStateAnomalous;

    @Value("${order.orderState.handAnomalous}")
    private int orderStateHandAnomalous;

    @Resource
    private  ThreadPoolTaskExecutor threadPoolExecutor;

    /**
     * 查询订单明细列表
     */
    @Override
    public JSONObject queryList(JSONObject reqJson) {
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
            bizDataJson.put("total",shoppingOrderformDao.queryTotalCount(reqMap));
            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryList(reqMap);
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
                orderDetail.setDeductionCouponPrice(shoppingOrderform.getDeductionCouponPrice());
                orderDetail.setDeductionMemberPrice(shoppingOrderform.getDeductionMemberPrice());   //会员权益抵扣金额
                orderDetail.setDeductionIntegral(shoppingOrderform.getDeductionIntegral());        //积分抵扣数量
                orderDetail.setDeductionIntegralPrice(shoppingOrderform.getDeductionIntegralPrice());    //积分抵扣金额
                orderDetail.setDeductionBalancePrice(shoppingOrderform.getDeductionBalancePrice());    //余额抵扣金额
                orderDetail.setPayPrice(shoppingOrderform.getPayPrice());                  //现金支付金额

                if(shoppingOrderform.getOrderStatus()==orderStateAnomalous||shoppingOrderform.getOrderStatus()==orderStateHandAnomalous){  //异常订单，需要到订单支付表中查询实际支付情况

                    if(shoppingOrderPay.getCouponStatus()==0||shoppingOrderPay.getCouponStatus()==2){
                        orderDetail.setDeductionCouponPriceState(1);
                    }

                    if(orderDetail.getDeductionMemberPrice().compareTo(BigDecimal.ZERO)==1){
                        orderDetail.setDeductionMemberPriceState(1);
                    }

                    if(shoppingOrderPay.getIntegralStatus()==0||shoppingOrderPay.getIntegralStatus()==2){
                        orderDetail.setDeductionIntegralState(1);
                        orderDetail.setDeductionIntegralPriceState(1);
                    }
                    if(shoppingOrderPay.getBalanceStatus()==0||shoppingOrderPay.getIntegralStatus()==2){
                        orderDetail.setDeductionBalancePriceState(1);    //余额抵扣金额
                    }
                    if(shoppingOrderPay.getCashStatus()==0||shoppingOrderPay.getCashStatus()==2){
                        orderDetail.setPayPriceState(1);
                    }
                }
                orderDetail.setPaymentId(shoppingOrderform.getPaymentId());   //现金支付方式Id
                orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
                orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）
                orderDetail.setOutOrderId(shoppingOrderform.getOutOrderId());  //第三方交互订单号 （麦座订单号/速停车账单号）
                //商品信息
                List<HashMap<String,Object>> goodsInfoList = getOrderGoods(shoppingOrderform.getId());
                orderDetail.setGoodsInfo(goodsInfoList);
                String goodsName ="";
                for(HashMap<String,Object> goodsMap:goodsInfoList){
                    goodsName +=goodsMap.get("goodsName")+";";
                }
                if(goodsName.length()>0){
                    goodsName = goodsName.substring(0,goodsName.length() - 1);
                }
                orderDetail.setGoodsName(goodsName);

                orderDetail.setUserId(shoppingOrderform.getUserId());
                orderDetail.setUserName(shoppingOrderform.getUserName());
                orderDetail.setMobile(shoppingOrderform.getMobile());

                objList.add(orderDetail);
            }
            bizDataJson.put("objList",objList);
            //统计总数
            HashMap<String, Object> sum= shoppingOrderformDao.querySumList(reqMap);
            //查询所有异常订单
            List<ShoppingOrderform> exceptionList = shoppingOrderformDao.queryExceptionList(reqMap);
            BigDecimal couponException = BigDecimal.ZERO;
            BigDecimal memberException = BigDecimal.ZERO;
            BigDecimal integralException = BigDecimal.ZERO;
            BigDecimal balanceException = BigDecimal.ZERO;
            BigDecimal cashException = BigDecimal.ZERO;
            for(ShoppingOrderform shoppingOrderform:exceptionList){
                //查询订单支付情况
                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setOfId(shoppingOrderform.getId());
                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);

                if(shoppingOrderPay.getCouponStatus()==0||shoppingOrderPay.getCouponStatus()==2){
                    couponException = couponException.add(shoppingOrderform.getDeductionCouponPrice());
                }

                if(shoppingOrderform.getDeductionMemberPrice().compareTo(BigDecimal.ZERO)==1){
                    memberException = memberException.add(shoppingOrderform.getDeductionMemberPrice());
                }

                if(shoppingOrderPay.getIntegralStatus()==0||shoppingOrderPay.getIntegralStatus()==2){
                    integralException = integralException.add(shoppingOrderform.getDeductionIntegralPrice());
                }
                if(shoppingOrderPay.getBalanceStatus()==0||shoppingOrderPay.getIntegralStatus()==2){
                    balanceException = balanceException.add(shoppingOrderform.getDeductionBalancePrice());
                }
                if(shoppingOrderPay.getCashStatus()==0||shoppingOrderPay.getCashStatus()==2){
                    cashException = cashException.add(shoppingOrderform.getPayPrice());
                }
            }

            BigDecimal totalPrice = new BigDecimal(sum.get("totalPrice").toString());

            BigDecimal deduction_coupon_price = new BigDecimal(sum.get("deduction_coupon_price").toString());
            BigDecimal deduction_member_price = new BigDecimal(sum.get("deduction_member_price").toString());
            BigDecimal deduction_integral_price = new BigDecimal(sum.get("deduction_integral_price").toString());
            BigDecimal deduction_balance_price = new BigDecimal(sum.get("deduction_balance_price").toString());
            BigDecimal pay_price = new BigDecimal(sum.get("pay_price").toString());

            sum.put("real_coupon_price",deduction_coupon_price.subtract(couponException));
            sum.put("real_member_price",deduction_member_price.subtract(memberException));
            sum.put("real_integral_price",deduction_integral_price.subtract(integralException));
            sum.put("real_balance_price",deduction_balance_price.subtract(balanceException));
            sum.put("real_pay_price",pay_price.subtract(cashException));

            sum.put("real_totalPrice",totalPrice.subtract(couponException.add(memberException).add(memberException).add(integralException).add(balanceException).add(cashException)));

            bizDataJson.put("sum",sum);
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
//    @Override
//    public void exportOrderList(JSONObject reqJson, HttpServletResponse response) {
//        JSONObject retJson = new JSONObject();
//        try {
//            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
//            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryList(reqMap);
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
//
//                orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
//                orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）
//                orderDetail.setOutOrderId(shoppingOrderform.getOutOrderId());  //第三方交互订单号 （麦座订单号/速停车账单号）
//                //商品信息
//                List<HashMap<String,Object>> goodsInfoList = getOrderGoods(shoppingOrderform.getId());
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
//                }
//                orderDetail.setSignInfo(signInfo);
//
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
//            //统计总数
//            HashMap<String, Object> sum= shoppingOrderformDao.querySumList(reqMap);
//
//            String sumStr = "订单总金额："+sum.get("totalPrice")
//                    +"  优惠券应扣："+sum.get("deduction_coupon_price")+"  会员权益抵扣："+sum.get("deduction_member_price")+"  积分支付抵扣："
//                    +sum.get("deduction_integral_price")+"  余额支付抵扣："+sum.get("deduction_balance_price")+"  现金支付金额："+sum.get("pay_price");
//
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
//            String fileName =new String(("账单明细_"+String.valueOf(System.currentTimeMillis()) + ".xls").getBytes(),"ISO-8859-1");
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
            tExportFile.setDataType("票款结算");
            tExportFileDao.insert(tExportFile);
//            ExecutorService fixPool = Executors.newFixedThreadPool(1);
//            ThreadPoolTaskExecutor fixPool = ThreadPool.getThreadPool();
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                        List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryList(reqMap);
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
                            List<HashMap<String,Object>> goodsInfoList = getOrderGoods(shoppingOrderform.getId());
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

                            objList.add(orderDetail);
                        }
                        //统计总数
                        HashMap<String, Object> sum= shoppingOrderformDao.querySumList(reqMap);

                        String sumStr = "订单总金额："+sum.get("totalPrice")
                                +"  优惠券应扣："+sum.get("deduction_coupon_price")+"  会员权益抵扣："+sum.get("deduction_member_price")+"  积分支付抵扣："
                                +sum.get("deduction_integral_price")+"  余额支付抵扣："+sum.get("deduction_balance_price")+"  现金支付金额："+sum.get("pay_price");

                        // 导出表的标题
                        String title =sumStr;
                        // 导出表的列名
                        String[] rowsName =new String[]{"订单编号","商品名称","商品数量","商品规格","演出日期","下单时间","订单类型","订单状态","订单金额","商品金额","运费金额","优惠券抵扣"
                                ,"优惠券信息","会员权益抵扣","积分支付抵扣","余额支付抵扣",
//                    "现金支付金额","现金支付方式",
                                "微信支付(大剧院)","微信支付(停车场)","支付宝支付",
                                "现金支付流水号","支付时间"
                                ,"会员姓名","会员手机号","第三方订单号","报名信息"};
                        List<Object[]> dataList = new ArrayList<Object[]>();
                        for(OrderDetail orderDetail:objList){
                            Object[] obj = new Object[25];
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
                            obj[16] = orderDetail.getWxPayPrice();
                            obj[17] = orderDetail.getWxPayPrice_park();
                            obj[18] = orderDetail.getAliPayPrice();
                            obj[19] = orderDetail.getPayOrderId();
                            obj[20] = orderDetail.getPayTime();
                            obj[21] = orderDetail.getUserName();
                            obj[22] = orderDetail.getMobile();
                            obj[23] = orderDetail.getOutOrderId();
                            obj[24] = orderDetail.getSignInfo();
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
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            retCode = "0";
            retMsg = "操作成功！";

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询充值订单明细列表
     */
    @Override
    public JSONObject queryRechargeList(JSONObject reqJson) {
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
            bizDataJson.put("total",shoppingOrderformDao.queryRechargeTotalCount(reqMap));
            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryRechargeList(reqMap);
            List<OrderDetail> objList = new ArrayList<>();
            for(ShoppingOrderform shoppingOrderform:orderforms){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOfId(shoppingOrderform.getId());
                orderDetail.setOrderId(shoppingOrderform.getOrderId());
                orderDetail.setAddTime(shoppingOrderform.getAddTime());
                orderDetail.setOrderType(shoppingOrderform.getOrderType());
                orderDetail.setOrderStatus(shoppingOrderform.getOrderStatus());
                orderDetail.setTotalPrice(shoppingOrderform.getTotalPrice());
//                orderDetail.setShipPrice(shoppingOrderform.getShipPrice());
//                orderDetail.setGoodsPrice(shoppingOrderform.getTotalPrice().subtract(shoppingOrderform.getShipPrice()));
//                orderDetail.setCouponId(shoppingOrderform.getCouponId());

                orderDetail.setPayPrice(shoppingOrderform.getPayPrice());                  //现金支付金额

                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setOfId(shoppingOrderform.getId());
                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);

                orderDetail.setPaymentId(shoppingOrderform.getPaymentId());   //现金支付方式Id


                orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
                orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）

                orderDetail.setUserId(shoppingOrderform.getUserId());
                orderDetail.setUserName(shoppingOrderform.getUserName());
                orderDetail.setMobile(shoppingOrderform.getMobile());

                objList.add(orderDetail);
            }
            bizDataJson.put("objList",objList);
            //统计总数
            HashMap<String, Object> sum= shoppingOrderformDao.queryRechargeSumList(reqMap);
            bizDataJson.put("sum",sum);
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
     * 导出充值订单明细列表
     */
    @Override
    public void exportRechargeOrderList(JSONObject reqJson, HttpServletResponse response) {
        JSONObject retJson = new JSONObject();
        try {
//            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
//            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
//            reqMap.put("startRow", (pageNo-1)*pageSize);
//            reqMap.put("pageSize", pageSize);
//            bizDataJson.put("total",shoppingOrderformDao.queryTotalCount(reqMap));
            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryRechargeList(reqMap);
            List<OrderDetail> objList = new ArrayList<>();
            for(ShoppingOrderform shoppingOrderform:orderforms){
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOfId(shoppingOrderform.getId());
                orderDetail.setOrderId(shoppingOrderform.getOrderId());
                orderDetail.setAddTime(shoppingOrderform.getAddTime());
                orderDetail.setOrderType(shoppingOrderform.getOrderType());
                orderDetail.setOrderStatus(shoppingOrderform.getOrderStatus());
                orderDetail.setTotalPrice(shoppingOrderform.getTotalPrice());


                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setOfId(shoppingOrderform.getId());
                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);

                orderDetail.setPayPrice(shoppingOrderform.getPayPrice());                  //现金支付金额
                orderDetail.setPaymentId(shoppingOrderform.getPaymentId());   //现金支付方式Id

                if(null !=shoppingOrderform.getPaymentId()&&(shoppingOrderform.getPaymentId().equals("20")||shoppingOrderform.getPaymentId().equals("21"))){   //微信支付
                    orderDetail.setWxPayPrice(orderDetail.getPayPrice());
                }
                if(null !=shoppingOrderform.getPaymentId()&&shoppingOrderform.getPaymentId().equals("22")){   //支付宝支付
                    orderDetail.setAliPayPrice(orderDetail.getPayPrice());
                }

                orderDetail.setPayTime(shoppingOrderform.getPayTime());       //支付时间
                orderDetail.setPayOrderId(shoppingOrderPay.getOutTradeNo());  //第三方支付订单号（微信/支付宝）

                orderDetail.setUserId(shoppingOrderform.getUserId());
                orderDetail.setUserName(shoppingOrderform.getUserName());
                orderDetail.setMobile(shoppingOrderform.getMobile());

                objList.add(orderDetail);
            }
            //统计总数
            HashMap<String, Object> sum= shoppingOrderformDao.queryRechargeSumList(reqMap);

            String sumStr = "充值总金额："+sum.get("totalPrice")+"  现金支付金额："+sum.get("pay_price");

            // 导出表的标题
            String title =sumStr;
            // 导出表的列名
            String[] rowsName =new String[]{"订单编号","下单时间","充值金额", "现金支付金额",
//                    "现金支付方式","现金支付流水号",
                    "微信支付金额","支付宝支付金额",
                    "支付时间","会员姓名","会员手机号"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(OrderDetail orderDetail:objList){
                Object[] obj = new Object[9];
                obj[0] = orderDetail.getOrderId();
                obj[1] = orderDetail.getAddTime();
                obj[2] = orderDetail.getTotalPrice();
//                obj[3] = orderDetail.getPayPrice();
//                if(null !=orderDetail.getPaymentId()){
//                    if(orderDetail.getPaymentId().equals("21")){
//                        obj[4] = "微信支付";
//                    }else if(orderDetail.getPaymentId().equals("22")){
//                        obj[4] = "支付宝支付";
//                    }else{
//                        obj[4] = "";
//                    }
//                }else{
//                    obj[4] = "";
//                }
                obj[3] = orderDetail.getWxPayPrice();
                obj[4] = orderDetail.getAliPayPrice();
                obj[5] = orderDetail.getPayOrderId();
                obj[6] = orderDetail.getPayTime();
                obj[7] = orderDetail.getUserName();
                obj[8] = orderDetail.getMobile();
                dataList.add(obj);
            }
            String fileName =new String(("余额充值明细_"+String.valueOf(System.currentTimeMillis()) + ".xls").getBytes(),"ISO-8859-1");
            String headStr = "attachment; filename=\"" + fileName + "\"";
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);
            OutputStream out = response.getOutputStream();
            ExportExcel ex = new ExportExcel(title, rowsName, dataList);
            try {
                ex.export(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error(e);
        }
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
            default:
                return "";
        }
    }

    /**
     * 获取订单关联的商品
     */
    public List<HashMap<String,Object>> getOrderGoods(String ofId){
        List<HashMap<String,Object>> goods = new ArrayList<>();
        try{
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(ofId);
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("ofId",ofId);
            reqMap.put("deleteStatus","0");
            List<ShoppingGoodscart> cartGoods = shoppingGoodscartDao.queryList(reqMap);

            for(ShoppingGoodscart shoppingGoodscart:cartGoods){
                HashMap<String,Object> map =new HashMap<>();
                map.put("cartType",shoppingGoodscart.getCartType());
                //充值订单
                if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.RECHARGE_CART_TYPE)){
                    ShoppingRecharge shoppingRecharge = new ShoppingRecharge();
                    shoppingRecharge.setId(shoppingGoodscart.getGoodsId());
                    shoppingRecharge =shoppingRechargeDao.queryDetail(shoppingRecharge);

                    map.put("goodsId",shoppingRecharge.getId());
                    map.put("photoId",Const.RECHARGE_ORDER_PHOTO);  //充值默认图片id
                    map.put("goodsName","余额充值");
                    map.put("moneyAmount",shoppingRecharge.getMoneyAmount());
                }
                //点播订单
                else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.VIDEO_CART_TYPE)){
                    TOnOndemand tOnOndemand = new TOnOndemand();
                    tOnOndemand.setId(shoppingGoodscart.getGoodsId());
                    tOnOndemand =tOnOndemandDao.queryDetail(tOnOndemand);

                    map.put("goodsId",tOnOndemand.getId());
                    map.put("photoUrl",tOnOndemand.getCoverfilepath());
                    map.put("goodsName",tOnOndemand.getTitle());
                }
                //停车缴费订单
                else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.PARK_CART_TYPE)){
                    ParkOrder parkOrder = new ParkOrder();
                    parkOrder.setOrderId(shoppingOrderform.getOrderId());
                    parkOrder =parkOrderDao.queryDetail(parkOrder);
                    map.put("photoId",Const.PARK_ORDER_PHOTO);  //停车缴费默认图片id
                    map.put("goodsName","停车缴费");
                    try{
                        map.put("goodsId",parkOrder.getOrderNo());

                        //订单列表展示车牌号和停车场信息
                        map.put("plateNo",parkOrder.getPlateNo());
                        map.put("parkName",parkOrder.getParkName());
                    }catch (Exception e){
                        e.printStackTrace();
                        map.put("goodsId","");

                        //订单列表展示车牌号和停车场信息
                        map.put("plateNo","");
                        map.put("parkName","");
                    }

                }
                //演出票订单
                else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.TICKET_CART_TYPE)){
                    TicketEvent ticketEvent = new TicketEvent();
                    ticketEvent.setEventId(shoppingGoodscart.getGoodsId());
                    ticketEvent =ticketEventDao.queryDetail(ticketEvent);
                    //根据场次信息查询项目信息
                    TicketProject ticketProject = new TicketProject();
                    ticketProject.setProjectId(ticketEvent.getProjectId());
                    ticketProject = ticketProjectDao.queryDetail(ticketProject);
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
                    shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                    map.put("goodsId",shoppingArtactivity.getId());
                    map.put("photoId",shoppingArtactivity.getMainPhotoId());
                    map.put("goodsName",shoppingArtactivity.getActivityName());

                    //报名信息
                    reqMap.put("ofId",ofId);
                    reqMap.put("activityId",shoppingArtactivity.getId());
                    List<ShoppingArtactivitySignupinfo> signupInfos = CommonInit.staticShoppingArtactivitySignupinfoDao.queryList(reqMap);
                    map.put("signupInfos",signupInfos);

                }else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.SHOPPING_PLAN_CART_TYPE)){
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                    //主体信息
                    shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                    map.put("goodsId",shoppingArtplan.getId());
                    map.put("photoId",shoppingArtplan.getMainPhotoId());
                    map.put("goodsName",shoppingArtplan.getActivityName());

                    //报名信息
                    reqMap.put("ofId",ofId);
                    reqMap.put("activityId",shoppingArtplan.getId());
                    List<ShoppingArtplanSignupinfo> signupInfos = CommonInit.staticShoppingArtplanSignupinfoDao.queryList(reqMap);
                    map.put("signupInfos",signupInfos);

                }else if(shoppingGoodscart.getCartType()!=null&&shoppingGoodscart.getCartType().equals(Const.SHOPPING_CLASS_CART_TYPE)){
                    ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                    shoppingArtclass.setId(shoppingGoodscart.getGoodsId());
                    //活动主体信息
                    shoppingArtclass = shoppingArtclassDao.queryDetail(shoppingArtclass);
                    map.put("goodsId",shoppingArtclass.getId());
                    map.put("photoId",shoppingArtclass.getMainPhotoId());
                    map.put("goodsName",shoppingArtclass.getClassName());

                }else{
                    ShoppingGoods shoppingGoods=new ShoppingGoods();
                    shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                    shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

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
        }catch (Exception e){
            e.printStackTrace();
        }
        return goods;
    }

    /**
     * 账款核对表
     */
    @Override
    public JSONObject queryMoneyList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";


        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            List<HashMap<String, Object>> objList= shoppingOrderformDao.queryMoneyList(reqMap);
            bizDataJson.put("objList",objList);
            HashMap<String, Object> sumObj = new HashMap<String, Object>();
            int num=0;
            BigDecimal totalprice=BigDecimal.ZERO;
            BigDecimal goods_price=BigDecimal.ZERO;
            BigDecimal ship_price=BigDecimal.ZERO;
            BigDecimal deduction_coupon_price=BigDecimal.ZERO;
            BigDecimal deduction_member_price=BigDecimal.ZERO;
            BigDecimal deduction_integral_price=BigDecimal.ZERO;
            BigDecimal deduction_balance_price=BigDecimal.ZERO;
            BigDecimal pay_price=BigDecimal.ZERO;
            BigDecimal ali_pay_price=BigDecimal.ZERO;
            BigDecimal wx_pay_price=BigDecimal.ZERO;

            for(HashMap<String, Object> objMap:objList){
                num = num+Integer.valueOf(objMap.get("num").toString());
                totalprice = totalprice.add(new BigDecimal(objMap.get("totalprice").toString()));
                goods_price = goods_price.add(new BigDecimal(objMap.get("goods_price").toString()));
                ship_price = ship_price.add(new BigDecimal(objMap.get("ship_price").toString()));
                deduction_coupon_price = deduction_coupon_price.add(new BigDecimal(objMap.get("deduction_coupon_price").toString()));
                deduction_member_price = deduction_member_price.add(new BigDecimal(objMap.get("deduction_member_price").toString()));
                deduction_integral_price = deduction_integral_price.add(new BigDecimal(objMap.get("deduction_integral_price").toString()));
                deduction_balance_price = deduction_balance_price.add(new BigDecimal(objMap.get("deduction_balance_price").toString()));
                pay_price = pay_price.add(new BigDecimal(objMap.get("pay_price").toString()));
                ali_pay_price = ali_pay_price.add(new BigDecimal(objMap.get("ali_pay_price").toString()));
                wx_pay_price = wx_pay_price.add(new BigDecimal(objMap.get("wx_pay_price").toString()));
            }
            sumObj.put("payDay","合计");
            sumObj.put("num",num);
            sumObj.put("totalprice",totalprice);
            sumObj.put("goods_price",goods_price);
            sumObj.put("ship_price",ship_price);
            sumObj.put("deduction_coupon_price",deduction_coupon_price);
            sumObj.put("deduction_member_price",deduction_member_price);
            sumObj.put("deduction_integral_price",deduction_integral_price);
            sumObj.put("deduction_balance_price",deduction_balance_price);
            sumObj.put("pay_price",pay_price);
            sumObj.put("ali_pay_price",ali_pay_price);
            sumObj.put("wx_pay_price",wx_pay_price);
            objList.add(sumObj);
//            bizDataJson.put("sumObj",sumObj);
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
     * 导出账款核对表
     */
    @Override
    public JSONObject exportMoneyList(JSONObject reqJson, HttpServletResponse response) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();

        try {
            TExportFile tExportFile = new TExportFile();
            String fileId = String.valueOf(System.currentTimeMillis());
            tExportFile.setId(fileId);
            tExportFile.setDataType("账款核对");
            tExportFileDao.insert(tExportFile);

            ExecutorService fixPool = Executors.newFixedThreadPool(1);
            fixPool.execute(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                    List<HashMap<String, Object>> objList= shoppingOrderformDao.queryMoneyList(reqMap);
                    HashMap<String, Object> sumObj = new HashMap<String, Object>();
                    int num=0;
                    BigDecimal totalprice=BigDecimal.ZERO;
                    BigDecimal goods_price=BigDecimal.ZERO;
                    BigDecimal ship_price=BigDecimal.ZERO;
                    BigDecimal deduction_coupon_price=BigDecimal.ZERO;
                    BigDecimal deduction_member_price=BigDecimal.ZERO;
                    BigDecimal deduction_integral_price=BigDecimal.ZERO;
                    BigDecimal deduction_balance_price=BigDecimal.ZERO;
                    BigDecimal pay_price=BigDecimal.ZERO;
                    BigDecimal ali_pay_price=BigDecimal.ZERO;
                    BigDecimal wx_pay_price=BigDecimal.ZERO;

                    for(HashMap<String, Object> objMap:objList){
                        num = num+Integer.valueOf(objMap.get("num").toString());
                        totalprice = totalprice.add(new BigDecimal(objMap.get("totalprice").toString()));
                        goods_price = goods_price.add(new BigDecimal(objMap.get("goods_price").toString()));
                        ship_price = ship_price.add(new BigDecimal(objMap.get("ship_price").toString()));
                        deduction_coupon_price = deduction_coupon_price.add(new BigDecimal(objMap.get("deduction_coupon_price").toString()));
                        deduction_member_price = deduction_member_price.add(new BigDecimal(objMap.get("deduction_member_price").toString()));
                        deduction_integral_price = deduction_integral_price.add(new BigDecimal(objMap.get("deduction_integral_price").toString()));
                        deduction_balance_price = deduction_balance_price.add(new BigDecimal(objMap.get("deduction_balance_price").toString()));
                        pay_price = pay_price.add(new BigDecimal(objMap.get("pay_price").toString()));
                        ali_pay_price = ali_pay_price.add(new BigDecimal(objMap.get("ali_pay_price").toString()));
                        wx_pay_price = wx_pay_price.add(new BigDecimal(objMap.get("wx_pay_price").toString()));
                    }
                    sumObj.put("payDay","合计");
                    sumObj.put("num",num);
                    sumObj.put("totalprice",totalprice);
                    sumObj.put("goods_price",goods_price);
                    sumObj.put("ship_price",ship_price);
                    sumObj.put("deduction_coupon_price",deduction_coupon_price);
                    sumObj.put("deduction_member_price",deduction_member_price);
                    sumObj.put("deduction_integral_price",deduction_integral_price);
                    sumObj.put("deduction_balance_price",deduction_balance_price);
                    sumObj.put("pay_price",pay_price);
                    sumObj.put("ali_pay_price",ali_pay_price);
                    sumObj.put("wx_pay_price",wx_pay_price);
                    objList.add(sumObj);

                    // 导出表的标题
                    String title ="账款数据";
                    // 导出表的列名
                    String[] rowsName =new String[]{"日期","订单数","订单金额","商品金额","运费金额","优惠券抵扣"
                            ,"会员权益抵扣","积分支付抵扣","余额支付抵扣","支付宝支付","微信支付"};
                    List<Object[]> dataList = new ArrayList<Object[]>();
                    for(HashMap<String, Object> objMap:objList){
                        Object[] obj = new Object[11];
                        obj[0] = objMap.get("payDay");
                        obj[1] = objMap.get("num");
                        obj[2] = objMap.get("totalprice");
                        obj[3] = objMap.get("goods_price");
                        obj[4] = objMap.get("ship_price");
                        obj[5] = objMap.get("deduction_coupon_price");
                        obj[6] = objMap.get("deduction_member_price");
                        obj[7] = objMap.get("deduction_integral_price");
                        obj[8] = objMap.get("deduction_balance_price");
                        obj[9] = objMap.get("ali_pay_price");
                        obj[10] = objMap.get("wx_pay_price");
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

    /**
     * 查询指定商品订单列表
     */
    @Override
    public JSONObject queryGoodsOrderList(JSONObject reqJson) {
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
            bizDataJson.put("total",shoppingOrderformDao.queryTotalCount(reqMap));
            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryList(reqMap);
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
                orderDetail.setGoodsInfo(getOrderGoods(shoppingOrderform.getId()));
                List<HashMap<String,Object>> goodsList = orderDetail.getGoodsInfo();
                for(HashMap<String,Object> goodsMap:goodsList){
                    if(goodsMap.get("goodsId").equals(reqJson.get("goodsId"))&&goodsMap.get("goodsType")==reqJson.getInteger("cartType")){
                        orderDetail.setGoodsCount((orderDetail.getGoodsCount()!=null?orderDetail.getGoodsCount():0)+Integer.valueOf(goodsMap.get("goodsCount").toString()));
                    }
                }

                orderDetail.setUserId(shoppingOrderform.getUserId());
                orderDetail.setUserName(shoppingOrderform.getUserName());
                orderDetail.setMobile(shoppingOrderform.getMobile());

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
     * 导出指定商品订单列表
     */
    @Override
    public void exportGoodsOrderList(JSONObject reqJson, HttpServletResponse response) {

        try {

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);

            List<ShoppingOrderform> orderforms= shoppingOrderformDao.queryList(reqMap);
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
//                //商品信息
//                orderDetail.setGoodsInfo(getOrderGoods(shoppingOrderform.getId()));
                List<HashMap<String,Object>> goodsList = getOrderGoods(shoppingOrderform.getId());
                orderDetail.setGoodsInfo(goodsList);
                String signInfo="";
                for(HashMap<String,Object> goodsMap:goodsList){
                    if(goodsMap.get("goodsId").equals(reqJson.get("goodsId"))&&goodsMap.get("goodsType")==reqJson.getInteger("cartType")){
                        orderDetail.setGoodsCount((orderDetail.getGoodsCount()!=null?orderDetail.getGoodsCount():0)+Integer.valueOf(goodsMap.get("goodsCount").toString()));
                    }

                    if(null !=goodsMap.get("signupInfos")){   //报名信息
                        String cartType= goodsMap.get("cartType").toString();
                        if(cartType.equals(Const.SHOPPING_ACT_CART_TYPE)){
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

                        }else if(cartType.equals(Const.SHOPPING_PLAN_CART_TYPE)){
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
                orderDetail.setUserId(shoppingOrderform.getUserId());
                orderDetail.setUserName(shoppingOrderform.getUserName());
                orderDetail.setMobile(shoppingOrderform.getMobile());

                objList.add(orderDetail);
            }

            // 导出表的标题
            String title ="商品数量:"+shoppingGoodscartDao.queryGoodsCount(reqMap)+",订单数量:"+shoppingOrderformDao.queryTotalCount(reqMap);
            // 导出表的列名
            String[] rowsName =new String[]{"订单编号","下单时间","商品数量","订单状态","订单金额","商品金额","运费金额","优惠券抵扣"
                    ,"优惠券信息","会员权益抵扣","积分支付抵扣","余额支付抵扣","现金支付金额","现金支付方式","现金支付流水号","支付时间"
                    ,"会员姓名","会员手机号","第三方订单号","报名信息","商品规格"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(OrderDetail orderDetail:objList){
                Object[] obj = new Object[21];
                obj[0] = orderDetail.getOrderId();
                obj[1] = orderDetail.getAddTime();
                obj[2] = orderDetail.getGoodsCount();
                obj[3] = getOrderStatus(orderDetail.getOrderStatus());
                obj[4] = orderDetail.getTotalPrice();
                obj[5] = orderDetail.getGoodsPrice();
                obj[6] = orderDetail.getShipPrice();
                obj[7] = orderDetail.getDeductionCouponPrice();
                if(null !=orderDetail.getCouponInfo()){
                    obj[8] = orderDetail.getCouponInfo().getRight_Display();
                }else{
                    obj[8] ="";
                }
                obj[9] = orderDetail.getDeductionMemberPrice();
                obj[10] = orderDetail.getDeductionIntegralPrice();
                obj[11] = orderDetail.getDeductionBalancePrice();
                obj[12] = orderDetail.getPayPrice();
                if(null !=orderDetail.getPaymentId()){
                    if(orderDetail.getPaymentId().equals("21")){
                        obj[13] = "微信支付";
                    }else if(orderDetail.getPaymentId().equals("22")){
                        obj[13] = "支付宝支付";
                    }else{
                        obj[13] = "";
                    }
                }else{
                    obj[13] = "";
                }
                obj[14] = orderDetail.getPayOrderId();
                obj[15] = orderDetail.getPayTime();
                obj[16] = orderDetail.getUserName();
                obj[17] = orderDetail.getMobile();
                obj[18] = orderDetail.getOutOrderId();
                obj[19] = orderDetail.getSignInfo();
                //商品规格信息
                List<HashMap<String,Object>> goodsInfo =orderDetail.getGoodsInfo();
                String specInfo="";
                for(HashMap<String,Object> specMap:goodsInfo){
                    if(specMap.get("spec")!=null&&!"".equals(specMap.get("spec"))){
                        specInfo=specInfo+specMap.get("spec")+" 数量："+specMap.get("goodsCount")+"\r\n";
                    }
                }
                obj[20] = specInfo;
                dataList.add(obj);
            }
            String fileName =new String(("商品统计_"+String.valueOf(System.currentTimeMillis()) + ".xls").getBytes(),"ISO-8859-1");
            String headStr = "attachment; filename=\"" + fileName + "\"";
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);
            OutputStream out = response.getOutputStream();
            ExportExcel ex = new ExportExcel(title, rowsName, dataList);
            try {
                ex.export(out);
            } catch (Exception e) {
                e.printStackTrace();
            }
            out.flush();
            out.close();

        } catch (Exception e) {
            log.error(e);
        }

    }
}
