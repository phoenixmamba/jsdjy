package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingSysconfig;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-21
 **/
@Repository
@Mapper
public interface ShoppingSysconfigDao {

    /**
     * 新增
     */
    int insert(ShoppingSysconfig entity);

    /**
     * 更新
     */
    int update(ShoppingSysconfig entity);

    /**
     * 删除
     */
    int delete(ShoppingSysconfig entity);

    /**
     * 查询详情
     */
    ShoppingSysconfig queryDetail(ShoppingSysconfig entity);

    /**
     * 查询列表
     */
    List<ShoppingSysconfig> queryList(HashMap<String, Object> reqMap);

}
