package com.centit.order.service.strategy;

import com.centit.core.consts.RedisConst;
import com.centit.core.dto.OrderDto;
import com.centit.core.enums.SellTypeEnum;
import com.centit.order.dao.*;
import com.centit.order.po.ShoppingGoodscartPo;
import com.centit.order.redis.RedisDataService;
import com.centit.order.service.BaseOrderAddService;
import com.centit.order.service.StockRedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 文创商品订单创建策略类
 * @Date : 2024/12/27 15:01
 **/
@Component
public class CulturalOrderAddService extends BaseOrderAddService {
    @Resource
    private ShoppingGoodsDao goodsDao;
    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;
    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;
    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private RedisDataService redisDataService;
    @Resource
    private StockRedisService stockRedisService;



    @Override
    public boolean validateOrder(OrderDto orderDto) {
        //校验是否下架
        if(!goodsDao.checkGoodsStatus(orderDto.getGoodsId())){
            super.updateOrderWithFail(orderDto.getOrderId(),"商品已下架");
            return false;
        }
        return true;
    }



    @Override
    public long cutStock(OrderDto orderDto) {
        String redisKey = StringUtils.isNotBlank(orderDto.getPropertys())? RedisConst.KEY_STOCK_GOODS + orderDto.getGoodsId() + ":" + orderDto.getPropertys():RedisConst.KEY_STOCK_GOODS + orderDto.getGoodsId();
        long res = redisDataService.cutStock(redisKey,orderDto.getGoodsCount());
        //脚本返回值标识未获取到库存，有可能是库存已经过期，所以要更新一次redis库存
        if(res==RedisConst.LUA_RES_STOCK_UNSET){
            stockRedisService.updateRedisGoodsStock(orderDto);
            //重新再执行一次库存扣减操作
            return redisDataService.cutStock(redisKey,orderDto.getGoodsCount());
        }
        return res;
    }

    @Override
    public void cutDBStock(OrderDto orderDto) {
        if(StringUtils.isNotBlank(orderDto.getPropertys())){
            goodsDao.cutInventoryStock(orderDto.getGoodsCount(),orderDto.getGoodsId(),orderDto.getPropertys());
        }else{
            goodsDao.cutGoodsStock(orderDto.getGoodsCount(),orderDto.getGoodsId());
        }
    }

    @Override
    public void saveNewCartInfo(ShoppingGoodscartPo goodscart,String scId, OrderDto orderDto) {
        goodscart.setScId(scId);
        goodscart.setGoodsId(orderDto.getGoodsId());
        goodscart.setCartType(SellTypeEnum.CULTURAL.getCartType());
        goodscart.setSpecInfo(orderDto.getSpecInfo());
        goodscart.setPropertys(orderDto.getPropertys());
        shoppingGoodscartDao.insert(goodscart);
    }


}
