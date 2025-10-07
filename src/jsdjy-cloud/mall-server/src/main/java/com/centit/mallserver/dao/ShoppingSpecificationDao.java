package com.centit.mallserver.dao;

import com.centit.mallserver.po.ShoppingSpecificationPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-22
 **/
@Repository
@Mapper
public interface ShoppingSpecificationDao {


    /**
     * 查询列表
     */
    List<ShoppingSpecificationPo> queryList(String goodsId);

    String selectPropertysValue(@Param("ids") List<String> ids);

}
