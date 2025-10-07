package com.centit.mallserver.po;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/12 15:54
 **/
@Data
public class ShoppingGoodsPo {
    private String id;

    private String goodsName;

    private BigDecimal storePrice;

    private String goodsMainPhotoId;

    private int orderCount;

    private Integer useIntegralSet;

    private Integer useIntegralValue;

    private Integer useBalanceSet;

    private Integer useMembershipSet;

    private BigDecimal goodsPrice;

    private Integer goodsInventory;

    private String goodsNotice;

    private String returnExplain;

    private Integer limitBuy;

    private String inventoryType;

    private String goodsCheckbox;

    private String goodsDetails;

    private String goodsStatus;

    private String deleteStatus;

    private Integer goodsTransfee;

    private String selfextractionSet;

    private String selfextractionAddress;

    private BigDecimal expressTransFee;

    private String transportId;

    private String gcId;

}
