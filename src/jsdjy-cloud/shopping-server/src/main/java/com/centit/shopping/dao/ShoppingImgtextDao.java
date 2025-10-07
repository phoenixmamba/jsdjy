package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingImgtext;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-08-17
 **/
@Repository
@Mapper
public interface ShoppingImgtextDao {

    /**
     * 新增
     */
    int insert(ShoppingImgtext entity);

    /**
     * 更新
     */
    int update(ShoppingImgtext entity);

    /**
     * 删除
     */
    int delete(ShoppingImgtext entity);

    /**
     * 查询详情
     */
    ShoppingImgtext queryDetail(ShoppingImgtext entity);

    /**
     * 查询列表
     */
    List<ShoppingImgtext> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

//    List<ShoppingImgtext> queryExistCode(HashMap<String, Object> reqMap);

}
