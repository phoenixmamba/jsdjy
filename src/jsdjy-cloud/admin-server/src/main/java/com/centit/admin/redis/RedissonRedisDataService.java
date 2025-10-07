package com.centit.admin.redis;

import cn.hutool.core.util.RandomUtil;
import com.centit.core.exp.BusinessException;
import com.centit.core.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


/**
 * @author cui_jian
 */
@Service
@Slf4j
public class RedissonRedisDataService implements RedisDataService {
    /**
     * 执行扣库存的Lua脚本
     */
    public static final String STOCK_LUA;
    /**
     * Redisson 客户端
     */
    @Resource
    private RedissonClient redissonClient;

    static {
        /*
         * @desc 扣减库存Lua脚本
         * 库存（stock）-1：表示不限库存
         * 库存（stock） 0：表示没有库存
         * 库存（stock）大于0：表示剩余库存
         *
         * @params 库存key
         * @return
         *      -3:库存未初始化
         *      -2:库存不足
         *      -1:不限库存
         *      大于等于0: 剩余库存（扣减之后剩余的库存）,
         *      redis缓存的库存(value)是-1表示不限库存，直接返回-1
         */
        final StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("if (redis.call('exists', KEYS[1]) == 1) then");
        strBuilder.append("    local stock = tonumber(redis.call('get', KEYS[1]));");
        strBuilder.append("    local num = tonumber(ARGV[1]);");
        strBuilder.append("    if (stock == -1) then");
        strBuilder.append("        return -1;");
        strBuilder.append("    end;");
        strBuilder.append("    if (stock >= num) then");
        strBuilder.append("        return redis.call('incrby', KEYS[1], 0 - num);");
        strBuilder.append("    end;");
        strBuilder.append("    return -2;");
        strBuilder.append("end;");
        strBuilder.append("return -3;");
        STOCK_LUA = strBuilder.toString();
    }


    @Override
    public Integer currentStock(String key) {
        try {
            RBucket<Integer> bucket = redissonClient.getBucket(key);
            return bucket.get();
        }catch (Exception e){
            log.error("redission查询库存异常，业务id：{}，异常信息：",key,e);
            throw new BusinessException(ResultCodeEnum.QUERY_EXCEPTION);
        }

    }

    @Override
    public void setStock(String key,int num,long expireMinutes) {
        try{
            RBucket<Integer> bucket = redissonClient.getBucket(key);
            bucket.set(num);
            bucket.expire(expireMinutes*60+ RandomUtil.randomInt(-100,100), TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("redission更新库存异常,业务id：{},库存值：{}，失效时间：{},异常信息：",key,num,expireMinutes,e);
            throw new BusinessException(ResultCodeEnum.UPDATE_EXCEPTION);
        }

    }

    @Override
    public Integer cutStock(String key, int num) {
        try {
            // 执行扣减库存LUA脚本
            RScript rScript = redissonClient.getScript();
            // 执行Lua脚本
            // 第一个参数是脚本的模式（READ_WRITE，因为脚本会修改库存）
            // 第二个参数是脚本内容
            // 第三个参数是返回类型（这里返回一个整数值）
            // 第四个参数是键的列表（KEYS数组）
            // 第五个参数是值的列表（ARGV数组）
            return rScript.eval(
                    RScript.Mode.READ_WRITE,
                    STOCK_LUA,
                    RScript.ReturnType.INTEGER,
                    Collections.singletonList(key),
                    Collections.singletonList(Integer.toString(num)));
        } catch (Exception e) {
            log.error("redission执行库存扣减脚本异常,业务id：{},扣减库存值：{}，异常信息：",key,num,e);
            throw new BusinessException(ResultCodeEnum.UPDATE_EXCEPTION);
        }
    }

    @Override
    public void addStock(String key,int num) {
        try{
            RAtomicLong atomicLong = redissonClient.getAtomicLong("batchNo");
            atomicLong.addAndGet(num);
        }catch (Exception e){
            log.error("redission添加库存异常,业务id：{},添加库存值：{}，异常信息：",key,num,e);
            throw new BusinessException(ResultCodeEnum.UPDATE_EXCEPTION);
        }
    }

    @Override
    public void deleteKey(String key) {
        try{
            RKeys keys = redissonClient.getKeys();
            keys.delete(key);
        }catch (Exception e){
            log.error("redission删除key异常,业务id：{}，异常信息：",key,e);
            throw new BusinessException(ResultCodeEnum.UPDATE_EXCEPTION);
        }
    }


}