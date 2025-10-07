package com.centit.file.webmgr.dao;

import com.centit.file.webmgr.po.ShoppingAccessory;
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
public interface ShoppingAccessoryDao {

    /**
     * 新增
     */
    int insert(ShoppingAccessory entity);

    /**
     * 更新
     */
    int update(ShoppingAccessory entity);

    /**
     * 删除
     */
    int delete(ShoppingAccessory entity);

    /**
     * 查询详情
     */
    ShoppingAccessory queryDetail(ShoppingAccessory entity);

    /**
     * 查询列表
     */
    List<ShoppingAccessory> queryList(HashMap<String, Object> reqMap);

}
