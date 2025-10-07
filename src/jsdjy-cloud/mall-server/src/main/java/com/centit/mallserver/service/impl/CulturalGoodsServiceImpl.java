package com.centit.mallserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.dto.OrderDto;
import com.centit.core.enums.SellTypeEnum;
import com.centit.core.result.Result;
import com.centit.core.result.ResultCodeEnum;
import com.centit.mallserver.consts.CulturalGoodsConst;
import com.centit.mallserver.consts.MallConst;
import com.centit.mallserver.dao.*;
import com.centit.mallserver.dto.ProductDto;
import com.centit.mallserver.feign.FeignOrderService;
import com.centit.mallserver.model.CulturalGoodsInfo;
import com.centit.mallserver.model.ShoppingGoodsclass;
import com.centit.mallserver.order.render.OrderRender;
import com.centit.mallserver.order.render.OrderRenderFactory;
import com.centit.mallserver.order.render.vo.GoodsOrderRenderVo;
import com.centit.mallserver.order.validate.OrderValidator;
import com.centit.mallserver.order.validate.OrderValidatorFactory;
import com.centit.mallserver.po.ShoppingEvaluatePo;
import com.centit.mallserver.po.ShoppingGoodsPo;
import com.centit.mallserver.redis.manager.GoodsRedisLockManager;
import com.centit.mallserver.service.CulturalGoodsService;
import com.centit.mallserver.threadPool.ThreadPoolExecutorFactory;
import com.centit.mallserver.vo.CulturalGoodsPageVo;
import com.centit.mallserver.vo.CulturalGoodsVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/12 14:49
 **/
@Service
@Slf4j
public class CulturalGoodsServiceImpl implements CulturalGoodsService {
    @Resource
    private ShoppingGoodsclassDao shoppingGoodsclassDao;
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingEvaluateDao shoppingEvaluateDao;
    @Resource
    private GoodsRedisLockManager goodsRedisService;
    @Resource
    private ShoppingFavoriteDao shoppingFavoriteDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;
    @Resource
    private OrderValidatorFactory orderValidatorFactory;
    @Resource
    private FeignOrderService feignOrderService;

    @Autowired
    private OrderRenderFactory orderRenderFactory;


    @Override
    public Result culGoodsClass(String parentId) {
        JSONObject bizDataJson = new JSONObject();
        bizDataJson.put("objList", shoppingGoodsclassDao.queryChildList(StringUtils.isBlank(parentId) ? CulturalGoodsConst.TOP_CLASS_ID : parentId));
        return Result.defaultSuccess(bizDataJson);
    }

    @Override
    public Result culGoodsClassTree() {
        JSONObject bizDataJson = new JSONObject();
        List<ShoppingGoodsclass> list = shoppingGoodsclassDao.queryAllList();
        JSONArray menu = new JSONArray();
        menuTree(list, menu, CulturalGoodsConst.TOP_CLASS_ID);
        bizDataJson.put("objList", menu);
        return Result.defaultSuccess(bizDataJson);
    }

    @Override
    public Result culGoodsList(HttpServletRequest request) {
        JSONObject bizDataJson = new JSONObject();
        int pageNo = request.getParameter("pageNo") == null ? 1 : Integer.parseInt(request.getParameter("pageNo"));
        int pageSize = request.getParameter("pageSize") == null ? 10 : Integer.parseInt(request.getParameter("pageSize"));
        String gcId = StringUtils.isBlank(request.getParameter("gcId")) ? CulturalGoodsConst.TOP_CLASS_ID : request.getParameter("gcId");
        Page<ShoppingGoodsPo> goodsInfoPoPage = PageHelper.startPage(pageNo, pageSize);
        List<ShoppingGoodsPo> goodsList = shoppingGoodsDao.selectClassGoodsList(gcId);
        bizDataJson.put("total", goodsInfoPoPage.getTotal());
        List<CulturalGoodsPageVo> goodsPageVOList = goodsList.stream().map(shoppingGoods -> {
            CulturalGoodsPageVo vo = new CulturalGoodsPageVo(shoppingGoods);
            if (shoppingGoods.getUseIntegralSet() == 1) {
                int integralValue = shoppingGoods.getUseIntegralValue();
                BigDecimal storePrice = shoppingGoods.getStorePrice();
                BigDecimal integralAmount = (new BigDecimal(integralValue).divide(new BigDecimal(MallConst.MONEY_TO_POINTS_SCALE))).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal restPrice = storePrice.subtract(integralAmount);
                //扣除固定积分值的现金价格
                vo.setRestPrice(restPrice.compareTo(BigDecimal.ZERO) >= 0 ? restPrice : BigDecimal.ZERO);
                //固定积分值
                vo.setIntegralValue(shoppingGoods.getUseIntegralValue());
            }
            return vo;
        }).collect(Collectors.toList());
        bizDataJson.put("objList", goodsPageVOList);
        return Result.defaultSuccess(bizDataJson);
    }

