package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodstype;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-01
 **/
@Repository
@Mapper
public interface ShoppingGoodstypeDao {

    /**
     * 新增
     */
    int insert(ShoppingGoodstype entity);

    /**
     * 更新
     */
    int update(ShoppingGoodstype entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodstype entity);

    /**
     * 查询详情
     */
    ShoppingGoodstype queryDetail(ShoppingGoodstype entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodstype> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询数量
     */
    int queryTotalCount(HashMap<String, Object> reqMap);
}
