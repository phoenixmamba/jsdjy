package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingRefundPhoto;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-07
 **/
@Repository
@Mapper
public interface ShoppingRefundPhotoDao {

    /**
     * 新增
     */
    int insert(ShoppingRefundPhoto entity);

    /**
     * 更新
     */
    int update(ShoppingRefundPhoto entity);

    /**
     * 删除
     */
    int delete(ShoppingRefundPhoto entity);

    /**
     * 查询详情
     */
    ShoppingRefundPhoto queryDetail(ShoppingRefundPhoto entity);

    /**
     * 查询列表
     */
    List<ShoppingRefundPhoto> queryList(HashMap<String, Object> reqMap);

}
