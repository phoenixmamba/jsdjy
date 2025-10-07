package com.centit.mallserver.order.validate;

import com.centit.core.dto.OrderDto;
import com.centit.core.exp.BusinessException;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单校验接口
 * @Date : 2025/8/27 10:33
 **/
public interface OrderValidator {
    /**
     * 校验订单
     * @param orderDto 校验参数
     * @throws BusinessException 校验失败时抛出异常
     */
    void validate(OrderDto orderDto) throws BusinessException;
}
