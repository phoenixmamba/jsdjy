package com.centit.admin.redis;

/**
 * @author cui_jian
 */
public interface RedisDataService {
    /**
     *
     * 获取当前库存量
     *
     * @param key 库存key
     * @return int 库存
     */
    Integer currentStock(String key);

    /**
     * 设置库存
     * @param key 库存key
     * @param num 库存量
     * @param expireMinutes 过期时间
     */
    void setStock(String key,int num,long expireMinutes);

    /**
     * 扣减库存
     * @param key 库存key
     * @param num 扣减库存数量
     * @return 扣减之后剩余的库存【-3:库存未初始化; -2:库存不足; -1:不限库存; 大于等于0:扣减库存之后的剩余库存】
     */
    Integer cutStock(String key, int num);

    /**
     * 增加库存
     * @param batchNo 库存key
     * @param num 增加库存数量
     */
    void addStock(String batchNo,int num);

    /**
     * 删除redis中保存的信息
     * @param key 信息key
     */
    void deleteKey(String key);
}
