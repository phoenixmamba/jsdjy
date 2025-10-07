package com.centit.shopping.dao;

import com.centit.shopping.po.VSearch;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>VIEW<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-21
 **/
@Repository
@Mapper
public interface VSearchDao {

    /**
     * 新增
     */
    int insert(VSearch entity);

    /**
     * 更新
     */
    int update(VSearch entity);

    /**
     * 删除
     */
    int delete(VSearch entity);

    /**
     * 查询详情
     */
    VSearch queryDetail(VSearch entity);

    /**
     * 查询列表
     */
    List<VSearch> queryList(HashMap<String, Object> reqMap);


    int queryTotalCount(HashMap<String, Object> reqMap);

}
