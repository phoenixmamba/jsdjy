package com.centit.thirdserver.biz.controller;

import com.centit.core.dto.SmsEntity;
import com.centit.core.result.Result;
import com.centit.thirdserver.biz.service.SmsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 9:47
 **/
@RestController
@RequestMapping("/sms")
public class SmsController {
    @Resource
    private SmsService smsService;

    /**
     * 订单渲染
     * @return
     */
    @PostMapping("/sendTemplateSMS")
    public Result sendTemplateSMS(@RequestBody @Validated SmsEntity smsEntity){
        return smsService.sendTemplateSMS(smsEntity);
    }
}
