package com.centit.pay.feign;

import com.centit.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单服务模块接口
 * @Date : 2025/1/3 17:24
 **/
@Component
@FeignClient(value = "order")
public interface FeignOrderService {

    /**
     * 更新订单状态为已支付
     * @param orderId 订单id
     * @return
     */
    @RequestMapping(value = "/status/updateOrderStatusToPaid",method = RequestMethod.POST)
    Result<String> updateOrderStatusToPaid(@RequestParam("orderId") String orderId);

    /**
     * 更新订单状态为异常
     * @param orderId 订单id
     * @return
     */
    @RequestMapping(value = "/status/updateOrderStatusToAbnormal",method = RequestMethod.POST)
    Result<String> updateOrderStatusToAbnormal(@RequestParam("orderId") String orderId, @RequestParam("msg") String msg);
}
