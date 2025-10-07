package com.centit.order.mq.productor;

import com.centit.core.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/26 16:38
 **/
@Service
@Slf4j
public class RocketMqProducerServiceImpl implements MqProducerService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

//    public RocketMqProducerServiceImpl(RocketMQTemplate rocketMqTemplate){
//        this.rocketMQTemplate=rocketMqTemplate;
//    }

    @Override
    public boolean sendOrderMessage(String topic, OrderDto orderContent) {
        try{
            rocketMQTemplate.syncSendOrderly(topic,orderContent,orderContent.getOrderId());
        }catch (Exception e){
            log.error("向rocketMQ发送下单消息失败,消息内容：{}",orderContent,e);
            return false;
        }
        return true;
    }
}
