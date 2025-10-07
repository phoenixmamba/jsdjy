package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtplanInfo;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>活动和报名信息关联表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-12-14
 **/
@Repository
@Mapper
public interface ShoppingArtplanInfoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtplanInfo entity);

    /**
     * 更新
     */
    int update(ShoppingArtplanInfo entity);

    /**
     * 删除
     */
    int delete(ShoppingArtplanInfo entity);

    /**
     * 查询详情
     */
    ShoppingArtplanInfo queryDetail(ShoppingArtplanInfo entity);

    /**
     * 查询列表
     */
    List<ShoppingArtplanInfo> queryList(HashMap<String, Object> reqMap);

    int deleteByArtplanId(ShoppingArtplanInfo entity);

}
