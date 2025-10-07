package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.FUserloginToken;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-06-15
 **/
@Repository
@Mapper
public interface FUserloginTokenDao {

    /**
     * 新增
     */
    int insert(FUserloginToken entity);

    /**
     * 更新
     */
    int update(FUserloginToken entity);

    /**
     * 删除
     */
    int delete(FUserloginToken entity);

    /**
     * 查询详情
     */
    FUserloginToken queryDetail(FUserloginToken entity);

    /**
     * 查询列表
     */
    List<FUserloginToken> queryList(HashMap<String, Object> reqMap);

}
