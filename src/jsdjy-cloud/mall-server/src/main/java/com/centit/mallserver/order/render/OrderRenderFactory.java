package com.centit.mallserver.order.render;

import com.centit.core.enums.SellTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单渲染器工厂
 * @Date : 2025/8/26 16:22
 **/
@Component
public class OrderRenderFactory {
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 根据商品类型创建对应的订单渲染器
     */
    public OrderRender createOrderRender(SellTypeEnum sellType) {
        switch (sellType) {
            case CULTURAL:
                return applicationContext.getBean(CulturalGoodsOrderRender.class);
            // 其他类型...
            default:
                throw new IllegalArgumentException("Unsupported sell type: " + sellType);
        }
    }
}
