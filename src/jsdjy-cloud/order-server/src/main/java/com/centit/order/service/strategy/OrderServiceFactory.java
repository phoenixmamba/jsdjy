package com.centit.order.service.strategy;

import com.centit.core.dto.OrderDto;
import com.centit.core.enums.SellTypeEnum;
import com.centit.core.exp.BusinessException;
import com.centit.core.result.ResultCodeEnum;
import com.centit.order.service.BaseOrderAddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/27 17:12
 **/
@Component
public class OrderServiceFactory {
    @Autowired
    private Map<String, BaseOrderAddService> entStrategyMap;

    public BaseOrderAddService getOrderAddService(OrderDto dto) {
        if (dto.getOrderType() == SellTypeEnum.CULTURAL.getOrderType()) {
            return entStrategyMap.get("culturalOrderAddService");
        }
        throw new BusinessException(ResultCodeEnum.ORDER_ADD_FAIL, "订单类型异常");
    }
}
