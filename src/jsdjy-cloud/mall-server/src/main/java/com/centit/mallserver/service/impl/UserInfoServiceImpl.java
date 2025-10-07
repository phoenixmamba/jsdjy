package com.centit.mallserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.exp.ThirdApiException;
import com.centit.core.result.ResultCodeEnum;
import com.centit.core.service.third.MzService;
import com.centit.mallserver.dao.ShoppingAssetRuleDao;
import com.centit.mallserver.dao.ShoppingMembershipDao;
import com.centit.mallserver.dao.ShoppingPayLimitDao;
import com.centit.mallserver.dao.ShoppingUserInfoDao;
import com.centit.mallserver.model.UserAccountInfo;
import com.centit.mallserver.model.UserInfo;
import com.centit.mallserver.po.ShoppingAssetRulePo;
import com.centit.mallserver.po.ShoppingMembershipPo;
import com.centit.mallserver.po.ShoppingPayLimitPo;
import com.centit.mallserver.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 用户信息服务实现类
 * @Date : 2025/8/26 14:03
 **/
@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private MzService mzService;
    @Resource
    private ShoppingUserInfoDao shoppingUserInfoDao;
    @Resource
    private ShoppingPayLimitDao shoppingPayLimitDao;
    @Resource
    private ShoppingAssetRuleDao shoppingAssetRuleDao;
    @Resource
    private ShoppingMembershipDao membershipDao;
    @Override
    public UserInfo getUserInfo(String userId) {
        return shoppingUserInfoDao.selectUserDetail(userId);
    }

    @Override
    public UserAccountInfo getUserAccountInfo(String mzUserId) {
        JSONObject accountObj = mzService.getUserAccountInfo(mzUserId);
        int accountPoint = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");
        int accountMoneyFen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
        //分转换为元
        BigDecimal accountMoney = new BigDecimal(accountMoneyFen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        UserAccountInfo userAccountInfo = new UserAccountInfo(mzUserId);
        userAccountInfo.setAccountPoint(accountPoint);
        userAccountInfo.setAccountMoneyFen(accountMoneyFen);
        userAccountInfo.setAccountMoney(accountMoney);
        userAccountInfo.setAccountEnable(true);
        return null;
    }

    @Override
    public ShoppingPayLimitPo getPayLimit() {
        return shoppingPayLimitDao.selectDetail();
    }

    @Override
    public ShoppingAssetRulePo getAssetRule() {
        return shoppingAssetRuleDao.selectDetail();
    }

    @Override
    public JSONObject getUserAddress(String addressId, String mzUserId) {
        JSONObject addObj = null;
        if (StringUtils.isNotBlank(addressId)) {
            addObj = mzService.getUserAddressDetail(mzUserId,addressId);
        } else {
            try{
                //没有选择地址时，取用户默认收货地址
                JSONObject addressList = mzService.getUserAddressList(mzUserId, 100, 1);
                JSONArray addressArray = addressList.getJSONObject("data_list").getJSONArray("user_address_detail_v_o");
                for (int i = 0; i < addressArray.size(); i++) {
                    JSONObject addressObj = addressArray.getJSONObject(i);
                    addObj = addressObj;
                    if (addressObj.getBoolean("default_address_boolean")) {
                        break;
                    }
                }
            }catch (Exception e){
                log.error("获取会员收货地址异常：",e);
                throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR,"获取用户收货地址信息异常");
            }
        }

        return addObj;
    }

    @Override
    public BigDecimal getUserMemberShip(UserInfo userInfo) {
        String levelName = userInfo.getLevelName();
        ShoppingMembershipPo membership = membershipDao.selectDetail();
        if(StringUtils.isBlank(levelName)){
            return membership.getNormal();
        }
        switch (levelName) {
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
