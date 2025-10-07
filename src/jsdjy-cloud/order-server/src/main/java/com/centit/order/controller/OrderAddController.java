package com.centit.order.controller;

import com.centit.core.dto.OrderDto;
import com.centit.core.result.Result;
import com.centit.core.result.ResultCodeEnum;
import com.centit.order.mq.productor.MqProducerService;
import com.centit.order.service.BaseOrderAddService;
import com.centit.order.service.strategy.OrderServiceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单接收控制器
 * @Date : 2025/8/25 16:40
 **/
@RestController
@RequestMapping("/receiveOrder")
public class OrderAddController {
    @Resource
    private OrderServiceFactory orderServiceFactory;
    @Resource
    private MqProducerService mqProducerService;
    @Value("${rocketmq.topic.addOrder}")
    private String rocketmqTopicOrder;
    /**
     * 订单提交
     * @return
     */
    @PostMapping("/addOrder")
    public Result addOrder(@RequestBody @Validated OrderDto orderDto){
        BaseOrderAddService orderAddService = orderServiceFactory.getOrderAddService(orderDto);
        //创建临时订单
        orderAddService.addTempOrder(orderDto);
        //发送订单消息到消息队列
        if(!mqProducerService.sendOrderMessage(rocketmqTopicOrder,orderDto)){
            //提交消息失败，删除库中的临时订单
            orderAddService.delTempOrder(orderDto.getId());
            return Result.error(ResultCodeEnum.ORDER_ADD_FAIL);
        }
        return Result.defaultSuccess(orderDto.getOrderId());
    }
}
