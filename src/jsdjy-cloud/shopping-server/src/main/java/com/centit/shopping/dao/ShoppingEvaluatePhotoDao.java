package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingEvaluatePhoto;
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
public interface ShoppingEvaluatePhotoDao {

    /**
     * 新增
     */
    int insert(ShoppingEvaluatePhoto entity);

    /**
     * 更新
     */
    int update(ShoppingEvaluatePhoto entity);

    /**
     * 删除
     */
    int delete(ShoppingEvaluatePhoto entity);

    /**
     * 查询详情
     */
    ShoppingEvaluatePhoto queryDetail(ShoppingEvaluatePhoto entity);

    /**
     * 查询列表
     */
    List<ShoppingEvaluatePhoto> queryList(HashMap<String, Object> reqMap);

}
