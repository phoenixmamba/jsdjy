package com.centit.shopping.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.InvoiceService;
import com.centit.shopping.biz.service.TicketHolderService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.MZService;
import com.centit.shopping.utils.NNService;
import com.centit.shopping.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
public class InvoiceServiceImpl implements InvoiceService {
    public static final Log log = LogFactory.getLog(InvoiceService.class);

    @Resource
    private TInvoiceTicketDao tInvoiceTicketDao;

    @Resource
    private TInvoiceDao tInvoiceDao;

    @Resource
    private TInvoiceHeaderDao tInvoiceHeaderDao;

    @Resource
    private TInvoicePushDao tInvoicePushDao;


    /**
     * 查询可开票的演出订单列表
     */
    @Override
    public JSONObject queryTicketOrderList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");

            String userId = reqJson.getString("userId");   //userId
            //查询该用户已经开过票或正在开票中的演出订单
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("userid", userId);
            List<TInvoiceTicket> tickets= tInvoiceTicketDao.queryUserInvoiceTicketList(reqMap);
            List<String> orderIds = new ArrayList<>();
            for(TInvoiceTicket tInvoiceTicket:tickets){
                orderIds.add(tInvoiceTicket.getMzOrderId());
            }

            String mzUserId = CommonUtil.getMzUserId(userId);
            JSONArray resArray =MZService.getorderList(mzUserId);
            List<JSONObject> dataArray= new ArrayList<>();
            for(int i=0;i<resArray.size();i++){
                JSONObject resObj =  resArray.getJSONObject(i);
                String mz_order_id = resObj.getString("mz_order_id");  //麦座订单id
                int order_state = resObj.getInteger("order_state");   //订单状态；1=订单未完成，2=订单已完成，3=订单已关闭
                int order_pay_state = resObj.getInteger("order_pay_state");   //订单支付状态；1=已支付，2=待支付
                int refund_order_state = resObj.getInteger("refund_order_state");   //订单退单状态；退单状态：1=未退单，2=部分退单，3=已退单，4=退单申请中
                if(order_state==2&&order_pay_state==1&&refund_order_state==1&&!orderIds.contains(mz_order_id)){
//                    //过滤掉最后一场演出时间距今已经超过7天的订单
//                    JSONArray eventList = resObj.getJSONObject("order_event_list").getJSONArray("order_event_v_o");
//                    String maxTimeStr="";
//                    for(int j=0;j<eventList.size();j++){
//                        JSONObject eventObj = eventList.getJSONObject(j);
//                        String event_start_time = eventObj.getString("event_start_time");
//                        if(maxTimeStr.trim().equals("")||differentMilesBetween2Times(event_start_time,maxTimeStr)>0){
//                            maxTimeStr=event_start_time;
//                        }
//                    }
//                    if(differentDays(maxTimeStr)<7){
//                        dataArray.add(resObj);
//                    }
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
                    String mz_order_id = dataObj.getString("mz_order_id");
                    String mz_user_id = dataObj.getString("mz_user_id");
                    obj.put("id",dataObj.getString("mz_order_id"));   //订单id
                    obj.put("orderId",dataObj.getString("mz_order_id"));   //订单id
                    obj.put("orderTime",dataObj.getString("create_order_time"));   //订单时间
                    obj.put("count",dataObj.getInteger("order_goods_count"));  //商品数量
                    //场次信息
                    JSONArray eventList = dataObj.getJSONObject("order_event_list").getJSONArray("order_event_v_o");
                    obj.put("eventList",eventList);

                    //现金金额
                    JSONObject detailObj = MZService.getMzOrderDetail(mz_user_id,mz_order_id);
                    int online_payment_money_fen = detailObj.getInteger("online_payment_money_fen");  //在线支付的金额,单位：分
                    BigDecimal onlinePayMoney = new BigDecimal(online_payment_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                    obj.put("onlinePayMoney",onlinePayMoney);

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
     * 获取我的默认发票抬头
     */
    @Override
    public JSONObject myDefaultInvoicHeader(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");   //userId
            String headType = reqJson.getString("headType");   //抬头类型 '0':企业单位;'1':个人/非企业单位
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("userId", userId);
            reqMap.put("headType", headType);
            List<TInvoiceHeader> headers= tInvoiceHeaderDao.queryList(reqMap);
            TInvoiceHeader defaultHeader =null;
            for(TInvoiceHeader shoppingInvoiceHeader:headers){
                if(shoppingInvoiceHeader.getIsDefault().equals("Y")){  //默认抬头
                    defaultHeader = shoppingInvoiceHeader;
                    break;
                }
            }
            //如果用户没有设置默认抬头，则使用最近的抬头最为默认抬头
            if(!headers.isEmpty()&&null==defaultHeader){
                defaultHeader = headers.get(0);
            }
            bizDataJson.put("header",defaultHeader);
            //获取最近使用过推送邮箱或手机号
            List<TInvoicePush> pushList = tInvoicePushDao.queryUserPushList(reqMap);
            if(!pushList.isEmpty()){
                bizDataJson.put("pushEmail",pushList.get(0).getPushEmail());
                bizDataJson.put("pushPhone",pushList.get(0).getPushPhone());
            }else{
                bizDataJson.put("pushEmail",null);
                bizDataJson.put("pushPhone",null);
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
     * 获取我的发票抬头列表
     */
    @Override
    public JSONObject myInvoicHeaderList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");   //userId
            String headType = reqJson.getString("headType");   //抬头类型 '0':企业单位;'1':个人/非企业单位
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("userId", userId);
            reqMap.put("headType", headType);
            List<TInvoiceHeader> headers= tInvoiceHeaderDao.queryList(reqMap);

            bizDataJson.put("objList",headers);
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
     * 添加新的发票抬头
     */
    @Override
    public JSONObject addInvoiceHeader(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            TInvoiceHeader header = JSON.parseObject(reqJson.toJSONString(), TInvoiceHeader.class);
            if(header.getIsDefault().equals("Y")){  //如果该抬头是默认抬头，则要将其它已有的抬头设置为非默认
                tInvoiceHeaderDao.setOtherHeadersNotDefault(header);
            }
            tInvoiceHeaderDao.insert(header);

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
     * 修改发票抬头
     */
    @Override
    public JSONObject editTicketHeader(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            TInvoiceHeader header = JSON.parseObject(reqJson.toJSONString(), TInvoiceHeader.class);
            if(header.getIsDefault().equals("Y")){  //如果该抬头是默认抬头，则要将其它已有的抬头设置为非默认
                tInvoiceHeaderDao.setOtherHeadersNotDefault(header);
            }
            tInvoiceHeaderDao.update(header);

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
     * 删除我的发票抬头
     */
    @Override
    public JSONObject delTicketHeader(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            TInvoiceHeader header = JSON.parseObject(reqJson.toJSONString(), TInvoiceHeader.class);

            tInvoiceHeaderDao.delete(header);

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
     * 演出开票
     */
    @Override
    public JSONObject addTicketInvoice(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            TInvoice tInvoice = JSON.parseObject(reqJson.toJSONString(), TInvoice.class);
            tInvoice.setInvoiceType("1");  //线上
            tInvoice.setInvoiceLine("p");  //普票
            tInvoice.setOrderType("1");    //演出

            //校验开票金额是否正确
            BigDecimal invoiceAmount = tInvoice.getInvoiceAmount();

            JSONArray orderArray = reqJson.getJSONArray("orders");
            String orderNoStr="";
            String invoiceDate="";
            for(int i=0;i<orderArray.size();i++){
                JSONObject obj = orderArray.getJSONObject(i);
                String mzOrderId = obj.getString("mzOrderId");
                orderNoStr+=mzOrderId;
                invoiceDate=obj.getString("orderTime");
            }

            JSONObject reqObj = new JSONObject();
            reqObj.put("buyerName",tInvoice.getBuyerName());    //企业名称/个人
            if(StringUtil.isNotNull(tInvoice.getBuyerTaxNum())){
                reqObj.put("buyerTaxNum",tInvoice.getBuyerTaxNum());    //税号
            }
            if(StringUtil.isNotNull(tInvoice.getBuyerTel())){
                reqObj.put("buyerTel",tInvoice.getBuyerTel());    //购方电话
            }
            if(StringUtil.isNotNull(tInvoice.getBuyerAddress())){
                reqObj.put("buyerAddress",tInvoice.getBuyerAddress());    //购方地址
            }
            if(StringUtil.isNotNull(tInvoice.getBuyerBank())||StringUtil.isNotNull(tInvoice.getBuyerAccount())){
                reqObj.put("buyerAccount",StringUtil.isNotNull(tInvoice.getBuyerBank())?"":tInvoice.getBuyerBank()+(StringUtil.isNotNull(tInvoice.getBuyerAccount())?"":tInvoice.getBuyerAccount()));    //购方银行账号及开户行地址
            }
            reqObj.put("salerTaxNum", Const.INVOICE_TAXNUM);    //销方税号
            reqObj.put("salerTel", Const.INVOICE_SALERTEL);    //销方电话
            reqObj.put("salerAddress", Const.INVOICE_SALERADDRESS);    //销方地址
            reqObj.put("salerAccount", Const.INVOICE_SALERACCOUNT);    //销方开户行账号和开户行地址
            reqObj.put("orderNo", orderNoStr);    //订单号
            reqObj.put("invoiceDate", invoiceDate);    //订单时间
            reqObj.put("checker", Const.INVOICE_CHECKER);    //复核人
            reqObj.put("payee", Const.INVOICE_PAYEE);    //收款人
            reqObj.put("clerk", Const.INVOICE_CLERK);    //开票员
            if(StringUtil.isNotNull(reqJson.get("pushEmail"))&&StringUtil.isNotNull(reqJson.get("pushPhone"))){
                reqObj.put("pushMode", "2");    //邮箱和手机
                reqObj.put("buyerPhone", reqJson.getString("pushPhone"));    //手机号
                reqObj.put("email", reqJson.getString("pushEmail"));    //邮箱
            }else if(StringUtil.isNotNull(reqJson.get("pushPhone"))){
                reqObj.put("pushMode", "1");    //手机
                reqObj.put("buyerPhone", reqJson.getString("pushPhone"));    //手机号
            }else if(StringUtil.isNotNull(reqJson.get("pushEmail"))){
                reqObj.put("pushMode", "0");    //邮箱
                reqObj.put("email", reqJson.getString("pushEmail"));    //邮箱
            }
            reqObj.put("invoiceType", "1");    //开票类型：1:蓝票;2:红票
            reqObj.put("invoiceLine", "p");    //发票种类： p,普通发票(电票)(默认);c,普通发票(纸票);s,专用发票;e,收购发票(电票);f,收购发票(纸质);r,普通发票(卷式)
            //发票商品明细
            JSONObject invObj = new JSONObject();
            invObj.put("goodsCode",Const.INVOICE_GOODSCODE);    //税收分类编码
            invObj.put("goodsName",Const.INVOICE_GOODSNAME);    //商品名称
            invObj.put("withTaxFlag","1");    //单价含税标志：0:不含税,1:含税
            invObj.put("price",String.valueOf(invoiceAmount));    //单价
            invObj.put("num","1");    //数量
            invObj.put("taxRate",Const.INVOICE_TAXRATE);    //税率
            JSONArray invoiceDetail = new JSONArray();
            invoiceDetail.add(invObj);
            reqObj.put("invoiceDetail", invoiceDetail);    //发票明细
            JSONObject orderObj = new JSONObject();
            orderObj.put("order",reqObj);
            //            //模拟开票成功，返回线上开票流水号
//            String invoiceSerialNum = StringUtil.randomNum(20);

            //提交开票
            String invoiceSerialNum = NNService.addInvoice(orderObj);
            if(null !=invoiceSerialNum){
                tInvoice.setInvoiceSerialNum(invoiceSerialNum);
//            //利用开票流水号，调用接口获取开票详情，获取发票号码和开票时间等信息
//            String invoiceNo =StringUtil.randomNum(8);    //发票号码
//            tInvoice.setInvoiceNo(invoiceNo);
                String invoiceTime = StringUtil.nowTimeString();  //开票时间
                tInvoice.setInvoiceTime(invoiceTime);

                //开票状态为开票中
                //临时将开票状态都置为开票完成
                tInvoice.setInvoiceStatus("20");
                tInvoiceDao.insert(tInvoice);
                String invoiceId = tInvoice.getId();
                if(null !=reqJson.get("saveHeader")&&reqJson.getString("saveHeader").equals("Y")){   //保存新的抬头信息
                    TInvoiceHeader header = new TInvoiceHeader();
                    header.setUserId(tInvoice.getUserid());
                    if(null!=tInvoice.getBuyerTaxNum()&&!"".equals(tInvoice.getBuyerTaxNum())){   //有税号则为企业类型抬头
                        header.setHeadType("0");
                        header.setTaxNum(tInvoice.getBuyerTaxNum());
                    }else{
                        header.setHeadType("1");
                    }
                    header.setCompanyName(tInvoice.getBuyerName());
                    header.setCompanyAddress(tInvoice.getBuyerAddress());
                    header.setCompanyTel(tInvoice.getBuyerTel());
                    header.setCompanyBank(tInvoice.getBuyerBank());
                    header.setCompanyAccount(tInvoice.getBuyerAccount());
                    tInvoiceHeaderDao.insert(header);
                }
                //保存本次开票推送记录
                //推送邮箱和手机号，二者不可全为空
                String pushEmail = reqJson.get("pushEmail")==null?"":reqJson.getString("pushEmail");
                String pushPhone = reqJson.get("pushPhone")==null?"":reqJson.getString("pushPhone");
                TInvoicePush tInvoicePush = new TInvoicePush();
                tInvoicePush.setInvoiceId(invoiceId);
                tInvoicePush.setPushEmail(pushEmail);
                tInvoicePush.setPushPhone(pushPhone);
                tInvoicePushDao.insert(tInvoicePush);

                //保存本次开票关联的订单信息
//            String mzUserId = reqJson.getString("mzUserId");  //用户麦座id

                for(int i=0;i<orderArray.size();i++){
                    JSONObject obj = orderArray.getJSONObject(i);
                    String mzOrderId = obj.getString("mzOrderId");
                    JSONArray eventList =obj.getJSONArray("eventList");

                    Set<String> projectIds = new HashSet<>();
                    for(int j=0;j<eventList.size();j++){
                        JSONObject eventObj = eventList.getJSONObject(j);
                        projectIds.add(eventObj.getString("project_id"));
                    }

                    for(String projectId:projectIds){
                        TInvoiceTicket tInvoiceTicket = new TInvoiceTicket();
                        tInvoiceTicket.setInvoiceId(invoiceId);
                        tInvoiceTicket.setUserid(tInvoice.getUserid());
                        tInvoiceTicket.setMzOrderId(mzOrderId);
                        tInvoiceTicket.setProjectId(projectId);
                        tInvoiceTicketDao.insert(tInvoiceTicket);
                    }
                }

                retCode = "0";
                retMsg = "操作成功！";
            }else{
                retMsg = "提交开票失败，请稍后重试或联系客服处理！";
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
     * 获取我的开票历史
     */
    @Override
    public JSONObject queryMyInvoiceHistoryList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");

            String userId = reqJson.getString("userId");   //userId
            //查询该用户线上开票记录
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("userid", userId);
            reqMap.put("invoiceType","1");  //只获取线上开票记录
            bizDataJson.put("total",tInvoiceDao.queryListCount(reqMap));
            List<TInvoice> tInvoiceList= tInvoiceDao.queryList(reqMap);
            List<HashMap<String, Object>> resList = new ArrayList<>();
            for(TInvoice tInvoice:tInvoiceList){
                HashMap<String, Object> resMap = new HashMap<>();
                resMap.put("invoiceId",tInvoice.getId());
                resMap.put("orderType",tInvoice.getOrderType());   //订单类型 1：演出；2：充值；3：停车；4：文创
                resMap.put("invoiceTime",tInvoice.getInvoiceTime());  //开票时间
                resMap.put("invoiceAmount",tInvoice.getInvoiceAmount());  //开票金额，单位元
                //如果开票状态不是已完成或已失败，则需要向诺诺平台查询开票实时状态
                if(!tInvoice.getInvoiceStatus().equals("2")&&!tInvoice.getInvoiceStatus().equals("22")&&!tInvoice.getInvoiceStatus().equals("24")){
                    //查询开票结果
                    JSONObject reqObj = new JSONObject();
                    String[] serialNos =tInvoice.getInvoiceSerialNum().split(",");
                    reqObj.put("serialNos",serialNos);
                    JSONObject dataObj =NNService.queryResult(reqObj);
                    if(null !=dataObj){
                        resMap.put("invoiceStatus",dataObj.getString("status"));  //开票状态
                        if(!dataObj.getString("status").equals(tInvoice.getInvoiceStatus())){  //状态发生变化时，更新数据库信息
                            //向数据库更新开票信息
                            tInvoice.setInvoiceStatus(dataObj.getString("status"));
                            tInvoice.setInvoiceCode(dataObj.get("invoiceCode")==null?"":dataObj.getString("invoiceCode"));  //发票代码
                            tInvoice.setInvoiceNo(dataObj.get("invoiceNo")==null?"":dataObj.getString("invoiceNo"));   //发票号码
                            tInvoice.setImgUrls(dataObj.get("imgUrls")==null?"":dataObj.getString("imgUrls"));   //发票图片地址
                            tInvoiceDao.update(tInvoice);
                        }
                    }else{
                        resMap.put("invoiceStatus",tInvoice.getInvoiceStatus());  //开票状态
                    }
                }else{
                    resMap.put("invoiceStatus",tInvoice.getInvoiceStatus());  //开票状态
                }

                resList.add(resMap);
            }

            bizDataJson.put("objList",resList);

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
     * 获取开票详情
     */
    @Override
    public JSONObject queryMyInvoiceDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String invoiceId = reqJson.getString("invoiceId");
            TInvoice tInvoice = new TInvoice();
            tInvoice.setId(invoiceId);
            tInvoice = tInvoiceDao.queryDetail(tInvoice);
            //如果开票状态不是已完成或已失败，则需要向诺诺平台查询开票实时状态
            if(!tInvoice.getInvoiceStatus().equals("2")&&!tInvoice.getInvoiceStatus().equals("22")&&!tInvoice.getInvoiceStatus().equals("24")){   //如果开票状态不是已完成或已失败，则需要向诺诺平台查询开票实时状态
                //查询开票结果
                JSONObject reqObj = new JSONObject();
                String[] serialNos =tInvoice.getInvoiceSerialNum().split(",");
                reqObj.put("serialNos",serialNos);
                JSONObject dataObj =NNService.queryResult(reqObj);
                if(null !=dataObj){
                    if(!dataObj.getString("status").equals(tInvoice.getInvoiceStatus())){  //状态发生变化时，更新数据库信息
                        //向数据库更新开票信息
                        tInvoice.setInvoiceStatus(dataObj.getString("status"));
                        tInvoice.setInvoiceCode(dataObj.get("invoiceCode")==null?"":dataObj.getString("invoiceCode"));  //发票代码
                        tInvoice.setInvoiceNo(dataObj.get("invoiceNo")==null?"":dataObj.getString("invoiceNo"));   //发票号码
                        tInvoice.setImgUrls(dataObj.get("imgUrls")==null?"":dataObj.getString("imgUrls"));   //发票图片地址
                        tInvoiceDao.update(tInvoice);
                    }
                }
            }

            HashMap<String, Object> resMap = new HashMap<>();
            resMap.put("invoiceId",invoiceId);
            resMap.put("pictureUrl",tInvoice.getImgUrls());   //发票图片地址
            resMap.put("buyerName",tInvoice.getBuyerName());  //公司名称
            resMap.put("buyerTaxNum",tInvoice.getBuyerTaxNum());  //公司税号
            resMap.put("buyerAddress",tInvoice.getBuyerAddress());  //公司地址
            resMap.put("buyerTel",tInvoice.getBuyerTel());    //公司电话
            resMap.put("buyerBank",tInvoice.getBuyerBank());   //公司开户行
            resMap.put("buyerAccount",tInvoice.getBuyerAccount());    //开户行账号

            resMap.put("invoiceAmount",tInvoice.getInvoiceAmount());   //发票金额
            resMap.put("invoiceTime",tInvoice.getInvoiceTime());   //申请时间

            HashMap<String, Object> pushMap = new HashMap<>();
            pushMap.put("invoiceId",invoiceId);
            List<TInvoicePush> pushs=tInvoicePushDao.queryList(pushMap);
            if(!pushs.isEmpty()){
                resMap.put("pushPhone",pushs.get(0).getPushPhone());   //接收手机
                resMap.put("pushEmail",pushs.get(0).getPushEmail());   //接收邮箱
            }


            //发票关联的订单数量
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("invoiceId",invoiceId);
            resMap.put("orderCount",tInvoiceTicketDao.queryInvoiceOrderCount(reqMap));   //查询订单数量

            bizDataJson.put("data",resMap);

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
     * 获取演出发票的订单信息
     */
    @Override
    public JSONObject queryTicketInvoiceOrders(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String invoiceId = reqJson.getString("invoiceId");
            TInvoice tInvoice = new TInvoice();
            tInvoice.setId(invoiceId);
            tInvoice = tInvoiceDao.queryDetail(tInvoice);

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("invoiceId",invoiceId);
            List<TInvoiceTicket> tickets= tInvoiceTicketDao.queryList(reqMap);
            List<String> orderIds = new ArrayList<>();
            for(TInvoiceTicket tInvoiceTicket:tickets){
                orderIds.add(tInvoiceTicket.getMzOrderId());
            }

            String mzUserId = CommonUtil.getMzUserId(tInvoice.getUserid());
            JSONArray resArray =MZService.getorderList(mzUserId);
            List<JSONObject> dataArray= new ArrayList<>();
            for(int i=0;i<resArray.size();i++){
                JSONObject resObj =  resArray.getJSONObject(i);
                String mz_order_id = resObj.getString("mz_order_id");  //麦座订单id
                if(orderIds.contains(mz_order_id)){
                    dataArray.add(resObj);
                }
            }

            JSONArray objArray = new JSONArray();
            for(int i=0;i<dataArray.size();i++){
                try {
                    JSONObject dataObj = dataArray.get(i);
                    JSONObject obj = new JSONObject();
                    //订单基本信息
                    String mz_order_id = dataObj.getString("mz_order_id");
                    String mz_user_id = dataObj.getString("mz_user_id");
                    obj.put("id",dataObj.getString("mz_order_id"));   //订单id
                    obj.put("orderId",dataObj.getString("mz_order_id"));   //订单id
                    obj.put("count",dataObj.getInteger("order_goods_count"));  //商品数量
                    //场次信息
                    JSONArray eventList = dataObj.getJSONObject("order_event_list").getJSONArray("order_event_v_o");
                    obj.put("eventList",eventList);

                    //现金金额
                    JSONObject detailObj = MZService.getMzOrderDetail(mz_user_id,mz_order_id);
                    int online_payment_money_fen = detailObj.getInteger("online_payment_money_fen");  //在线支付的金额,单位：分
                    BigDecimal onlinePayMoney = new BigDecimal(online_payment_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                    obj.put("onlinePayMoney",onlinePayMoney);

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
     * 演出开票重发发票
     */
    @Override
    public JSONObject reSendTicketInvoice(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            String invoiceId = reqJson.getString("invoiceId");
            TInvoice tInvoice = new TInvoice();
            tInvoice.setId(invoiceId);
            tInvoice = tInvoiceDao.queryDetail(tInvoice);

            //重新发送发票
            JSONObject reqObj = new JSONObject();
            reqObj.put("taxnum",Const.INVOICE_TAXNUM);   //销方税号
            reqObj.put("invoiceCode",tInvoice.getInvoiceCode());   //发票代码
            reqObj.put("invoiceNum",tInvoice.getInvoiceNo());   //发票号码
            if(StringUtil.isNotNull(reqJson.get("pushEmail"))){
                reqObj.put("mail",reqJson.get("pushEmail"));
            }
            if(StringUtil.isNotNull(reqJson.get("pushPhone"))){
                reqObj.put("phone",reqJson.get("pushPhone"));
            }

            if(NNService.reSendInvoice(reqObj)){
                //保存本次开票推送记录
                //推送邮箱和手机号，二者不可全为空
                String pushEmail = reqJson.get("pushEmail")==null?"":reqJson.getString("pushEmail");
                String pushPhone = reqJson.get("pushPhone")==null?"":reqJson.getString("pushPhone");
                TInvoicePush tInvoicePush = new TInvoicePush();
                tInvoicePush.setInvoiceId(invoiceId);
                tInvoicePush.setPushEmail(pushEmail);
                tInvoicePush.setPushPhone(pushPhone);
                tInvoicePushDao.insert(tInvoicePush);

                retCode = "0";
                retMsg = "操作成功！";
            }else{
                retMsg = "重新发送发票失败，请稍后重试或联系客服人员！";
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
     * 判断参数时间间相差的毫秒

     * @return
     */
    public int differentMilesBetween2Times(String timeStr1,String timeStr2)
    {
        Date date1 = null;
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date2 = null;
        try {
            date1 = sf.parse(timeStr1);
            date2 = sf.parse(timeStr2);
            int miles = (int) ((date1.getTime() - date2.getTime()) );
            return miles;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int differentDays(String dateStr)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date1 = new Date();
        Date date2 = null;
        try {
            date2 = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = (int) ((date1.getTime() - date2.getTime()) / (1000*3600*24));
        return days;
    }
}
