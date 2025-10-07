package com.centit.shopping.dao;

import com.centit.shopping.po.TJpushInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p>极光推送历史记录<p>
 *
 * @version : 1.0
 * @Author : lihao
 * @Description : Dao接口
 * @Date : 2021-01-19
 **/
@Repository
@Mapper
public interface TJpushInfoDao {

    /**
     * 新增
     */
    int insert(TJpushInfo entity);

    /**
     * 更新
     */
    int update(TJpushInfo entity);

    /**
     * 删除
     */
    int delete(TJpushInfo entity);

    /**
     * 查询详情
     */
    TJpushInfo queryDetail(TJpushInfo entity);

    /**
     * 查询列表
     */
    List<TJpushInfo> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表
     */
    List<TJpushInfo> queryPageList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表数量
     */
    int queryPageListCount(HashMap<String, Object> reqMap);

}
