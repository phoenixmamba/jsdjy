package com.centit.admin.shopping.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.admin.shopping.dto.GoodInfoDTO;
import com.centit.admin.shopping.service.SellerGoodsService;
import com.centit.core.result.Result;
import com.centit.core.util.RequestParametersUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>商户商品<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/sellerGoods")
public class SellerGoodsController {

    @Resource
    private SellerGoodsService sellerGoodsService;

    /**
     * 查询商品分页列表
     * @return
     */
    @GetMapping("/goodsPageList")
    public Result queryList(HttpServletRequest request){
        return sellerGoodsService.queryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

        /**
     * 校验商品名称是否已存在
     * @return
     */
    @GetMapping("/checkGoodsName")
    public Result checkGoodsName(HttpServletRequest request){
        return sellerGoodsService.checkGoodsName(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 上/下架商品
     * @return
     */
    @PostMapping("/putGoods")
    public Result putGoods(@RequestBody JSONObject reqJson){
        return sellerGoodsService.putGoods(reqJson);
    }

    /**
     * 删除商品
     * @return
     */
    @PostMapping("/delGoods")
    public Result delGoods(@RequestBody JSONObject reqJson){
        return sellerGoodsService.delGoods(reqJson);
    }

    /**
     * 查询商品分类列表
     * @return
     */
    @GetMapping("/queryGoodsClass")
    public Result queryGoodsClass(HttpServletRequest request){
        return sellerGoodsService.queryGoodsClass(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取商品详情
     * @return
     */
    @GetMapping("/goodsDetail/{goodsId}")
    public Result goodsDetail(@PathVariable String goodsId,HttpServletRequest request){
        return sellerGoodsService.goodsDetail(goodsId);
    }

    /**
     * 查询商品分类默认规格配置
     * @return
     */
    @GetMapping("/queryDefaultSpecification")
    public Result queryDefaultSpecification(HttpServletRequest request){
        return sellerGoodsService.queryDefaultSpecification(RequestParametersUtil.getRequestParametersRetJson(request));
    }



    /**
     * 发布新商品
     * @return
     */
    @PostMapping("/addGoods")
    public Result addGoods(@Validated @RequestBody GoodInfoDTO goodInfoDto){
        return sellerGoodsService.addGoods(goodInfoDto);
    }

    /**
     * 编辑商品
     * @return
     */
    @PostMapping("/editGoods")
    public Result editGoods(@Validated @RequestBody GoodInfoDTO goodInfoDto){
        return sellerGoodsService.editGoods(goodInfoDto);
    }
}