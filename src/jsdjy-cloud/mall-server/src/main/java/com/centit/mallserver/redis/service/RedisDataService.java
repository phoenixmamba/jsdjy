package com.centit.mallserver.redis.service;

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
     * 设置带过期时间的整型数据
     * @param key key
     * @param num num
     * @param expireMinutes 过期时间
     */
    void setIntWithExpireTime(String key, int num, long expireMinutes);

    /**
     * 设置带过期时间的字符串型数据
     * @param key key
     * @param str str
     * @param expireMinutes 过期时间
     */
    void setStringWithExpireTime(String key, String str, long expireMinutes);

    /**
     * 查询信息
     * @param key 业务key
     * @return
     */
    String queryStringInfo(String key);

    /**
     *
     * 获取redis数据
     *
     * @param key 业务key
     * @return int
     */
    Integer queryInt(String key);

    /**
     * 保存SortedSet数据
     * @param key SortedSet的key
     * @param value 值
     * @param score 排序值
     */
    void addSortedSetValue(String key,String value,long score);

}
