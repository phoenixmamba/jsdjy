package com.centit.order.mq.productor;

import com.centit.core.dto.OrderDto;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/26 16:37
 **/
public interface MqProducerService {
    /**
     * 发送消息
     * @param topic 主题
     * @param message 消息内容
     * @return true:发送成功，false:发送失败
     */
    boolean sendOrderMessage(String topic, OrderDto message);
}
