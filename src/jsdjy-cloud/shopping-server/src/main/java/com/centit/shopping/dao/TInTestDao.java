package com.centit.shopping.dao;

import com.centit.shopping.po.TInTest;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-07-19
 **/
@Repository
@Mapper
public interface TInTestDao {

    /**
     * 新增
     */
    int insert(TInTest entity);

    /**
     * 更新
     */
    int update(TInTest entity);

    /**
     * 删除
     */
    int delete(TInTest entity);

    /**
     * 查询详情
     */
    TInTest queryDetail(TInTest entity);

    /**
     * 查询列表
     */
    List<TInTest> queryList(HashMap<String, Object> reqMap);

}
