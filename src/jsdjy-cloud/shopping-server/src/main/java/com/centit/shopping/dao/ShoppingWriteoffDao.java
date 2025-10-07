package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingWriteoff;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-05
 **/
@Repository
@Mapper
public interface ShoppingWriteoffDao {

    /**
     * 新增
     */
    int insert(ShoppingWriteoff entity);

    /**
     * 更新
     */
    int update(ShoppingWriteoff entity);

    /**
     * 删除
     */
    int delete(ShoppingWriteoff entity);

    /**
     * 查询详情
     */
    ShoppingWriteoff queryDetail(ShoppingWriteoff entity);

    /**
     * 查询列表
     */
    List<ShoppingWriteoff> queryList(HashMap<String, Object> reqMap);

    ShoppingWriteoff queryGoodsWriteoff(HashMap<String, Object> reqMap);

    ShoppingWriteoff queryActivityWriteoff(HashMap<String, Object> reqMap);

    List<HashMap<String, Object>> queryOrderWriteoff(String ofId);
}
