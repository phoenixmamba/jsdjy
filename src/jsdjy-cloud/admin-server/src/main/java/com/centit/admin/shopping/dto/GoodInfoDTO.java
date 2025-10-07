package com.centit.admin.shopping.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @String : 2021-02-21
 **/
@Data
public class GoodInfoDTO {
    /**
     * 商品id
     */
    private String id;
    /**
     * 商品详情
     */
    @NotNull(message = "商品详情不能为空")
    private String goodsDetails;
    /**
     * 商品名称
     */
    @NotNull(message = "商品详情不能为空")
    @Size(min = 0,max = 50,message = "商品名称应该在0到50个字符之间")
    private String goodsName;
    /**
     * 商品标识
     */
    private String goodsCheckbox;
    /**
     * 商品原价
     */
    @NotNull(message = "商品原价不能为空")
    @DecimalMin(value = "0",message = "商品原价不能小于0")
    private BigDecimal goodsPrice;
    /**
     * 店铺价格
     */
    @NotNull(message = "店铺价格不能为空")
    @DecimalMin(value = "0",message = "店铺价格不能小于0")
    private BigDecimal storePrice;
    /**
     * 出厂价
     */
    @DecimalMin(value = "0",message = "出厂价不能小于0")
    private BigDecimal exFactoryPrice;
    /**
     * 限购数量
     */
    @Min(value = 0,message = "限购数量不能小于0")
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
    @NotNull(message = "商品重量不能为空")
    @DecimalMin(value = "0",message = "商品重量不能小于0")
    private BigDecimal goodsWeight;
    /**
     * 商品体积
     */
    private BigDecimal goodsVolume;
    /**
     * 库存配置
     */
    @NotNull(message = "库存配置不能为空")
    private String inventoryType;
    /**
     * 库存值
     */
    @Min(value = 0,message = "商品库存不能小于0")
    @NotNull(message = "库存值不能为空")
    private Integer goodsInventory;
    /**
     * 运费模板id
     */
    private String transportId;
    /**
     * 运费金额
     */
    @DecimalMin(value = "0",message = "运费金额不能小于0")
    private BigDecimal expressTransFee;
    /**
     * 积分抵扣设置,0:不允许使用积分抵扣;1:固定积分额度;2:积分不限额抵扣
     */
    private Integer useIntegralSet;
    /**
     * 定额积分抵扣值
     */
    @Min(value =0,message = "积分抵扣值不能小于0")
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
    @NotNull(message = "商品主图不能为空")
    private String goodsMainPhotoId;
    /**
     * 商户手机号
     */
    private String phone;
    /**
     * 商品创建人
     */
    private String addUser;

    @Valid
    private List<GoodsSpecInventoryDTO> inventoryDetails;
    @Valid
    private List<GoodsSpecDTO> specs;
    @Valid
    private List<GoodsPhotoDTO> photos;

}
