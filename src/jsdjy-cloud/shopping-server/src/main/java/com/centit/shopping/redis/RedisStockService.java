package com.centit.shopping.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class RedisStockService implements IStockCallback {
    public static final Log log = LogFactory.getLog(RedisStockService.class);
    /**
     * 库存还未初始化
     */
    public static final long UNINITIALIZED_STOCK = -3L;
    /**
     * 判断商品是否存在KEY标识
     */
    public static final long EXIST_FLAG = -2L;
    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY_STOCK = "REDIS_KEY:STOCK:";
    /**
     * 执行扣库存的Lua脚本
     */
    public static final String STOCK_LUA;
    /**
     * Redisson 客户端
     */
    @Resource
    private RedissonClient redissonClient;
    /**
     * Redis 客户端
     */
    @Resource
    private RedisTemplate<String, Integer> redisTemplate;

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


    /**
     * 执行扣减库存业务
     *
     * @param goodsKey 商品唯一标识
//     * @param expire  库存过期时间
     * @param num     扣减库存的数量
     * @return 返回扣减库存后剩余库存数量
     */
    @Override
    public long updateStock(String goodsKey, int num) {
//    public long getStock(String batchNo, long expire, int num) {
        // 商品库存唯一标识
        final String key = REDIS_KEY_STOCK + goodsKey;

        /*
         * 从redis中获取key对应的过期时间;
         * 1、如果该值有过期时间，就返回相应的过期时间;
         * 2、如果该值没有设置过期时间，就返回-1;
         * 3、如果没有该值，就返回-2;
         *
         * 注意：这里为了方便模拟，实际线上。通过缓存预热的方式通过DB查询实际的库存数据
         * 添加到Redis中
         */
//        Long expire1 = redisTemplate.opsForValue().getOperations().getExpire(key);
//        if (Objects.equals(EXIST_FLAG, expire1)) {
//            redisTemplate.opsForValue().set(key, 100, expire, TimeUnit.SECONDS);
//            System.out.println("Redis无初始库存，设置库存数据 = " + expire1);
//        }

//        // 初始化商品库存
//        Integer stock = redisTemplate.opsForValue().get(key);

        // 设置分布式锁
        final RLock rLock = redissonClient.getLock(REDIS_KEY_STOCK +goodsKey+ ":LOCK");
        try {
            //尝试加锁，最多等待5秒，上锁以后3秒自动解锁
            if (rLock.tryLock(5, 3,TimeUnit.SECONDS)) {
                Integer stock = redisTemplate.opsForValue().get(key);
                log.info("--- 当前Key:[{}]加锁成功，当前最新库存:{}---"+ stock);
                // 调一次扣库存的操作

                Long stock1 = stock(key, num);
                log.info("--- stock1 ="+stock1);
                //库存未初始化
                if(stock1==UNINITIALIZED_STOCK){

                }
//                stock = redisTemplate.opsForValue().get(key);
//                int batchNoLock = Objects.requireNonNull(stock);
                log.info("--- 当前剩余库存"+stock1);
                return stock1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rLock != null && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        return UNINITIALIZED_STOCK;
    }

    /**
     * 执行扣减库存业务
     *
     * @param goodsKey 库存唯一标识
    //     * @param expire  库存过期时间
     * @param num     扣减库存的数量
     * @return 返回扣减库存后剩余库存数量
     */
    @Override
    public long cutStockWithPropertys(String goodsKey,String inventoryKey, int num) {
//    public long getStock(String batchNo, long expire, int num) {
        // 商品库存唯一标识
        final String key = REDIS_KEY_STOCK + goodsKey;

        /*
         * 从redis中获取key对应的过期时间;
         * 1、如果该值有过期时间，就返回相应的过期时间;
         * 2、如果该值没有设置过期时间，就返回-1;
         * 3、如果没有该值，就返回-2;
         *
         * 注意：这里为了方便模拟，实际线上。通过缓存预热的方式通过DB查询实际的库存数据
         * 添加到Redis中
         */
//        Long expire1 = redisTemplate.opsForValue().getOperations().getExpire(key);
//        if (Objects.equals(EXIST_FLAG, expire1)) {
//            redisTemplate.opsForValue().set(key, 100, expire, TimeUnit.SECONDS);
//            System.out.println("Redis无初始库存，设置库存数据 = " + expire1);
//        }

//        // 初始化商品库存
//        Integer stock = redisTemplate.opsForValue().get(key);

        // 设置分布式锁
        final RLock rLock = redissonClient.getLock(REDIS_KEY_STOCK + goodsKey + ":LOCK");
        try {
            //尝试加锁，最多等待5秒，上锁以后3秒自动解锁
            if (rLock.tryLock(5, 3,TimeUnit.SECONDS)) {
                Integer stock = redisTemplate.opsForValue().get(key);
                log.info("--- 当前Key:[{}]加锁成功，当前最新库存:{}---"+ stock);
                // 先扣除商品规格库存
                Long stock1 = stock(REDIS_KEY_STOCK + inventoryKey, num);
                log.info("--- stock1 ="+stock1);
                //规格库存扣除成功，继续扣除商品总库存
                if(stock1>=0){
                    stock1 = stock(key, num);
                    if(stock1<0){
                        //扣减商品总库存失败，返前一步的规格库存
                        redisTemplate.opsForValue().increment(REDIS_KEY_STOCK + inventoryKey, num);
                    }
                }
                log.info("--- 扣库存后stock1 ="+stock1);
//                stock = redisTemplate.opsForValue().get(key);
//                int batchNoLock = Objects.requireNonNull(stock);
                return stock1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rLock != null && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        return UNINITIALIZED_STOCK;
    }

    /**
     * 执行加库存业务
     *
     * @param goodsKey 商品唯一标识
     * @param num     加库存的数量
     * @return 返回剩余库存数量
     */
    @Override
    public long addStock(String goodsKey, int num) {
        // 商品库存唯一标识
        final String key = REDIS_KEY_STOCK + goodsKey;

        // 设置分布式锁
        final RLock rLock = redissonClient.getLock(REDIS_KEY_STOCK +goodsKey+ ":LOCK");
        try {
            //尝试加锁，最多等待5秒，上锁以后3秒自动解锁
            if (rLock.tryLock(5, 3,TimeUnit.SECONDS)) {
                Integer stock = redisTemplate.opsForValue().get(key);
                log.info("--- 当前Key:[{}]加锁成功，当前最新库存:{}---"+ stock);
                // 加库存
                Long stock1 =redisTemplate.opsForValue().increment(key, num);
                log.info("--- 加库存后stock1 ="+stock1);
                return stock1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rLock != null && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        return UNINITIALIZED_STOCK;
    }

    /**
     * 执行加库存业务
     *
     * @param goodsKey 商品唯一标识
     * @param num     加库存的数量
     * @return 返回剩余库存数量
     */
    @Override
    public long addStockWithPropertys(String goodsKey,String inventoryKey, int num) {
        // 商品库存唯一标识
        final String key = REDIS_KEY_STOCK + goodsKey;

        // 设置分布式锁
        final RLock rLock = redissonClient.getLock(key+ ":LOCK");
        try {
            //尝试加锁，最多等待5秒，上锁以后3秒自动解锁
            if (rLock.tryLock(5, 3,TimeUnit.SECONDS)) {
                Integer stock = redisTemplate.opsForValue().get(key);
                log.info("--- 当前Key:[{}]加锁成功，当前最新库存:{}---"+ stock);
                // 加库存
                long stock1 = redisTemplate.opsForValue().increment(REDIS_KEY_STOCK + goodsKey, num);
                if(stock1>0){
                    return redisTemplate.opsForValue().increment(REDIS_KEY_STOCK + inventoryKey, num);
                }
                return stock1;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (rLock != null && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        return UNINITIALIZED_STOCK;
    }

    /**
     * 扣库存这步特别注意，分布式连接有问题，需要依赖包里，去掉lettuce组件
     * 初始化库存数量，这个可以从DB里取实际的量
     *
     * @param key 库存key
     * @param num 扣减库存数量
     * @return 扣减之后剩余的库存【-3:库存未初始化; -2:库存不足; -1:不限库存; 大于等于0:扣减库存之后的剩余库存】
     */
    private Long stock(String key, int num) {
        // 脚本里的KEYS参数
        List<String> keys = new ArrayList<>();
        keys.add(key);

        // 脚本里的ARGV参数
        List<String> argvList = new ArrayList<>();
        argvList.add(Integer.toString(num));

        // 执行扣减库存LUA脚本
        return redisTemplate.execute((RedisCallback<Long>) connection -> {
            Object nativeConnection = connection.getNativeConnection();
            // 集群模式
            if (nativeConnection instanceof JedisCluster) {
                return (Long) ((JedisCluster) nativeConnection).eval(STOCK_LUA, keys, argvList);
            }
            // 单机模式
            else if (nativeConnection instanceof Jedis) {
                return (Long) ((Jedis) nativeConnection).eval(STOCK_LUA, keys, argvList);
            }
            return UNINITIALIZED_STOCK;
        });
    }

    /**
     *
     * 判断缓存服务器是否有该商品库存
     *
     * @param batchNo 业务ID
     * @return 判断结果
     */
    @Override
    public boolean checkGoods(String batchNo) {
        String key = REDIS_KEY_STOCK + batchNo;
        Long expire1 = redisTemplate.opsForValue().getOperations().getExpire(key);
        if (Objects.equals(EXIST_FLAG, expire1)) {
            return false;
        }
        return true;
    }

    /**
     *
     * 获取当前库存量
     *
     * @param batchNo 业务ID
     * @return 初始库存
     */
    @Override
    public int currentStock(String batchNo) {
        String key = REDIS_KEY_STOCK + batchNo;
        return redisTemplate.opsForValue().get(key);
    }

    /**
     *
     * 初始化库存数量，这个可以从DB里取实际的量
     *
     * @param batchNo 业务ID
     * @return 初始库存
     */
    @Override
    public void initStock(String batchNo,int num) {
        String key = REDIS_KEY_STOCK + batchNo;
        redisTemplate.opsForValue().set(key,num);
    }

}