package com.centit.mallserver.redis.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.centit.core.exp.BusinessException;
import com.centit.core.result.ResultCodeEnum;
import com.centit.mallserver.redis.service.RedisDataService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


/**
 * @author cui_jian
 * @date 2025/08/27
 * @description RedissonRedisDataServiceImpl
 */
@Service
@Slf4j
public class RedissonRedisDataServiceImpl implements RedisDataService {

    /**
     * Redisson 客户端
     */
    @Resource
    private RedissonClient redissonClient;

    @Override
    public long deleteKey(String key) {
        try{
            RKeys keys = redissonClient.getKeys();
            return keys.delete(key);
        }catch (Exception e){
            log.error("redisson删除key异常,业务id：{}，异常信息：",key,e);
            throw new BusinessException(ResultCodeEnum.UPDATE_EXCEPTION);
        }
    }

    @Override
    public void setIntWithExpireTime(String key, int num, long expireMinutes) {
        try{
            RBucket<Integer> bucket = redissonClient.getBucket(key);
            bucket.set(num);
            bucket.expire(expireMinutes*60+ RandomUtil.randomInt(-100,100), TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("redisson更新数据异常,业务id：{},库存值：{}，失效时间：{},异常信息：",key,num,expireMinutes,e);
            throw new BusinessException(ResultCodeEnum.UPDATE_EXCEPTION);
        }

    }

    @Override
    public void setStringWithExpireTime(String key, String str, long expireMinutes) {
        try{
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.set(str);
            bucket.expire(expireMinutes*60+ RandomUtil.randomInt(-100,100), TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("redisson更新数据异常,业务id：{},库存值：{}，失效时间：{},异常信息：",key,str,expireMinutes,e);
            throw new BusinessException(ResultCodeEnum.UPDATE_EXCEPTION);
        }
    }

    @Override
    public String queryStringInfo(String key) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(key);
            return bucket.get();
        }catch (Exception e){
            log.error("redisson查询数据异常，业务id：{}，异常信息：",key,e);
            throw new BusinessException(ResultCodeEnum.QUERY_EXCEPTION);
        }
    }

    @Override
    public Integer queryInt(String key) {
        try {
            RBucket<Integer> bucket = redissonClient.getBucket(key);
            return bucket.get();
        }catch (Exception e){
            log.error("redisson查询数据异常，业务id：{}，异常信息：",key,e);
            throw new BusinessException(ResultCodeEnum.QUERY_EXCEPTION);
        }

    }

    @Override
    public void addSortedSetValue(String key,String value,long score){
        RScoredSortedSet<String> browseHistory = redissonClient.getScoredSortedSet(key);
        browseHistory.add(score,value);
    }


}