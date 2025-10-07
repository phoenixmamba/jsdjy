package com.centit.mallserver.order.validate;

import com.centit.core.enums.SellTypeEnum;
import com.centit.core.exp.OrderValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单校验工厂类
 * @Date : 2025/8/27 10:48
 **/
@Component
public class OrderValidatorFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public OrderValidator getValidator(SellTypeEnum sellType) {
        switch (sellType) {
            case CULTURAL:
                return applicationContext.getBean(GoodsOrderValidator.class);
            // 其他类型...
            default:
                throw new OrderValidationException("不支持的订单类型: " + sellType);
        }
    }
}
