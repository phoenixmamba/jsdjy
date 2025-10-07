package com.centit.shopping.webmgr.service.impl;


import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.TicketCouponExchangeRecordDao;
import com.centit.shopping.po.*;
import com.centit.shopping.dao.TicketCouponExchangeDao;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.ExportExcel;
import com.centit.shopping.utils.MZService;
import com.centit.shopping.utils.StringUtil;
import com.centit.shopping.webmgr.service.TicketCouponExchangeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2022-07-04
 **/
@Transactional
@Service
public class TicketCouponExchangeServiceImpl implements TicketCouponExchangeService {
    public static final Log log = LogFactory.getLog(TicketCouponExchangeService.class);

    @Resource
    private TicketCouponExchangeDao ticketCouponExchangeDao;

    @Resource
    private TicketCouponExchangeRecordDao ticketCouponExchangeRecordDao;


    /**
     * 查询列表
     */
    @Override
    public JSONObject queryCouponActivityList(JSONObject reqJson) {
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

            bizDataJson.put("total",ticketCouponExchangeDao.queryTotalCount(reqMap));
            List<TicketCouponExchange> objList=ticketCouponExchangeDao.queryList(reqMap);

            //查询每个活动已使用的兑换码数量
            for(TicketCouponExchange ticketCouponExchange:objList){
                reqMap.clear();
                reqMap.put("used",1);
                reqMap.put("actId",ticketCouponExchange.getId());
                ticketCouponExchange.setUsedAmount(ticketCouponExchangeRecordDao.queryList(reqMap).size());
            }
            bizDataJson.put("objList",objList);

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
     * 查询已创建的兑换码列表
     */
    @Override
    public JSONObject queryCouponCodeList(JSONObject reqJson) {
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

            bizDataJson.put("total",ticketCouponExchangeRecordDao.queryTotalCount(reqMap));
            List<TicketCouponExchangeRecord> objList=ticketCouponExchangeRecordDao.queryList(reqMap);
            bizDataJson.put("objList",objList);

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
     * 导出兑换码文件
     */
    @Override
    public void exportCouponCodeList(JSONObject reqJson, HttpServletResponse response) {
        JSONObject retJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            List<TicketCouponExchangeRecord> objList=ticketCouponExchangeRecordDao.queryList(reqMap);
            String actId = reqJson.getString("actId");
            TicketCouponExchange ticketCouponExchange = new TicketCouponExchange();
            ticketCouponExchange.setId(actId);
            ticketCouponExchange = ticketCouponExchangeDao.queryDetail(ticketCouponExchange);

            String sumStr =ticketCouponExchange.getTitle();
            // 导出表的标题
            String title =sumStr;
            // 导出表的列名
            String[] rowsName =new String[]{"兑换码","密码","创建时间","兑换手机号","兑换时间"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            for(TicketCouponExchangeRecord ticketCouponExchangeRecord:objList){
                Object[] obj = new Object[5];
                obj[0] = ticketCouponExchangeRecord.getCouponCode();
                obj[1] = ticketCouponExchangeRecord.getCouponPwd();
                obj[2] = ticketCouponExchangeRecord.getCreateTime();
                obj[3] = ticketCouponExchangeRecord.getExchangeMobile();
                obj[4] = ticketCouponExchangeRecord.getExchangeTime();
                dataList.add(obj);
            }
//            String fileName =new String(("订单明细_"+String.valueOf(System.currentTimeMillis()) + ".xls").getBytes(),"ISO-8859-1");
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
     * 新增兑换码活动
     */
    @Override
    public JSONObject addTicketCouponActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketCouponExchange ticketCouponExchange = JSONObject.parseObject(reqJson.toJSONString(), TicketCouponExchange.class);
            ticketCouponExchangeDao.insert(ticketCouponExchange);

            int amount = ticketCouponExchange.getAmount();
            //创建指定数量的兑换码
            for(int i=0;i<amount;i++){
                TicketCouponExchangeRecord ticketCouponExchangeRecord = new TicketCouponExchangeRecord();
                ticketCouponExchangeRecord.setActId(ticketCouponExchange.getId());
                ticketCouponExchangeRecord.setPromotionId(ticketCouponExchange.getPromotionId());
                SimpleDateFormat sf=new SimpleDateFormat("MMdd");
                String code = sf.format(new Date())+StringUtil.randomNum(8);
                ticketCouponExchangeRecord.setCouponCode(code);
                if(ticketCouponExchange.getNeedPwd().equals("1")){
                    String pwd = StringUtil.randomCode(8);
                    ticketCouponExchangeRecord.setCouponPwd(pwd);
                }
                ticketCouponExchangeRecordDao.insert(ticketCouponExchangeRecord);
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
     * 编辑兑换码活动
     */
    @Override
    public JSONObject editTicketCouponActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketCouponExchange ticketCouponExchange = JSONObject.parseObject(reqJson.toJSONString(), TicketCouponExchange.class);
            ticketCouponExchangeDao.update(ticketCouponExchange);
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
     * 查询麦座优惠码的优惠详情
     */
    @Override
    public JSONObject queryPromotionDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String promotionId = reqJson.getString("promotionId");
            JSONObject dataObj=MZService.getPromotionDetail(promotionId);
            if(null ==dataObj){
                retMsg = "未查询对应的票品优惠规则，请确认输入的优惠码是否正确！";
            }else{
                bizDataJson.put("data", dataObj);
                retCode = "0";
                retMsg = "操作成功！";
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
     * 追加指定数量的兑换码
     */
    @Override
    public JSONObject addCouponCode(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String actId = reqJson.getString("actId");
            int amount = reqJson.getInteger("addAmount");   //追加的数量
            TicketCouponExchange ticketCouponExchange = new TicketCouponExchange();
            ticketCouponExchange.setId(actId);
            ticketCouponExchange = ticketCouponExchangeDao.queryDetail(ticketCouponExchange);

            //创建指定数量的兑换码
            for(int i=0;i<amount;i++){
                TicketCouponExchangeRecord ticketCouponExchangeRecord = new TicketCouponExchangeRecord();
                ticketCouponExchangeRecord.setActId(actId);
                ticketCouponExchangeRecord.setPromotionId(ticketCouponExchange.getPromotionId());
                SimpleDateFormat sf=new SimpleDateFormat("MMdd");
                String code = sf.format(new Date())+StringUtil.randomNum(8);
                ticketCouponExchangeRecord.setCouponCode(code);
                if(ticketCouponExchange.getNeedPwd().equals("1")){
                    String pwd = StringUtil.randomCode(8);
                    ticketCouponExchangeRecord.setCouponPwd(pwd);
                }
                ticketCouponExchangeRecordDao.insert(ticketCouponExchangeRecord);
            }

            ticketCouponExchange.setAmount(ticketCouponExchange.getAmount()+amount);
            ticketCouponExchangeDao.update(ticketCouponExchange);

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
     * 上/下架兑换码活动
     */
    @Override
    public JSONObject pubTicketCouponActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketCouponExchange ticketCouponExchange = JSONObject.parseObject(reqJson.toJSONString(), TicketCouponExchange.class);
            ticketCouponExchangeDao.update(ticketCouponExchange);

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
     * 删除兑换码活动
     */
    @Override
    public JSONObject delTicketCouponActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TicketCouponExchange ticketCouponExchange = new TicketCouponExchange();
            ticketCouponExchange.setId(reqJson.getString("id"));
            ticketCouponExchange.setIsDelete("1");
            ticketCouponExchangeDao.update(ticketCouponExchange);

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
