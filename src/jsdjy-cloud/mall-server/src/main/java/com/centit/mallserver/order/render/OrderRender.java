package com.centit.mallserver.order.render;

import com.centit.mallserver.dto.ProductDto;
import com.centit.mallserver.order.render.vo.GoodsOrderRenderVo;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单渲染器接口
 * @Date : 2025/8/26 16:28
 **/
public interface OrderRender {
    /**
     * 渲染订单信息
     */
    GoodsOrderRenderVo renderOrder(ProductDto productDto);
}
