package com.centit.shopping.dao;

import com.centit.shopping.po.TEarlywarning;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-06-28
 **/
@Repository
@Mapper
public interface TEarlywarningDao {

    /**
     * 新增
     */
    int insert(TEarlywarning entity);

    /**
     * 更新
     */
    int update(TEarlywarning entity);

    /**
     * 删除
     */
    int delete(TEarlywarning entity);

    /**
     * 查询详情
     */
    TEarlywarning queryDetail(TEarlywarning entity);

    /**
     * 查询列表
     */
    List<TEarlywarning> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

}
