package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.ExportExcel;
import com.centit.shopping.utils.StringUtil;
import com.centit.shopping.webmgr.service.WriteOffService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class WriteOffServiceImpl implements WriteOffService {
    public static final Log log = LogFactory.getLog(WriteOffService.class);

    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    @Resource
    private ShoppingWriteoffRecordDao shoppingWriteoffRecordDao;

    @Resource
    private TExportFileDao tExportFileDao;



    @Value("${order.orderState.hasDone}")
    private int orderStateDone;

    @Value("${order.orderState.anomalous}")
    private int orderStateAnomalous;

    @Value("${order.orderState.handAnomalous}")
    private int orderStateHandAnomalous;


    /**
     * 查询核销记录
     */
    @Override
    public JSONObject queryWriteOffRecordList(JSONObject reqJson) {
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

            List<HashMap<String, Object>> recordList = shoppingWriteoffRecordDao.queryWriteRecordList(reqMap);
            for(HashMap<String, Object> rMap:recordList){
                if(StringUtil.isNotNull(rMap.get("cartType"))&&StringUtil.isNotNull(rMap.get("goodsId"))){
                    //查询商品信息
                    int cartType = Integer.valueOf(rMap.get("cartType").toString());
                    if(cartType==1||cartType==2){   //文创或积分商品
                        ShoppingGoods shoppingGoods = new ShoppingGoods();
                        shoppingGoods.setId(rMap.get("goodsId").toString());//查询商品主体信息
                        shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                        rMap.put("goodsName", shoppingGoods.getGoodsName());
                        rMap.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                    }else if(cartType==3){   //艺术活动
                        ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                        shoppingArtactivity.setId(rMap.get("goodsId").toString());
                        //活动主体信息
                        shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                        rMap.put("goodsName", shoppingArtactivity.getActivityName());
                        rMap.put("photoId", shoppingArtactivity.getMainPhotoId());  //主图id
                    }else if(cartType==9){   //爱艺计划
                        ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                        shoppingArtplan.setId(rMap.get("goodsId").toString());
                        //爱艺计划主体信息
                        shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                        rMap.put("goodsName", shoppingArtplan.getActivityName());
                        rMap.put("photoId", shoppingArtplan.getMainPhotoId());  //主图id
                    }
                }

            }


            bizDataJson.put("total",shoppingWriteoffRecordDao.queryWriteRecordCount(reqMap));
            bizDataJson.put("objList",recordList);
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
    public JSONObject exportWriteOffRecordList(JSONObject reqJson, HttpServletResponse response) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TExportFile tExportFile = new TExportFile();
            String fileId = String.valueOf(System.currentTimeMillis());
            tExportFile.setId(fileId);
            tExportFile.setDataType("核销记录");
            tExportFileDao.insert(tExportFile);

            ExecutorService fixPool = Executors.newFixedThreadPool(1);
            fixPool.execute(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);

                    List<HashMap<String, Object>> recordList = shoppingWriteoffRecordDao.queryWriteRecordList(reqMap);
                    for(HashMap<String, Object> rMap:recordList){
                        if(StringUtil.isNotNull(rMap.get("cartType"))&&StringUtil.isNotNull(rMap.get("goodsId"))){
                            //查询商品信息
                            int cartType = Integer.valueOf(rMap.get("cartType").toString());
                            if(cartType==1||cartType==2){   //文创或积分商品
                                ShoppingGoods shoppingGoods = new ShoppingGoods();
                                shoppingGoods.setId(rMap.get("goodsId").toString());//查询商品主体信息
                                shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                                rMap.put("goodsName", shoppingGoods.getGoodsName());
                                rMap.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                            }else if(cartType==3){   //艺术活动
                                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                                shoppingArtactivity.setId(rMap.get("goodsId").toString());
                                //活动主体信息
                                shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                                rMap.put("goodsName", shoppingArtactivity.getActivityName());
                                rMap.put("photoId", shoppingArtactivity.getMainPhotoId());  //主图id
                            }else if(cartType==9){   //爱艺计划
                                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                                shoppingArtplan.setId(rMap.get("goodsId").toString());
                                //爱艺计划主体信息
                                shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                                rMap.put("goodsName", shoppingArtplan.getActivityName());
                                rMap.put("photoId", shoppingArtplan.getMainPhotoId());  //主图id
                            }
                        }

                    }
//            //统计总数
//            HashMap<String, Object> sum= shoppingOrderformDao.querySumList(reqMap);
//
//            String sumStr = "订单总金额："+sum.get("totalPrice")
//                    +"  优惠券应扣："+sum.get("deduction_coupon_price")+"  会员权益抵扣："+sum.get("deduction_member_price")+"  积分支付抵扣："
//                    +sum.get("deduction_integral_price")+"  余额支付抵扣："+sum.get("deduction_balance_price")+"  现金支付金额："+sum.get("pay_price");
                    String sumStr ="核销记录";
                    // 导出表的标题
                    String title =sumStr;
                    // 导出表的列名
                    String[] rowsName =new String[]{"核销时间","核销账户","商品类型","商品数量","商品名称",
                            "商品属性","用户昵称","用户手机","订单ID","下单时间"};
                    List<Object[]> dataList = new ArrayList<Object[]>();
                    for(HashMap<String, Object> rMap:recordList){
                        Object[] obj = new Object[10];
                        obj[0] = rMap.get("writeoffTime")==null?"":rMap.get("writeoffTime").toString();
                        obj[1] = rMap.get("accountName")==null?"":rMap.get("accountName").toString();
                        String cartTypeStr="";
                        if(StringUtil.isNotNull(rMap.get("cartType"))){
                            int cartType = Integer.valueOf(rMap.get("cartType").toString());

                            switch(cartType){
                                case 1:
                                    cartTypeStr= "文创商品";
                                    break;
                                case 2:
                                    cartTypeStr ="积分商品";
                                    break;
                                case 3:
                                    cartTypeStr= "会员活动";
                                    break;
                                case 9:
                                    cartTypeStr= "爱艺计划";
                                    break;
                                default:
                                    cartTypeStr= "";
                                    break;
                            }

                        }
                        obj[2] = cartTypeStr;
                        obj[3] = rMap.get("count")==null?"":rMap.get("count").toString();
                        obj[4] = rMap.get("goodsName")==null?"":rMap.get("goodsName").toString();
                        obj[5] = rMap.get("specInfo")==null?"":rMap.get("specInfo").toString();
                        obj[6] = rMap.get("nickName")==null?"":rMap.get("nickName").toString();
                        obj[7] = rMap.get("mobile")==null?"":rMap.get("mobile").toString();
                        obj[8] = rMap.get("ordId")==null?"":rMap.get("ordId").toString();
                        obj[9] = rMap.get("orderTime")==null?"":rMap.get("orderTime").toString();
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

}
