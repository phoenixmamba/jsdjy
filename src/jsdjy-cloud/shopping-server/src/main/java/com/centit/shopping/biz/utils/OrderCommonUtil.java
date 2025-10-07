package com.centit.shopping.biz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.po.ShoppingPayment;
import com.centit.shopping.utils.CommonInit;

import java.util.HashMap;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2023/8/31 15:55
 **/
public class OrderCommonUtil {

    /**
     * 获取当前系统可用支付方式
     */
    public static JSONArray getPayments(){
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("deleteStatus", 0);
        List<ShoppingPayment> payments = CommonInit.staticShoppingPaymentDao.queryList(reqMap);
        JSONArray payArray = new JSONArray();
        for (ShoppingPayment shoppingPayment : payments) {
            JSONObject obj = new JSONObject();
            obj.put("id", shoppingPayment.getId());
            obj.put("name", shoppingPayment.getName());
            payArray.add(obj);
        }
        return payArray;
    }
}
