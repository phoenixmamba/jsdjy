package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.FUserinfo;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-01-06
 **/
@Repository
@Mapper
public interface FUserinfoDao {

    /**
     * 新增
     */
    int insert(FUserinfo entity);

    /**
     * 更新
     */
    int update(FUserinfo entity);

    /**
     * 删除
     */
    int delete(FUserinfo entity);

    /**
     * 查询详情
     */
    FUserinfo queryDetail(FUserinfo entity);

    /**
     * 查询列表
     */
    List<FUserinfo> queryList(HashMap<String, Object> reqMap);

}
