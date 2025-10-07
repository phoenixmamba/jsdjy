package com.centit.order.controller;

import com.centit.core.result.Result;
import com.centit.order.mq.productor.MqProducerService;
import com.centit.order.service.strategy.OrderServiceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单接收控制器
 * @Date : 2025/8/25 16:40
 **/
@RestController
@RequestMapping("/status")
public class OrderStatusController {
    @Resource
    private OrderServiceFactory orderServiceFactory;
    @Resource
    private MqProducerService mqProducerService;
    @Value("${rocketmq.topic.addOrder}")
    private String rocketmqTopicOrder;

    /**
     * 更新订单状态为已支付
     *
     * @return
     */
    @PostMapping("/updateOrderStatusToPaid")
    public Result updateOrderStatusToPaid(@RequestParam("orderId") String orderId) {
        //ToDo 处理订单已支付状态更新逻辑
        return Result.defaultSuccess();
    }

    /**
     * 更新订单状态为异常
     *
     * @return
     */
    @PostMapping("/updateOrderStatusToAbnormal")
    public Result updateOrderStatusToAbnormal(@RequestParam("orderId") String orderId, @RequestParam("msg") String msg) {
        //ToDo 处理订单异常状态更新逻辑

        return Result.defaultSuccess();
    }
}
