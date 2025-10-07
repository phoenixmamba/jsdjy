package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodstypeSpec;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-23
 **/
@Repository
@Mapper
public interface ShoppingGoodstypeSpecDao {

    /**
     * 新增
     */
    int insert(ShoppingGoodstypeSpec entity);

    /**
     * 更新
     */
    int update(ShoppingGoodstypeSpec entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodstypeSpec entity);

    /**
     * 查询详情
     */
    ShoppingGoodstypeSpec queryDetail(ShoppingGoodstypeSpec entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodstypeSpec> queryList(HashMap<String, Object> reqMap);

}
