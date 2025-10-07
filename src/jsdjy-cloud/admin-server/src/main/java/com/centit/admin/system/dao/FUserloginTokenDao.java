package com.centit.admin.system.dao;

import com.centit.admin.system.po.FUserloginToken;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-03
 **/
@Repository
@Mapper
public interface FUserloginTokenDao {

    /**
     * 新增
     */
    int insert(FUserloginToken entity);

    int insertOnDuplicateKey(FUserloginToken entity);

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
    FUserloginToken queryDetail(String userCode);

    /**
     * 查询列表
     */
    List<FUserloginToken> queryList(HashMap<String, Object> reqMap);

    /**
     * 注销登录
     */
    int logout(FUserloginToken entity);
}
