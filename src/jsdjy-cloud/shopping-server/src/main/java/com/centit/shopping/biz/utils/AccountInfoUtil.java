package com.centit.shopping.biz.utils;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.bo.UserAccountInfo;
import com.centit.shopping.utils.MZService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountInfoUtil {
    public UserAccountInfo getUserAccountInfo(String mzUserId){
        UserAccountInfo userAccountInfo = new UserAccountInfo(mzUserId);
        try{
            //从卖座实时查询账户积分和余额
            JSONObject accountObj = MZService.getAssetinfo(mzUserId);
            if (null != accountObj) {
                int account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                int account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                //账户余额；单位：分
                BigDecimal accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元

                userAccountInfo.setAccount_point(account_point);
                userAccountInfo.setAccount_money_fen(account_money_fen);
                userAccountInfo.setAccountMoney(accountMoney);
                userAccountInfo.setAccountEnable(true);
            }
        }catch (Exception e){
            e.printStackTrace();
            userAccountInfo.setAccountEnable(false);
        }
        return userAccountInfo;
    }
}
