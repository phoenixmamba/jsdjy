package com.centit.zuulgateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.zuulgateway.service.FUserroleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>用户角色<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2020-09-16
 **/
@RestController
@RequestMapping("/zuulgateway/fUserrole")
public class FUserroleController {

    @Resource
    private FUserroleService userroleService ;

    /**
     * 查询列表
     * @return
     */
    @PostMapping("/queryList")
    public JSONObject queryList(@RequestBody JSONObject reqJson){
        JSONObject retJson = new JSONObject();
        return retJson;
    }

}