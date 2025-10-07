package com.centit.mallserver.dao;

import com.centit.mallserver.po.GoodsSpecInventoryPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
     * 查询列表
     */
    List<GoodsSpecInventoryPo> selectGoodsInventorys(String goodsId);

    int selectInventoryStock(@Param("goodsId") String goodsId, @Param("propertys") String propertys);

    BigDecimal selectInventoryPrice(@Param("goodsId") String goodsId, @Param("propertys") String propertys);
}
