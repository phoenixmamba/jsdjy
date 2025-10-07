package com.centit.admin.shopping.dao;

import com.centit.admin.shopping.po.GoodsSpecInventoryPo;
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
 * @Date : 2021-03-10
 **/
@Repository
@Mapper
public interface GoodsSpecInventoryDao {

    /**
     * 新增
     */
    int insert(GoodsSpecInventoryPo entity);

    /**
     * 更新
     */
    int update(GoodsSpecInventoryPo entity);

    /**
     * 删除
     */
    int delete(GoodsSpecInventoryPo entity);

    /**
     * 查询详情
     */
    GoodsSpecInventoryPo queryDetail(GoodsSpecInventoryPo entity);

    /**
     * 查询列表
     */
    List<GoodsSpecInventoryPo> queryList(HashMap<String, Object> reqMap);

    int selectInventoryStock(@Param("goodsId") String goodsId, @Param("propertys") String propertys);

    int batchInsertGoodsInventorys(List<GoodsSpecInventoryPo> goodsSpecInventoryPoList);

    int cutSpecStock(@Param("goodsId") String goodsId, @Param("propertys") String propertys, @Param("cutStock") int cutStock);

    int addSpecStock(@Param("goodsId") String goodsId, @Param("propertys") String propertys, @Param("addStock") int addStock);
}
