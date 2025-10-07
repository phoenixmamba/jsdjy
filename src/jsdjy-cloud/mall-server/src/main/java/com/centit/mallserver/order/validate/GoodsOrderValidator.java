package com.centit.mallserver.order.validate;

import com.centit.core.consts.StoreConst;
import com.centit.core.dto.OrderDto;
import com.centit.core.enums.SellTypeEnum;
import com.centit.core.exp.OrderValidationException;
import com.centit.mallserver.dao.ShoppingGoodsDao;
import com.centit.mallserver.po.ShoppingGoodsPo;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商品订单校验器
 * @Date : 2025/8/27 11:06
 **/
@Scope("prototype")
@Component
public class GoodsOrderValidator extends AbstractMallOrderValidator {
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    private ShoppingGoodsPo shoppingGoodsPo;

    /**
     * 校验商品状态是否变化
     *
     * @param orderDto 订单信息
     * @throws OrderValidationException 校验失败时抛出异常
     */
    @Override
    protected void validateGoodsStatePrice(OrderDto orderDto) throws OrderValidationException {
        ShoppingGoodsPo shoppingGoodsPo = getShoppingGoodsPo(orderDto.getGoodsId());
        if (shoppingGoodsPo == null
                || shoppingGoodsPo.getDeleteStatus().equals(StoreConst.GOODS_DELETE_STATUS_ON)
                || shoppingGoodsPo.getGoodsStatus().equals(StoreConst.GOODS_SALE_STATUS_OFF)
                || orderDto.getUnitPrice().compareTo(shoppingGoodsPo.getGoodsPrice()) != 0) {
            throw new OrderValidationException("当前商品状态发生变化");
        }
    }

    @Override
    protected void validateSpecific(OrderDto orderDto) throws OrderValidationException {
        // 1. 校验商品限购
        validatePurchaseLimit(orderDto);
    }

    /**
     * 校验商品限购
     *
     * @param orderDto 订单信息
     * @throws OrderValidationException 校验失败时抛出异常
     */
    private void validatePurchaseLimit(OrderDto orderDto) throws OrderValidationException {
        ShoppingGoodsPo shoppingGoodsPo = getShoppingGoodsPo(orderDto.getGoodsId());
        int limitBuy = shoppingGoodsPo.getLimitBuy();
        if (limitBuy > 0) {
            int doneCount = shoppingGoodscartDao.selectHasCount(orderDto.getUserId(), orderDto.getGoodsId(), SellTypeEnum.CULTURAL.getGoodsType());
            if (doneCount + orderDto.getGoodsCount() > limitBuy) {
                throw new OrderValidationException("当前商品已超出限购数量");
            }
        }
    }

    /**
     * 获取商品信息，未获取到则查询数据库并缓存
     *
     * @param goodsId 商品ID
     * @return 商品信息
     * @throws OrderValidationException 商品不存在时抛出异常
     */
    private ShoppingGoodsPo getShoppingGoodsPo(String goodsId) throws OrderValidationException {
        // 如果缓存中没有，则查询数据库
        if (shoppingGoodsPo == null) {
            shoppingGoodsPo = shoppingGoodsDao.selectGoodsDetail(goodsId);
            // 提前校验商品是否存在
            if (shoppingGoodsPo == null) {
                throw new OrderValidationException("商品不存在");
            }
        }

        return shoppingGoodsPo;
    }
}
