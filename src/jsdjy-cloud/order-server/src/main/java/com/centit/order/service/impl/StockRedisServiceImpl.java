package com.centit.order.service.impl;

import com.centit.core.consts.RedisConst;
import com.centit.core.dto.OrderDto;
import com.centit.order.dao.ShoppingGoodsDao;
import com.centit.order.redis.LockServiceBase;
import com.centit.order.redis.RedisDataService;
import com.centit.order.service.StockRedisService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/2 21:25
 **/
@Service
public class StockRedisServiceImpl extends LockServiceBase implements StockRedisService {
    @Resource
    private RedisDataService redisDataService;
    @Resource
    private ShoppingGoodsDao goodsDao;

    @Override
    public void updateRedisGoodsStock(OrderDto orderDto) {
        String batchNo = StringUtils.isNotBlank(orderDto.getPropertys())?RedisConst.KEY_STOCK_GOODS + orderDto.getGoodsId()+ ":" + orderDto.getPropertys():RedisConst.KEY_STOCK_GOODS + orderDto.getGoodsId();
        executeWithLock(batchNo, () -> {
            int stock;
            if(StringUtils.isNotBlank(orderDto.getPropertys())){
                stock = goodsDao.selectInventoryStock(orderDto.getGoodsId(),orderDto.getPropertys());
            }else{
                stock = goodsDao.selectGoodsStock(orderDto.getGoodsId());
            }
            redisDataService.setNewInt(batchNo, stock, RedisConst.EXPIRE_MINUTES_STOCK_GOODS);
        });
    }
}
