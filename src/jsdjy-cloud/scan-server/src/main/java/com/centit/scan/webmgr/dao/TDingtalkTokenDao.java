package com.centit.scan.webmgr.dao;

import com.centit.scan.webmgr.po.TDingtalkToken;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-12-09
 **/
@Repository
@Mapper
public interface TDingtalkTokenDao {

    /**
     * 新增
     */
    int insert(TDingtalkToken entity);

    /**
     * 更新
     */
    int update(TDingtalkToken entity);

    /**
     * 删除
     */
    int delete(TDingtalkToken entity);

    /**
     * 查询详情
     */
    TDingtalkToken queryDetail(TDingtalkToken entity);

    /**
     * 查询列表
     */
    List<TDingtalkToken> queryList(HashMap<String, Object> reqMap);

}
