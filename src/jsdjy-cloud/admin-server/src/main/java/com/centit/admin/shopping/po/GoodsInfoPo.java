package com.centit.admin.shopping.po;

import com.centit.admin.shopping.dto.GoodInfoDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date  : 2021-02-21
 **/
@Data
public class GoodsInfoPo implements Serializable {

    private String id;
    /**
     * 创建时间
     */
    private String addTime;
    /**
     * 删除状态
     */
    private String deleteStatus;
    /**
     * 销售数量
     */
    private Integer soldCount;
    /**
     * 上下架状态
     */
    private String goodsStatus;
    /**
     * 所属商户id
     */
    private String goodsStoreId;
    /**
     * 商品详情
     */
    private String goodsDetails;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品标识
     */
    private String goodsCheckbox;
    /**
     * 商品原价
     */
    private BigDecimal goodsPrice;
    /**
     * 店铺价格
     */
    private BigDecimal storePrice;
    /**
     * 出厂价
     */
    private BigDecimal exFactoryPrice;
    /**
     * 限购数量
     */
    private Integer limitBuy;
    /**
     * 商品排序
     */
    private Integer appSort;
    /**
     * 商品货号
     */
    private String goodsSerial;
    /**
     * 商品重量
     */
    private BigDecimal goodsWeight;
    /**
     * 商品体积
     */
    private BigDecimal goodsVolume;
    /**
     * 库存配置
     */
    private String inventoryType;
    /**
     * 库存值
     */
    private Integer goodsInventory;
    /**
     * 运费模板id
     */
    private String transportId;
    /**
     * 运费金额
     */
    private BigDecimal expressTransFee;
    /**
     * 积分抵扣设置,0:不允许使用积分抵扣;1:固定积分额度;2:积分不限额抵扣
     */
    private Integer useIntegralSet;
    /**
     * 定额积分抵扣值
     */
    private Integer useIntegralValue;
    /**
     * 使用余额支付设置 0:不允许使用;1:可使用
     */
    private Integer useBalanceSet;
    /**
     * 是否支持会员等级这块 0：不支持；1：支持
     */
    private Integer useMembershipSet;
    /**
     * 核销账号
     */
    private String writeOffCount;
    /**
     * 是否支持自提0:不支持;1:支持
     */
    private String selfextractionSet;
    /**
     * 自提地址
     */
    private String selfextractionAddress;
    /**
     * 所属分类id
     */
    private String gcId;
    /**
     * 主图id
     */
    private String goodsMainPhotoId;
    /**
     * 商户手机号
     */
    private String phone;
    /**
     * 商品创建人
     */
    private String addUser;

    public GoodsInfoPo(){

    }

    public GoodsInfoPo(GoodInfoDTO goodInfoDto){
        this.id= goodInfoDto.getId();
        this.goodsDetails= goodInfoDto.getGoodsDetails();
        this.goodsName= goodInfoDto.getGoodsName();
        this.goodsCheckbox= goodInfoDto.getGoodsCheckbox();
        this.goodsPrice= goodInfoDto.getGoodsPrice();
        this.storePrice= goodInfoDto.getStorePrice();
        this.exFactoryPrice= goodInfoDto.getExFactoryPrice();
        this.limitBuy= goodInfoDto.getLimitBuy();
        this.appSort= goodInfoDto.getAppSort();
        this.goodsSerial= goodInfoDto.getGoodsSerial();
        this.goodsWeight= goodInfoDto.getGoodsWeight();
        this.goodsVolume= goodInfoDto.getGoodsVolume();
        this.inventoryType= goodInfoDto.getInventoryType();
        this.goodsInventory= goodInfoDto.getGoodsInventory();
        this.transportId= goodInfoDto.getTransportId();
        this.expressTransFee= goodInfoDto.getExpressTransFee();
        this.useIntegralSet= goodInfoDto.getUseIntegralSet();
        this.useIntegralValue= goodInfoDto.getUseIntegralValue();
        this.useBalanceSet= goodInfoDto.getUseBalanceSet();
        this.useMembershipSet= goodInfoDto.getUseMembershipSet();
        this.writeOffCount= goodInfoDto.getWriteOffCount();
        this.selfextractionSet= goodInfoDto.getSelfextractionSet();
        this.selfextractionAddress= goodInfoDto.getSelfextractionAddress();
        this.gcId= goodInfoDto.getGcId();
        this.goodsMainPhotoId= goodInfoDto.getGoodsMainPhotoId();
        this.phone= goodInfoDto.getPhone();
        this.addUser= goodInfoDto.getAddUser();
    }
}
