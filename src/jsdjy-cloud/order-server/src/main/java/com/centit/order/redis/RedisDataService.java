package com.centit.order.redis;

/**
 * @author cui_jian
 */
public interface RedisDataService {
    /**
     * 删除key
     * @param key
     * @return 删除的条数
     */
    long deleteKey(String key);

    /**
     * 设置整型数据
     * @param key key
     * @param num num
     */
    void setInt(String key, int num);

    /**
     * 设置整型数据，key不存在时设置成功，key已存在则不会覆盖原有值并返回失败
     * @param key key
     * @param num num
     * @param expireMinutes 失效时间
     * @return true:成功，false:失败
     */
    boolean setNewInt(String key,int num,long expireMinutes);

    /**
     * 扣减库存
     * @param key 库存key
     * @param num 扣减库存数量
     * @return 扣减之后剩余的库存【-3:库存未初始化; -2:库存不足; -1:不限库存; 大于等于0:扣减库存之后的剩余库存】
     */
    long cutStock(String key, int num);
}
