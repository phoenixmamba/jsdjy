package com.centit.order.service;

import com.centit.core.dto.OrderDto;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/2 21:25
 **/
public interface StockRedisService {
    /**
     * 更新redis中的商品库存
     * @param orderDto
     */
    void updateRedisGoodsStock(OrderDto orderDto);
}
