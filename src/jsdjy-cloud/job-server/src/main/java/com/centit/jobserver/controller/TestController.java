package com.centit.jobserver.controller;

import com.centit.jobserver.dao.ParkDataHourlyFlowDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-19
 **/
@RestController
@RequestMapping("/carousel")
public class TestController {

    @Resource
    private ParkDataHourlyFlowDao parkDataHourlyFlowDao;
    @Autowired
    private ApplicationContext context;


}