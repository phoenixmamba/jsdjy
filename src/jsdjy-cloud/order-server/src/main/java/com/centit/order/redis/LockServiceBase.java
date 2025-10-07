package com.centit.order.redis;

import com.centit.core.consts.RedisConst;
import com.centit.core.exp.BusinessException;
import com.centit.core.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 基础服务类，获取redis分布式锁和处理锁异常
 * @Date : 2024/12/11 15:33
 **/
@Slf4j
public class LockServiceBase {
    @Resource
    private RedissonClient redissonClient;

    protected void executeWithLock(String batchNo,Runnable action) {
        try {
            final RLock rLock = redissonClient.getLock(batchNo+":LOCK");
            if (rLock.tryLock(RedisConst.LOCK_WAIT_SECONDS, RedisConst.LOCK_LEASE_SECONDS, TimeUnit.SECONDS)) {
                try {
                    action.run();
                } finally {
                    rLock.unlock();
                }
            } else {
                log.error("未能在规定时间内获取到redis锁：{}", rLock.getName());
                throw new BusinessException(ResultCodeEnum.QUERY_EXCEPTION);
            }
        } catch (InterruptedException e) {
            log.error("更新redis数据异常:", e);
            throw new BusinessException(ResultCodeEnum.SYSTEM_EXCEPTION);
        }
    }

    protected <T> T executeWithLockAndReturn(String batchNo,Supplier<T> supplier) {
        try {
            final RLock rLock = redissonClient.getLock(batchNo+":LOCK");
            if (rLock.tryLock(RedisConst.LOCK_WAIT_SECONDS, RedisConst.LOCK_LEASE_SECONDS, TimeUnit.SECONDS)) {
                try {
                    return supplier.get();
                } finally {
                    rLock.unlock();
                }
            } else {
                log.error("未能在规定时间内获取到redis锁：{}", rLock.getName());
                throw new BusinessException(ResultCodeEnum.QUERY_EXCEPTION);
            }
        } catch (InterruptedException e) {
            log.error("更新redis数据异常:", e);
            throw new BusinessException(ResultCodeEnum.SYSTEM_EXCEPTION);
        }
    }
}
