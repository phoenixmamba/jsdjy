package com.centit.order.mq.productor;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/10/8 10:38
 **/
@Component
@Slf4j
public class SendBack implements SendCallback {

    @Override
    public void onSuccess(SendResult sendResult) {
        log.info(sendResult.toString());
    }

    @Override
    public void onException(Throwable throwable) {

    }
}
