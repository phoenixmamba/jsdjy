package com.centit.admin.shopping.service.impl;

import cn.hutool.core.util.StrUtil;
import com.centit.core.consts.RedisConst;
import com.centit.admin.redis.LockServiceBase;
import com.centit.admin.redis.RedissonRedisDataService;
import com.centit.admin.shopping.dao.GoodsInfoDao;
import com.centit.admin.shopping.dao.GoodsSpecInventoryDao;
import com.centit.admin.shopping.po.GoodsSpecInventoryPo;
import com.centit.admin.shopping.service.GoodsStockService;
import com.centit.core.exp.BusinessException;
import com.centit.core.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/4 16:30
 **/
@Service
@Slf4j
public class GoodsLockServiceImpl extends LockServiceBase implements GoodsStockService {
    @Resource
    private RedissonRedisDataService redissonRedisDataService;
    @Resource
    private GoodsInfoDao goodsInfoDao;
    @Resource
    private GoodsSpecInventoryDao goodsSpecInventoryDao;

    @Override
    public Integer getGoodsStock(String goodsId) {
        Integer goodsStock = redissonRedisDataService.currentStock(RedisConst.KEY_STOCK_GOODS + goodsId);
        if (goodsStock == null) {
            String batchNo = RedisConst.KEY_STOCK_GOODS + goodsId;
            return executeWithLockAndReturn(batchNo, () -> {
                int stock = goodsInfoDao.selectGoodsStock(goodsId);
                redissonRedisDataService.setStock(batchNo, stock, RedisConst.EXPIRE_MINUTES_STOCK_GOODS);
                return stock;
            });
        }
        return goodsStock;
    }

    @Override
    public Integer getGoodsStockWithPropertys(String goodsId, String propertys) {
        Integer goodsStock = redissonRedisDataService.currentStock(RedisConst.KEY_STOCK_GOODS + goodsId + ":" + propertys);
        if (goodsStock == null) {
            String batchNo = RedisConst.KEY_STOCK_GOODS + goodsId + ":" + propertys;
            return executeWithLockAndReturn(batchNo, () -> {
                int stock = goodsSpecInventoryDao.selectInventoryStock(goodsId, propertys);
                redissonRedisDataService.setStock(batchNo, stock, RedisConst.EXPIRE_MINUTES_STOCK_GOODS);
                return stock;
            });
        }
        return goodsStock;
    }

    @Override
    public void initGoodsStock(String goodsId, int stock) {
        String batchNo = RedisConst.KEY_STOCK_GOODS + goodsId;
        executeWithLock(batchNo, () -> redissonRedisDataService.setStock(batchNo, stock, RedisConst.EXPIRE_MINUTES_STOCK_GOODS));
    }

    @Override
    public void initGoodsStockWithProperty(String goodsId, String propertys, int stock) {
        String batchNo = RedisConst.KEY_STOCK_GOODS + goodsId + ":" + propertys;
        executeWithLock(batchNo, () -> {
            redissonRedisDataService.setStock(batchNo, stock, RedisConst.EXPIRE_MINUTES_STOCK_GOODS);
        });
    }

    @Override
    public void initGoodsStockWithPropertyList(String goodsId, int stock, List<GoodsSpecInventoryPo> goodsSpecInventoryPoList) {
        String batchNo = RedisConst.KEY_STOCK_GOODS + goodsId;
        executeWithLock(batchNo, () -> {
            redissonRedisDataService.setStock(batchNo, stock, RedisConst.EXPIRE_MINUTES_STOCK_GOODS);
            goodsSpecInventoryPoList.forEach(goodsSpecInventoryPo -> redissonRedisDataService.setStock(RedisConst.KEY_STOCK_GOODS + goodsId + ":" + goodsSpecInventoryPo.getPropertys(), goodsSpecInventoryPo.getCount(), RedisConst.EXPIRE_MINUTES_STOCK_GOODS));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cutGoodsStock(String goodsId, String propertys, int stock) {
        String batchNo = StrUtil.isNotBlank(propertys) ? RedisConst.KEY_STOCK_GOODS + goodsId
                : RedisConst.KEY_STOCK_GOODS + goodsId + ":" + propertys;
        executeWithLock(batchNo, () -> {
            //先扣减数据库库存
            if (StrUtil.isNotBlank(propertys)) {
                goodsSpecInventoryDao.cutSpecStock(goodsId, propertys, stock);
            } else {
                goodsInfoDao.cutGoodsStock(goodsId, stock);
            }
            int res = redissonRedisDataService.cutStock(batchNo, stock);
            if (res < 0) {
                log.error("redis扣减商品库存失败，key:{}，扣减值：{},返回值：{}", batchNo, stock, res);
                throw new BusinessException(ResultCodeEnum.UPDATE_EXCEPTION);
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoodsStock(String goodsId, String propertys, int stock) {
        String batchNo = StrUtil.isBlank(propertys) ? RedisConst.KEY_STOCK_GOODS + goodsId
                : RedisConst.KEY_STOCK_GOODS + goodsId + ":" + propertys;
        executeWithLock(batchNo, () -> {
            if (StrUtil.isNotBlank(propertys)) {
                goodsSpecInventoryDao.addSpecStock(goodsId, propertys, stock);
            } else {
                goodsInfoDao.addGoodsStock(goodsId, stock);
            }
            redissonRedisDataService.addStock(batchNo, stock);
        });
    }
}
