package com.centit.core.consts;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/5 9:42
 **/
public class RedisConst {
    /**
     * Redis获取锁等待时间，单位：秒
     */
    public static final int LOCK_WAIT_SECONDS = 5;

    /**
     * Redis锁自动释放时间，单位：秒
     */
    public static final int LOCK_LEASE_SECONDS = 3;

    /**
     * 商品库存Redis缓存Key前缀
     */
    public static final String KEY_STOCK_GOODS = "STOCK:GOODS:";

    /**
     * 商品库存Redis失效时间，单位：分钟
     */
    public static final long EXPIRE_MINUTES_STOCK_GOODS = 10L;

    /**
     * 商品信息Redis缓存Key前缀
     */
    public static final String KEY_INFO_GOODS = "INFO:GOODS:";

    /**
     * 商品信息Redis失效时间，单位：分钟
     */
    public static final long EXPIRE_MINUTES_INFO_GOODS = 10L;

    /**
     * 商城浏览历史记录Redis缓存Key前缀
     */
    public static final String KEY_HISTORY_MALL = "HISTORY:MALL:";

    /**
     * 防止重复下单校验key前缀
     */
    public static final String KEY_ORDER_RENDER = "ORDER:TEMP_ID:";
    /**
     * 防止重复下单校验key失效时间，单位：分钟
     */
    public static final long EXPIRE_MINUTES_ORDER_RENDER = 3L;

    /**
     * 防止消息重复消费校验key前缀
     */
    public static final String KEY_CONSUMER_ORDER_ADD = "CONSUMER:ORDER_ADD:";
    /**
     * 防止消息重复消费校验key失效时间
     */
    public static final long EXPIRE_MINUTES_CONSUMER_ORDER_ADD = 5L;

    /**
     * 扣减库存时脚本返回标识库存未初始化的值
     */
    public static final long LUA_RES_STOCK_UNSET = -3L;
}
