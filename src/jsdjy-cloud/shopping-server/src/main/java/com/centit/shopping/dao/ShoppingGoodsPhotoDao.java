package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodsPhoto;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-22
 **/
@Repository
@Mapper
public interface ShoppingGoodsPhotoDao {

    /**
     * 新增
     */
    int insert(ShoppingGoodsPhoto entity);

    /**
     * 更新
     */
    int update(ShoppingGoodsPhoto entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodsPhoto entity);

    /**
     * 查询详情
     */
    ShoppingGoodsPhoto queryDetail(ShoppingGoodsPhoto entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodsPhoto> queryList(HashMap<String, Object> reqMap);

}
