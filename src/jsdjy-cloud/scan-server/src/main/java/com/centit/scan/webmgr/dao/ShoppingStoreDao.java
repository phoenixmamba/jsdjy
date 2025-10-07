package com.centit.scan.webmgr.dao;

import com.centit.scan.webmgr.po.ShoppingStore;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-23
 **/
@Repository
@Mapper
public interface ShoppingStoreDao {

    /**
     * 新增
     */
    int insert(ShoppingStore entity);

    /**
     * 更新
     */
    int update(ShoppingStore entity);

    /**
     * 删除
     */
    int delete(ShoppingStore entity);

    /**
     * 查询详情
     */
    ShoppingStore queryDetail(ShoppingStore entity);

    /**
     * 查询列表
     */
    List<ShoppingStore> queryList(HashMap<String, Object> reqMap);

}
