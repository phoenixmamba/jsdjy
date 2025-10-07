package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingAssetRecord;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-01-13
 **/
@Repository
@Mapper
public interface ShoppingAssetRecordDao {

    /**
     * 新增
     */
    int insert(ShoppingAssetRecord entity);

    /**
     * 更新
     */
    int update(ShoppingAssetRecord entity);

    /**
     * 删除
     */
    int delete(ShoppingAssetRecord entity);

    /**
     * 查询详情
     */
    ShoppingAssetRecord queryDetail(ShoppingAssetRecord entity);

    /**
     * 查询列表
     */
    List<ShoppingAssetRecord> queryList(HashMap<String, Object> reqMap);

}
