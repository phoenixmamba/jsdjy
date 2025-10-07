package com.centit.shopping.biz.utils;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2023/8/31 11:15
 **/
public interface DBService {
    /**
     * 数据库扣减商城商品库存
     * @param goodsId 商品id
     * @param propertys 规格
     * @param cutCount 扣减数量
     */
    void cutDBGoodsInventory(String goodsId, String propertys, int cutCount);
    /**
     * 数据库增加商城商品库存
     * @param goodsId  商品id
     * @param propertys  规格
     * @param addCount  增加数量
     */
    void addDBGoodsInventory(String goodsId, String propertys, int addCount);
    /**
     * 数据库扣减会员活动库存
     * @param actId  活动Id
     * @param propertys  规格
     * @param cutCount  扣减数量
     */
    void cutDBActInventory(String actId, String propertys, int cutCount);
    /**
     * 数据库增加商城商品库存
     * @param actId  活动Id
     * @param propertys  规格
     * @param addCount  增加数量
     */
    void addDBActInventory(String actId, String propertys, int addCount);
    /**
     * 数据库扣减爱艺计划库存
     * @param actId  活动Id
     * @param propertys  规格
     * @param cutCount  扣减数量
     */
    void cutDBPlanInventory(String actId, String propertys, int cutCount);
    /**
     * 数据库增加爱艺计划库存
     * @param actId  活动id
     * @param propertys  规格
     * @param addCount  增加数量
     */
    void addDBPlanInventory(String actId, String propertys, int addCount);
}
