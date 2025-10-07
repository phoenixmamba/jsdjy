package com.centit.zuulgateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.zuulgateway.service.TRoleuserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>移动端角色-用户关联<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2020-10-14
 **/
@RestController
@RequestMapping("/zuulgateway/tRoleuser")
public class TRoleuserController {

    @Resource
    private TRoleuserService roleuserService;

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