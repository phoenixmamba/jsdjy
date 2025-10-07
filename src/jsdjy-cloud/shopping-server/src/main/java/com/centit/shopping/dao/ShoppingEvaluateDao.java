package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingEvaluate;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-16
 **/
@Repository
@Mapper
public interface ShoppingEvaluateDao {

    /**
     * 新增
     */
    int insert(ShoppingEvaluate entity);

    /**
     * 更新
     */
    int update(ShoppingEvaluate entity);

    /**
     * 删除
     */
    int delete(ShoppingEvaluate entity);

    /**
     * 查询详情
     */
    ShoppingEvaluate queryDetail(ShoppingEvaluate entity);

    /**
     * 获取最新评论
     */
    ShoppingEvaluate queryLastEvaluate(HashMap<String, Object> reqMap);

    /**
     * 查询列表
     */
    List<ShoppingEvaluate> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询数量
     */
    int queryTotalCount(HashMap<String, Object> reqMap);

}
