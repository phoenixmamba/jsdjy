package com.centit.mallserver.order.render;

import com.alibaba.fastjson.JSONArray;
import com.centit.core.consts.RedisConst;
import com.centit.core.model.OrderAmount;
import com.centit.mallserver.dto.ProductDto;
import com.centit.mallserver.model.UserAccountInfo;
import com.centit.mallserver.model.UserInfo;
import com.centit.mallserver.order.validate.OrderValidatorParams;
import com.centit.mallserver.po.ShoppingAssetRulePo;
import com.centit.mallserver.po.ShoppingPayLimitPo;
import com.centit.mallserver.redis.service.RedisDataService;
import com.centit.mallserver.service.UserInfoService;
import com.centit.mallserver.third.CrmService;
import com.centit.mallserver.order.render.vo.GoodsOrderRenderVo;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商城订单抽象渲染器
 * @Date : 2024/12/19 14:26
 **/
public abstract class AbstractMallOrderRender implements OrderRender {
    @Resource
    protected UserInfoService userInfoService;
    @Resource
    protected RedisDataService redissonRedisDataService;
    @Resource
    protected CrmService crmService;

    @Value("${snowflake.datacenterId}")
    protected long datacenterId;
    @Value("${snowflake.workerId}")
    protected long workerId;
    @Value("${sign.secret.key}")
    protected String signSecretKey;

    protected ProductDto productDto;

    protected OrderAmount orderAmount;

    protected String userId;

    protected String mzUserId;

    protected UserInfo userInfo;

    protected UserAccountInfo userAccountInfo;

    protected ShoppingPayLimitPo payLimit;

    protected ShoppingAssetRulePo assetRule;

    protected JSONArray couponList;

    protected GoodsOrderRenderVo goodsOrderRenderVo;

    public AbstractMallOrderRender(){
    }

    /**
     * 商城订单渲染主流程
     * @param productDto
     * @return
     */
    public GoodsOrderRenderVo renderOrder(ProductDto productDto) {
        // 1. 初始化环境
        initialize(productDto);

        // 2. 获取用户信息
        loadUserInfo();

        // 3. 获取商品信息
        loadGoodsInfo(productDto);

        // 4. 初始化订单金额
        initOrderAmount(productDto);

        // 5. 计算各种优惠
        calculateDiscounts();

        // 6. 构建返回信息
        return buildRenderInfo();
    }

    /**
     * 初始化环境
     * @param productDto 商品基本信息
     */
    protected void initialize(ProductDto productDto){
        this.userId=productDto.getUserId();
        this.productDto=productDto;
    }

    /**
     * 查询用户详细信息
     */
    protected void loadUserInfo(){
        this.userInfo= userInfoService.getUserInfo(userId);
        this.mzUserId = userInfo.getMzuserid();
        this.userAccountInfo = userInfoService.getUserAccountInfo(mzUserId);
        this.payLimit = userInfoService.getPayLimit();
        this.assetRule = userInfoService.getAssetRule();
    }

    /**
     * 加载商品信息
     */
    protected abstract void loadGoodsInfo(ProductDto productDto);

    /**
     * 初始化订单金额
     */
    protected abstract void initOrderAmount(ProductDto productDto);

    /**
     * 获取订单可用优惠券列表
     */
    protected abstract void getGoodsOrderCouponList();

    /**
     * 计算各种优惠
     */
    protected abstract void calculateDiscounts();

    /**
     * 构建返回信息
     */
    protected abstract GoodsOrderRenderVo buildRenderInfo();

    /**
     * 构建商城订单通用信息并返回签名
     */
    public String buildMallOrderCommonRenderInfo(String orderId){
        //保存订单id到redis，用于防止重复下单
        redissonRedisDataService.setIntWithExpireTime(RedisConst.KEY_ORDER_RENDER+orderId,0,RedisConst.EXPIRE_MINUTES_ORDER_RENDER);
        //生成订单金额防篡改签名校验参数
        OrderValidatorParams orderValidatorParams = new OrderValidatorParams(goodsOrderRenderVo);
        return orderValidatorParams.generateSignature(signSecretKey);
    }
}
