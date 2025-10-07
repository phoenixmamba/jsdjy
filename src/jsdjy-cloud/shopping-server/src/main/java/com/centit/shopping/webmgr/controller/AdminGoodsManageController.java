package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.AdminGoodsManageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>商品分类管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/adminGoods")
public class AdminGoodsManageController {

    @Resource
    private AdminGoodsManageService adminGoodsManageService;


    /**
     * 查询商品分类列表
     * @return
     */
    @GetMapping("/classPageList")
    public JSONObject queryClassPageList(HttpServletRequest request){
        return adminGoodsManageService.queryClassPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取下级分类
     * @return
     */
    @GetMapping("/childClasses")
    public JSONObject queryChildClass(HttpServletRequest request){
        return adminGoodsManageService.queryChildClass(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询商品类型
     * @return
     */
    @GetMapping("/goodstypes")
    public JSONObject queryGoodstypes(HttpServletRequest request){
        return adminGoodsManageService.queryGoodstypes(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增商品分类
     * @return
     */
    @PostMapping("/addGoodsclass")
    public JSONObject addGoodsclass(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.addGoodsclass(reqJson);
    }

    /**
     * 查询商品分类详情
     * @return
     */
    @GetMapping("/goodsclassDetail/{id}")
    public JSONObject queryGoodsclassDetail(@PathVariable String id,HttpServletRequest request){
        return adminGoodsManageService.queryGoodsclassDetail(id);
    }

    /**
     * 编辑商品分类
     * @return
     */
    @PostMapping("/editGoodsclass")
    public JSONObject editGoodsclass(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.editGoodsclass(reqJson);
    }

    /**
     * 删除商品分类
     * @return
     */
    @PostMapping("/delGoodsclass")
    public JSONObject delGoodsclass(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.delGoodsclass(reqJson);
    }

    /**
     * 查询商品规格分页列表
     * @return
     */
    @GetMapping("/specsPageList")
    public JSONObject querySpecsPageList(HttpServletRequest request){
        return adminGoodsManageService.querySpecsPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增商品规格
     * @return
     */
    @PostMapping("/addGoodsspecification")
    public JSONObject addGoodsspecification(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.addGoodsspecification(reqJson);
    }

    /**
     * 查询商品规格详情
     * @return
     */
    @GetMapping("/goodsspecificationDetail/{id}")
    public JSONObject queryGoodsspecificationDetail(@PathVariable String id,HttpServletRequest request){
        return adminGoodsManageService.queryGoodsspecificationDetail(id);
    }

    /**
     * 编辑商品规格
     * @return
     */
    @PostMapping("/editGoodsspecification")
    public JSONObject editGoodsspecification(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.editGoodsspecification(reqJson);
    }

    /**
     * 删除规格
     * @return
     */
    @PostMapping("/delGoodsspecification")
    public JSONObject delGoodsspecification(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.delGoodsspecification(reqJson);
    }

    /**
     * 查询商品类型分页列表
     * @return
     */
    @GetMapping("/typePageList")
    public JSONObject queryTypePageList(HttpServletRequest request){
        return adminGoodsManageService.queryTypePageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取初始商品规格列表
     * @return
     */
    @GetMapping("/querySpecifications")
    public JSONObject querySpecifications(HttpServletRequest request){
        return adminGoodsManageService.querySpecifications(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增商品类型
     * @return
     */
    @PostMapping("/addGoodstype")
    public JSONObject addGoodstype(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.addGoodstype(reqJson);
    }

    /**
     * 查询商品类型详情
     * @return
     */
    @GetMapping("/queryGoodstypeDetail/{id}")
    public JSONObject queryGoodstypeDetail(@PathVariable String id,HttpServletRequest request){
        return adminGoodsManageService.queryGoodstypeDetail(id);
    }

    /**
     * 编辑商品类型
     * @return
     */
    @PostMapping("/editGoodstype")
    public JSONObject editGoodstype(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.editGoodstype(reqJson);
    }

    /**
     * 删除商品类型
     * @return
     */
    @PostMapping("/delGoodstype")
    public JSONObject delGoodstype(@RequestBody JSONObject reqJson){
        return adminGoodsManageService.delGoodstype(reqJson);
    }
}