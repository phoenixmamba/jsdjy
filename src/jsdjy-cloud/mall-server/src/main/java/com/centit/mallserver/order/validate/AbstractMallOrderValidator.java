package com.centit.mallserver.order.validate;

import com.centit.core.consts.RedisConst;
import com.centit.core.dto.OrderDto;
import com.centit.core.exp.OrderValidationException;
import com.centit.mallserver.dao.ShoppingGoodscartDao;
import com.centit.mallserver.redis.service.RedisDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商城订单校验类
 * @Date : 2025/8/27 10:49
 **/
public abstract class AbstractMallOrderValidator implements OrderValidator {
    @Resource
    private RedisDataService redissonRedisDataService;
    @Resource
    protected ShoppingGoodscartDao shoppingGoodscartDao;

    @Value("${sign.secret.key}")
    protected String signSecretKey;

    @Override
    public final void validate(OrderDto orderDto) throws OrderValidationException {
        // 通用校验
        validateCommon(orderDto);

        // 特定订单类型校验
        validateSpecific(orderDto);
    }

    /**
     * 通用校验
     */
    private void validateCommon(OrderDto orderDto) throws OrderValidationException {
        // 1. 校验是否重复下单
        validateDuplicateOrder(orderDto);

        // 2. 校验商品状态是否发生变化
        validateGoodsStatePrice(orderDto);

        // 3. 校验购物车记录是否存在
        validateCartRecord(orderDto);

        // 4. 校验签名
        validateSignature(orderDto);
    }

    /**
     * 校验是否重复下单
     */
    private void validateDuplicateOrder(OrderDto orderDto) throws OrderValidationException {
        String orderId = orderDto.getOrderId();
        if(redissonRedisDataService.deleteKey(RedisConst.KEY_ORDER_RENDER+orderId)<=0){
            throw new OrderValidationException("订单重复提交");
        }
    }

    /**
     * 校验购物车记录是否存在
     */
    private void validateCartRecord(OrderDto orderDto) throws OrderValidationException {
        if (StringUtils.isNotBlank(orderDto.getCartId())&&!shoppingGoodscartDao.checkCartStatus(orderDto.getCartId())) {
            throw new OrderValidationException("购物车记录不存在");
        }
    }

    /**
     * 校验签名
     */
    private void validateSignature(OrderDto orderDto) throws OrderValidationException {
        OrderValidatorParams params = new OrderValidatorParams(orderDto);
        String expectedSignature = params.generateSignature(signSecretKey);
        if (!expectedSignature.equals(orderDto.getSign())) {
            throw new OrderValidationException("订单金额有误，请重新刷新订单");
        }
    }

    /**
     * 校验商品状态是否发生变化，由各个类型订单校验器实现
     */
    protected abstract void validateGoodsStatePrice(OrderDto orderDto) throws OrderValidationException;

    /**
     * 特定类型校验
     */
    protected abstract void validateSpecific(OrderDto orderDto) throws OrderValidationException;
}
