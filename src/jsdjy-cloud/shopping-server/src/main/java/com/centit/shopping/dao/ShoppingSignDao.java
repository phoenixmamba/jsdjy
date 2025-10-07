package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingSign;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-07-01
 **/
@Repository
@Mapper
public interface ShoppingSignDao {

    /**
     * 新增
     */
    int insert(ShoppingSign entity);

    /**
     * 更新
     */
    int update(ShoppingSign entity);

    /**
     * 删除
     */
    int delete(ShoppingSign entity);

    /**
     * 查询详情
     */
    ShoppingSign queryDetail(ShoppingSign entity);

    /**
     * 查询列表
     */
    List<ShoppingSign> queryList(HashMap<String, Object> reqMap);

}
