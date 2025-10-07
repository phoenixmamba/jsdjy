package com.centit.mallserver.dao;

import com.centit.mallserver.po.ShoppingEvaluatePo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 获取最新评论
     */
    ShoppingEvaluatePo selectLastEvaluate(String goodsId);

    /**
     * 查询列表
     */
    List<ShoppingEvaluatePo> queryList(String goodsId);


}
