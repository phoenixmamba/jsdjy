package com.centit.shopping.biz.controller;

import com.centit.core.result.Result;
import com.centit.shopping.biz.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-19
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;

    /**
     * 查询列表
     *
     * @return
     */
    @GetMapping("/queryList")
    public Result queryList(HttpServletRequest request) {
        return testService.getRule();
    }

}