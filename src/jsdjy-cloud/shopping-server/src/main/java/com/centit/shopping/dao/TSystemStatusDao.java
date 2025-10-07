package com.centit.shopping.dao;

import com.centit.shopping.po.TSystemStatus;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-06-02
 **/
@Repository
@Mapper
public interface TSystemStatusDao {

    /**
     * 新增
     */
    int insert(TSystemStatus entity);


    /**
     * 删除
     */
    int delete(TSystemStatus entity);


    /**
     * 查询列表
     */
    List<TSystemStatus> queryList(HashMap<String, Object> reqMap);

}
