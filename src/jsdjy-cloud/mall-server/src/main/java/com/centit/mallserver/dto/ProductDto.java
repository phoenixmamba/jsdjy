package com.centit.mallserver.dto;

import com.centit.core.dto.OrderDto;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单商品信息
 * @Date : 2024/12/19 14:04
 **/
@Data
public class ProductDto {
    /**
     * 用户ID
     */
    @NotNull
    private String userId;

    /**
     * 商品ID
     */
    @NotNull
    private String goodsId;

    /**
     * 商品数量
     */
    @NotNull
    private Integer goodsCount;

    /**
     * 商品属性
     */
    private String propertys;

    /**
     * 优惠券ID
     */
    private String couponId;

    /**
     * 配送方式
     */
    private String transport;

    /**
     * 收货地址ID
     */
    private String addressId;

    /**
     * 使用积分
     */
    private Integer useIntegral;

    /**
     * 使用余额
     */
    private Integer useBalance;

    public ProductDto(OrderDto orderDto){
        this.userId=orderDto.getUserId();
        this.goodsId=orderDto.getGoodsId();
        this.goodsCount=orderDto.getGoodsCount();
        this.propertys=orderDto.getPropertys();
        this.couponId=orderDto.getCouponId();
        this.transport=orderDto.getTransport();
        this.addressId=orderDto.getAddressId();
        this.useIntegral=orderDto.getUseIntegral();
        this.useBalance=orderDto.getUseBalance();
    }

}
