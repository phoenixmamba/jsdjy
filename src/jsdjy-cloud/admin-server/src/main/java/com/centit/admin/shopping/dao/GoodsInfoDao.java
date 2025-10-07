package com.centit.admin.shopping.dao;

import com.centit.admin.shopping.po.GoodsInfoPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-21
 **/
@Repository
@Mapper
public interface GoodsInfoDao {

    /**
     * 新增
     */
    int insert(GoodsInfoPo entity);

    /**
     * 更新
     */
    int update(GoodsInfoPo entity);

    /**
     * 查询详情
     */
    GoodsInfoPo queryDetail(GoodsInfoPo entity);

    /**
     * 查询列表
     */
    List<GoodsInfoPo> queryStoreList(HashMap<String, Object> reqMap);

    /**
     * 查询列表
     */
    List<GoodsInfoPo> queryClassGoodsList(HashMap<String, Object> reqMap);

    /**
     * 查询列表数量
     */
    int queryClassGoodsTotalCount(HashMap<String, Object> reqMap);

    /**
     * 更新商品库存
     */
    int updateGoodsInventory(GoodsInfoPo entity);


    Boolean checkGoodsName(HashMap<String, Object> reqMap);

    int updateStatus(GoodsInfoPo entity);

    /**
     * 查询商品库存
     * @param id 商品id
     * @return 库存值
     */
    int selectGoodsStock(String id);

    /**
     * 减少商品库存
     * @param goodsId 商品id
     * @param cutStock 库存减少量
     * @return
     */
    int cutGoodsStock(@Param("goodsId") String goodsId, @Param("cutStock") int cutStock);

    /**
     * 添加商品库存
     * @param goodsId 商品id
     * @param addStock 库存添加量
     * @return
     */
    int addGoodsStock(@Param("goodsId") String goodsId, @Param("addStock") int addStock);
}