    public Result goodsDetail(String goodsId, HttpServletRequest request) {
        String userId = request.getParameter("userId");
        //查询商品信息
        CompletableFuture<CulturalGoodsInfo> culturalGoodsFuture = CompletableFuture.supplyAsync(() -> goodsRedisService.getGoodsInfo(goodsId, userId), ThreadPoolExecutorFactory.createThreadPoolExecutor());
        //查询评价信息
        CompletableFuture<ShoppingEvaluatePo> shoppingEvaluateFuture = CompletableFuture.supplyAsync(() -> shoppingEvaluateDao.selectLastEvaluate(goodsId), ThreadPoolExecutorFactory.createThreadPoolExecutor());
        //查询收藏标识
        CompletableFuture<Boolean> isFavFuture = CompletableFuture.supplyAsync(() -> shoppingFavoriteDao.selectIsFav(goodsId, SellTypeEnum.CULTURAL.getGoodsType(), userId), ThreadPoolExecutorFactory.createThreadPoolExecutor());

        CulturalGoodsVo culturalGoodsVo = CompletableFuture.allOf(culturalGoodsFuture, shoppingEvaluateFuture, isFavFuture)
                .thenApply(v -> buildCulturalGoodsVo(culturalGoodsFuture.join(), shoppingEvaluateFuture.join(), isFavFuture.join()))
                .exceptionally(ex -> {
                    log.error("查询商品详情失败，商品id：{}", goodsId, ex);
                    return CulturalGoodsVo.build().build();
                }).join();
        return Result.defaultSuccess(culturalGoodsVo);
    }

    private CulturalGoodsVo buildCulturalGoodsVo(CulturalGoodsInfo culturalGoodsInfo, ShoppingEvaluatePo shoppingEvaluate, Boolean isFav) {
        return CulturalGoodsVo.build()
                .goodsInfo(culturalGoodsInfo)
                .evaluateInfo(shoppingEvaluate)
                .favInfo(isFav)
                .build();
    }

    @Override
    public Result getGoodsExtraInfo(HttpServletRequest request) {
        JSONObject bizDataJson = new JSONObject();
        String goodsId = request.getParameter("goodsId");
        String userId = request.getParameter("userId");
        bizDataJson.put("evaluate", shoppingEvaluateDao.selectLastEvaluate(goodsId));
        bizDataJson.put("isFav", shoppingFavoriteDao.selectIsFav(goodsId, SellTypeEnum.CULTURAL.getGoodsType(), userId));
        return Result.defaultSuccess(bizDataJson);
    }

    @Override
    public Result checkLimitBuy(JSONObject reqJson) {
        String userId = reqJson.getString("userId");
        String goodsId = reqJson.getString("goodsId");
        //下单或加购数量
        int count = reqJson.getInteger("count");
        ShoppingGoodsPo goodsPo = shoppingGoodsDao.selectGoodsDetail(goodsId);
        JSONObject bizDataJson = new JSONObject();
        if (goodsPo.getLimitBuy() == 0) {
            bizDataJson.put("result", false);
        } else {
            int doneCount = shoppingGoodscartDao.selectHasCount(userId, goodsId, SellTypeEnum.CULTURAL.getGoodsType());
            bizDataJson.put("limitBuy", goodsPo.getLimitBuy());
            bizDataJson.put("doneCount", doneCount);
            bizDataJson.put("result", doneCount + count > goodsPo.getLimitBuy());
        }
        return Result.defaultSuccess(bizDataJson);
    }

    @Override
    public Result renderOrder(ProductDto productDto) {
        // 获取对应的订单渲染器
        OrderRender orderRender = orderRenderFactory.createOrderRender(SellTypeEnum.CULTURAL);
        // 渲染订单
        GoodsOrderRenderVo renderVo = orderRender.renderOrder(productDto);
        return Result.defaultSuccess(renderVo);
    }

    @Override
    public Result addOrder(OrderDto orderDto) {
        //获取文创类型订单校验器，校验订单信息
        OrderValidator validator = orderValidatorFactory.getValidator(SellTypeEnum.CULTURAL);
        validator.validate(orderDto);
        //调用订单服务接口，提交订单信息
        orderDto.setOrderType(SellTypeEnum.CULTURAL.getOrderType());
        Result<JSONObject> result = feignOrderService.addOrder(orderDto);
        if (result.getRetCode().equals("0")) {
            return Result.defaultSuccess(orderDto.getOrderId());
        } else {
            return Result.error(ResultCodeEnum.ORDER_ADD_FAIL);
        }
    }


    private void menuTree(List<ShoppingGoodsclass> list, JSONArray array, String fatherId) {
        for (ShoppingGoodsclass shoppingGoodsclass : list) {
            String father = null;
            if (null != shoppingGoodsclass.getParentId()) {
                father = shoppingGoodsclass.getParentId();
            }
            if (father != null && father.equals(fatherId)) {
                JSONObject obj = new JSONObject();
                obj.put("id", shoppingGoodsclass.getId());
                obj.put("parentId", shoppingGoodsclass.getParentId());
                obj.put("className", shoppingGoodsclass.getClassName());

                JSONArray children = new JSONArray();
                menuTree(list, children, shoppingGoodsclass.getId());
                obj.put("children", children);
                array.add(obj);
            }
        }
    }
}
