package com.centit.mallserver.feign;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.dto.OrderDto;
import com.centit.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单服务接口
 * @Date : 2025/1/3 17:24
 **/
@Component
@FeignClient(value = "order")
public interface FeignOrderService {
    /**
     * 提交订单
     * @param orderDto 订单信息
     * @return
     */
    @RequestMapping(value = "/receiveOrder/addOrder",method = RequestMethod.POST)
    Result<JSONObject> addOrder(@Valid @RequestBody OrderDto orderDto);

}
