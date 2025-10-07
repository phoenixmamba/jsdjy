package com.centit.ticket.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-16
 **/
@Data
public class ShoppingPayment implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String content;

    private Boolean install;

    private String storeId;

    private String type;

    private String name;

    private String mark;

    private String weixinAppid;

    private String weixinAppsecret;

    private String weixinPartnerid;

    private String weixinPartnerkey;

    private String weixinPaysignkey;

    private String closeContent;

    private String useing;

    private BigDecimal alipayDivideRate;

    private BigDecimal alipayRate;

    private BigDecimal balanceDivideRate;

    private String chinabankAccount;

    private String chinabankKey;

    private String currencyCode;

    private Integer interfaceType;

    private String merchantAcctId;

    private String partner;

    private String paypalUserid;

    private String pid;

    private BigDecimal poundage;

    private String rmbKey;

    private String safeKey;

    private String sellerEmail;

    private String spname;

    private String tenpayKey;

    private String tenpayPartner;

    private Integer tradeMode;


}
