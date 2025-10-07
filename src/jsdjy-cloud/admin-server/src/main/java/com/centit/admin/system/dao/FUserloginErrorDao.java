package com.centit.admin.system.dao;

import com.centit.admin.system.po.FUserloginError;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-08-04
 **/
@Repository
@Mapper
public interface FUserloginErrorDao {

    /**
     * 新增
     */
    int insert(FUserloginError entity);

    /**
     * 更新
     */
    int update(FUserloginError entity);

    /**
     * 删除
     */
    int delete(FUserloginError entity);

    /**
     * 查询详情
     */
    FUserloginError queryDetail(FUserloginError entity);

    /**
     * 查询列表
     */
    List<FUserloginError> queryList(HashMap<String, Object> reqMap);

    int selectUserErrorCount(String userCode);

}
