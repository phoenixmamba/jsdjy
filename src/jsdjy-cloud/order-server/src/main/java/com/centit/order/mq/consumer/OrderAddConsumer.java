package com.centit.order.mq.consumer;

import com.centit.core.consts.RedisConst;
import com.centit.core.dto.OrderDto;
import com.centit.order.service.BaseOrderAddService;
import com.centit.order.service.strategy.OrderServiceFactory;
import com.centit.order.redis.RedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/27 14:07
 **/
@RocketMQMessageListener(topic = "${rocketmq.topic.addOrder}", consumerGroup = "${rocketmq.consumer.group.addOrder}", consumeMode = ConsumeMode.ORDERLY)
@Component
@Slf4j
public class OrderAddConsumer implements RocketMQListener<OrderDto> {
    @Resource
    private OrderServiceFactory orderServiceFactory;
    @Resource
    private RedisDataService redisDataService;

    @Override
    public void onMessage(OrderDto s) {
        if (s == null || s.getOrderId() == null) {
            log.warn("接收到无效订单消息，消息内容为空或订单ID为空");
            return;
        }

        String orderId = s.getOrderId();
        String redisKey = RedisConst.KEY_CONSUMER_ORDER_ADD + orderId;

        try {
            // 校验订单id,防止重复消费
            if (!redisDataService.setNewInt(redisKey, 0, RedisConst.EXPIRE_MINUTES_CONSUMER_ORDER_ADD)) {
                log.info("接收到重复消费消息，订单ID：{}", orderId);
                return;
            }

            BaseOrderAddService orderAddService = orderServiceFactory.getOrderAddService(s);
            orderAddService.addOrder(s);

            log.info("成功处理新增订单消息，订单ID：{}", orderId);
        } catch (Exception e) {
            // 消费异常时要删除redis中的校验key，保证该消息可以被再次消费
            redisDataService.deleteKey(redisKey);
            log.error("处理新增订单消息发生异常，订单ID：{}，异常信息：", orderId, e);
            throw new RuntimeException("处理新增订单消息失败", e);
        }
    }


}
