package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.*;
import com.centit.shopping.webmgr.service.TicketInvoiceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>发票<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-24
 **/
@Transactional
@Service
public class TicketInvoiceServiceImpl implements TicketInvoiceService {
    public static final Log log = LogFactory.getLog(TicketInvoiceService.class);

    @Resource
    private TInvoiceTicketDao tInvoiceTicketDao;

    @Resource
    private TInvoiceDao tInvoiceDao;

    @Resource
    private TicketProjectDao ticketProjectDao;

    /**
     * 查询用户可开票的演出订单列表
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
                    if(StringUtil.isNotNull(reqJson.get("mzOrderId"))){
                        if(mz_order_id.contains(reqJson.getString("mzOrderId"))){
                            dataArray.add(resObj);
                        }
                    }else{
                        dataArray.add(resObj);
                    }

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
     * 手动添加线下开票记录
     */
    @Override
    public JSONObject addTicketInvoice(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            TInvoice tInvoice = JSON.parseObject(reqJson.toJSONString(), TInvoice.class);
            tInvoice.setInvoiceType("2");  //线下
            tInvoice.setOrderType("1");    //演出
            tInvoice.setInvoiceStatus("2");
            tInvoiceDao.insert(tInvoice);
            String invoiceId = tInvoice.getId();

            JSONArray orderArray = reqJson.getJSONArray("orders");
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


        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询开票记录列表
     */
    @Override
    public JSONObject queryInvoiceRecordList(JSONObject reqJson) {
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
            reqMap.put("orderType", "1");  //演出开票
            reqMap.put("invoiceStatus", "2");   //只查询开票完成的记录
            bizDataJson.put("total",tInvoiceDao.queryRecordListCount(reqMap));
            List<TInvoice> tInvoiceList= tInvoiceDao.queryRecordList(reqMap);
            for(TInvoice tInvoice:tInvoiceList){
                //查询开票相关订单和项目数据
                HashMap<String, Object> proMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                proMap.put("invoiceId",tInvoice.getId());
                List<TInvoiceTicket> tickets= tInvoiceTicketDao.queryList(proMap);
                String orderIds = "";
                for(TInvoiceTicket tInvoiceTicket:tickets){
                    orderIds+=tInvoiceTicket.getMzOrderId()+";";
                }
                orderIds=orderIds.substring(0,orderIds.length()-1);
                tInvoice.setMzOrderIds(orderIds);
                List<TicketProject> projects= ticketProjectDao.queryInvoiceProjectList(proMap);
                String prjectNames ="";
                for(TicketProject ticketProject:projects){
                    prjectNames+=ticketProject.getProjectName()+";";
                }
                prjectNames=prjectNames.substring(0,prjectNames.length()-1);
                tInvoice.setPrjectNames(prjectNames);
            }

            bizDataJson.put("objList",tInvoiceList);

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
     * 导出开票记录列表
     */
    @Override
    public void exportInvoiceRecordList(JSONObject reqJson, HttpServletResponse response) {
        try {

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("invoiceStatus", "2");   //只查询开票完成的记录

            List<TInvoice> tInvoiceList= tInvoiceDao.queryRecordList(reqMap);
            for(TInvoice tInvoice:tInvoiceList){
                //查询开票相关订单和项目数据
                HashMap<String, Object> proMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                proMap.put("invoiceId",tInvoice.getId());
                List<TInvoiceTicket> tickets= tInvoiceTicketDao.queryList(proMap);
                String orderIds = "";
                for(TInvoiceTicket tInvoiceTicket:tickets){
                    orderIds+=tInvoiceTicket.getMzOrderId()+";";
                }
                orderIds=orderIds.substring(0,orderIds.length()-1);
                tInvoice.setMzOrderIds(orderIds);
                List<TicketProject> projects= ticketProjectDao.queryInvoiceProjectList(proMap);
                String prjectNames ="";
                for(TicketProject ticketProject:projects){
                    prjectNames+=ticketProject.getProjectName()+";";
                }
                prjectNames=prjectNames.substring(0,prjectNames.length()-1);
                tInvoice.setPrjectNames(prjectNames);
            }

            String sumStr ="开票数据";
            // 导出表的标题
            String title =sumStr;
            // 导出表的列名
            String[] rowsName =new String[]{"交款单位","纳税人识别号","地址","开户行","项目名称","订单号","金额","专/普","开票时间","票号"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(TInvoice tInvoice:tInvoiceList){
                Object[] obj = new Object[10];
                obj[0] = tInvoice.getBuyerName();
                obj[1] = tInvoice.getBuyerTaxNum();
                obj[2] = tInvoice.getBuyerAddress();
                obj[3] = tInvoice.getBuyerBank();
                obj[4] = tInvoice.getPrjectNames();
                obj[5] = tInvoice.getMzOrderIds();
                obj[6] = tInvoice.getInvoiceAmount();
                if(tInvoice.getInvoiceLine().equals("s")){
                    obj[7] = "专";
                }else{
                    obj[7] = "普";
                }
                obj[8] = tInvoice.getInvoiceTime();
                obj[9] = tInvoice.getInvoiceNo();
                dataList.add(obj);
            }
            String fileName =String.valueOf(System.currentTimeMillis()) + ".xls";

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

    /**
     * 查询演出项目列表
     */
    @Override
    public JSONObject queryProjectList(JSONObject reqJson) {
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

            bizDataJson.put("total",ticketProjectDao.queryListCount(reqMap));
            List<TicketProject> projectList= ticketProjectDao.queryList(reqMap);

            bizDataJson.put("objList",projectList);

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
     * 查询指定项目的开票记录
     */
    @Override
    public JSONObject queryProjectInvoiceList(JSONObject reqJson) {
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
            reqMap.put("invoiceStatus", "2");   //只查询开票完成的记录
            bizDataJson.put("total",tInvoiceDao.queryProjectRecordListCount(reqMap));
            List<TInvoice> tInvoiceList= tInvoiceDao.queryProjectRecordList(reqMap);
            for(TInvoice tInvoice:tInvoiceList){
                //查询开票相关订单和项目数据
                HashMap<String, Object> proMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                proMap.put("invoiceId",tInvoice.getId());
                List<TInvoiceTicket> tickets= tInvoiceTicketDao.queryList(proMap);
                String orderIds = "";
                for(TInvoiceTicket tInvoiceTicket:tickets){
                    orderIds+=tInvoiceTicket.getMzOrderId()+";";
                }
                orderIds=orderIds.substring(0,orderIds.length()-1);
                tInvoice.setMzOrderIds(orderIds);
                List<TicketProject> projects= ticketProjectDao.queryInvoiceProjectList(proMap);
                String prjectNames ="";
                for(TicketProject ticketProject:projects){
                    prjectNames+=ticketProject.getProjectName()+";";
                }
                prjectNames=prjectNames.substring(0,prjectNames.length()-1);
                tInvoice.setPrjectNames(prjectNames);
            }

            bizDataJson.put("objList",tInvoiceList);

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
     * 导出指定项目开票记录列表
     */
    @Override
    public void exportProjectInvoiceList(JSONObject reqJson, HttpServletResponse response) {
        try {

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("invoiceStatus", "2");   //只查询开票完成的记录

            List<TInvoice> tInvoiceList= tInvoiceDao.queryProjectRecordList(reqMap);
            for(TInvoice tInvoice:tInvoiceList){
                //查询开票相关订单和项目数据
                HashMap<String, Object> proMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                proMap.put("invoiceId",tInvoice.getId());
                List<TInvoiceTicket> tickets= tInvoiceTicketDao.queryList(proMap);
                String orderIds = "";
                for(TInvoiceTicket tInvoiceTicket:tickets){
                    orderIds+=tInvoiceTicket.getMzOrderId()+";";
                }
                orderIds=orderIds.substring(0,orderIds.length()-1);
                tInvoice.setMzOrderIds(orderIds);
                List<TicketProject> projects= ticketProjectDao.queryInvoiceProjectList(proMap);
                String prjectNames ="";
                for(TicketProject ticketProject:projects){
                    prjectNames+=ticketProject.getProjectName()+";";
                }
                prjectNames=prjectNames.substring(0,prjectNames.length()-1);
                tInvoice.setPrjectNames(prjectNames);
            }

            String sumStr ="开票数据";
            // 导出表的标题
            String title =sumStr;
            // 导出表的列名
            String[] rowsName =new String[]{"交款单位","纳税人识别号","地址","开户行","项目名称","订单号","金额","专/普","开票时间","票号"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(TInvoice tInvoice:tInvoiceList){
                Object[] obj = new Object[10];
                obj[0] = tInvoice.getBuyerName();
                obj[1] = tInvoice.getBuyerTaxNum();
                obj[2] = tInvoice.getBuyerAddress();
                obj[3] = tInvoice.getBuyerBank();
                obj[4] = tInvoice.getPrjectNames();
                obj[5] = tInvoice.getMzOrderIds();
                obj[6] = tInvoice.getInvoiceAmount();
                if(tInvoice.getInvoiceLine().equals("s")){
                    obj[7] = "专";
                }else{
                    obj[7] = "普";
                }
                obj[8] = tInvoice.getInvoiceTime();
                obj[9] = tInvoice.getInvoiceNo();
                dataList.add(obj);
            }
            String fileName =String.valueOf(System.currentTimeMillis()) + ".xls";

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
