package com.centit.admin.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.admin.system.service.FDatadictionaryService;
import com.centit.core.util.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 数据字典前端控制器
 * @Date : 2021-03-04
 **/
@RestController
@RequestMapping("/webmgr/datadictionary")
public class FDatadictionaryController {

    @Resource
    private FDatadictionaryService fDatadictionaryService;

    /**
     * 获取字典类型
     * @return
     */
    @GetMapping("/catalogStyle")
    public JSONObject catalogStyle(HttpServletRequest request, HttpServletResponse response){
        return fDatadictionaryService.catalogStyle(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取字典分页列表
     * @return
     */
    @GetMapping("/dictionarys")
    public JSONObject queryDictionaryPageList(HttpServletRequest request, HttpServletResponse response){
        return fDatadictionaryService.queryDictionaryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 删除数据字典
     * @return
     */
    @PostMapping("/delete")
    public JSONObject delete(@RequestBody JSONObject reqJson){
        return fDatadictionaryService.delete(reqJson);
    }

    /**
     * 校验数据字典编码是否可用
     * @return
     */
    @GetMapping("/notexists/{catalogCode}")
    public JSONObject notexists(@PathVariable String catalogCode, HttpServletRequest request, HttpServletResponse response){
        return fDatadictionaryService.notexists(catalogCode);
    }

    /**
     * 新增数据字典
     * @return
     */
    @PostMapping("/add")
    public JSONObject add(@RequestBody JSONObject reqJson){
        return fDatadictionaryService.addDictionary(reqJson);
    }

    /**
     * 获取字典项请
     * @return
     */
    @GetMapping("/detail/{catalogCode}")
    public JSONObject queryDictionaryDetail(@PathVariable String catalogCode, HttpServletRequest request, HttpServletResponse response){
        return fDatadictionaryService.queryDictionaryDetail(catalogCode);
    }

    /**
     * 编辑数据字典
     * @return
     */
    @PostMapping("/edit")
    public JSONObject edit(@RequestBody JSONObject reqJson){
        return fDatadictionaryService.editDictionary(reqJson);
    }

    /**
     * 根据字典code获取字典项
     * @return
     */
    @GetMapping("/values/{catalogCode}")
    public JSONObject queryDictionarys(@PathVariable String catalogCode, HttpServletRequest request, HttpServletResponse response){
        return fDatadictionaryService.queryDictionarys(catalogCode);
    }
}