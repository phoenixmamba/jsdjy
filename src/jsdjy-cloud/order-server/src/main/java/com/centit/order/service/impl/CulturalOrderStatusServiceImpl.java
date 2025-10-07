package com.centit.order.service.impl;

import com.centit.order.dao.*;
import com.centit.order.enums.OrderAction;
import com.centit.order.enums.OrderStatus;
import com.centit.order.po.OrderformPo;
import com.centit.order.service.OrderStatusService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/2 14:28
 **/
@Service("CulturalOrderStatusService")
public class CulturalOrderStatusServiceImpl implements OrderStatusService {
    @Resource
    private OrderformDao orderformDao;

    @Override
    public void setNextStatus(String id, OrderAction orderAction) {
        OrderformPo orderformPo= new OrderformPo();
        orderformPo.setId(id);
        switch (orderAction){
            case CREATE:
                orderformPo.setOrderStatus(OrderStatus.TO_PAY.getStatus());
                break;
            case PAY:
                orderformPo.setOrderStatus(OrderStatus.HAS_PAY.getStatus());
                break;
            default:
                break;
        }
        orderformDao.updateStatus(orderformPo);
    }
}
