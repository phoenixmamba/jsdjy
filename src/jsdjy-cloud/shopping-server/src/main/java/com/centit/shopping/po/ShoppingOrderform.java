package com.centit.shopping.po;

import java.util.*;
import java.io.Serializable;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;
import java.math.BigDecimal;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-25
 **/
@Data
public class ShoppingOrderform implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String finishTime;

    private BigDecimal goodsAmount;

    private String invoice;

    private Integer invoiceType;

    private String msg;

    private String orderId;

    private Integer orderStatus;

    private String payTime;

    private String payMsg;

    private BigDecimal refund;

    private String refundType;

    private String shipCode;

    private String shipTime;

    private BigDecimal shipPrice;

    private BigDecimal totalPrice;

    private String addrId;

//    private ShoppingAddress addrInfo;
//
//    public ShoppingAddress getAddrInfo(){
//        if(null !=getAddrId()&&!"".equals(getAddrId())){
//            return CommonUtil.getShoppingAddress(getAddrId());
//        }
//        return null;
//    }

    private String paymentId;

    private String storeId;

    private String userId;

    private ShoppingUser userInfo;

    public Map<String,Object> getUserInfo(){
        ShoppingUser user =CommonUtil.getShoppingUserByUserId(getUserId());
        Map<String,Object> userInfoMap = new HashMap<>();
        if(null !=user){
            userInfoMap.put("userId",user.getId());
            userInfoMap.put("userName",user.getNickName());
            userInfoMap.put("mobile",user.getMobile());
        }else{
            userInfoMap.put("userId","");
            userInfoMap.put("userName","");
            userInfoMap.put("mobile","");
        }

        return userInfoMap;
    }

    private Boolean autoConfirmEmail;

    private Boolean autoConfirmApp;

    private Boolean autoConfirmSms;

    private String transport;

    private String outOrderId;

    private String ecId;

    public String getecvalue(){
        if(null !=getEcId()&&!"".equals(getEcId())){
            String[] ecIds = getEcId().split(";");
            String res="";
            for(int i=0;i<ecIds.length;i++){
                String name = CommonUtil.getExpressCompany(ecIds[i]).getCompanyName();
                res=res+name+";";
            }
            return res;
        }
        return "";
    }

    private String ciId;

    private String orderSellerIntro;

    private String returnShipcode;

    private String returnEcId;

    private String returnContent;

    private String returnShiptime;

    private Integer orderType;

    private String weixinpayOrderId;

    /**
     * 商家赠送D币
     */
    private BigDecimal goodsDgold;

    /**
     * 订单总积分
     */
    private BigDecimal totalIntegral;

    private String wxPrepayId;

    private BigDecimal payPrice;

    /**
     * 签到截止时间
     */
    private String signEndtime;

    /**
     * 微信支付吊起时间
     */
    private String weixinpayTime;

    private String invoice1;

    private Integer deductionIntegral;

    /**
     * 拼团分组id
     */
    private String groupId;

    private String maintainCode;

    private Boolean homeMaintain;

    private BigDecimal maintainMoney;

    private BigDecimal homeMaintainMoney;

    private BigDecimal bargainAlreadyPrice;

    private BigDecimal bargainCanPrice;

    private String bargainEndTime;

    private Integer bargainNumber;

    private BigDecimal orderTolPrice;

    private String applyforBargainId;

    /**
     * 红包状态，10可分享，20已拆完，30已过期
     */
    private Integer redStatus;

    private String confirmTime;

    private Integer shareCount;

    private String shareEndTime;

    private BigDecimal shareTotalPrice;

    private String ordershareredgoodsId;

    private Integer shareDiedTime;

    private String couponId;

    private BigDecimal deductionIntegralPrice;

    private BigDecimal deductionMemberPrice;

    private BigDecimal deductionBalancePrice;

    private BigDecimal deductionCouponPrice;

    private BigDecimal deductionGiftcardPrice;

    private String userName;

    public String getUserName(){
        return getUserInfo().get("userName").toString();
    }

    private String mobile;

    public String getMobile(){
        return getUserInfo().get("mobile").toString();
    }


    private List<HashMap<String,Object>> orderGoods = new ArrayList<>();
//    public List<HashMap<String,Object>> getOrderGoods(){
//        return CommonUtil.getOrderGoods(getId());
//    }
}
