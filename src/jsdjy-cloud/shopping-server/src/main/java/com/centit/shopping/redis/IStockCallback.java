package com.centit.shopping.redis;

public interface IStockCallback {
    /**
     * 扣减Redis库存
     *
     * @param goodsKey 商品唯一编号
//     * @param expire  过期时间
     * @param num     扣减库存数量
     * @return 剩余库存数量
     */
    long updateStock(String goodsKey, int num);
    long cutStockWithPropertys(String goodsKey,String inventoryKey, int num);

    long addStock(String goodsKey, int num);
    long addStockWithPropertys(String goodsKey,String inventoryKey, int num);
    /**
     * 初始化库存
     *
     * @param batchNo 业务
     * @return 库存
     */
    void initStock(String batchNo,int num);

    int currentStock(String batchNo);

    boolean checkGoods(String batchNo);
}
