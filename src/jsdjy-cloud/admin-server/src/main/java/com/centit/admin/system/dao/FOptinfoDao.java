package com.centit.admin.system.dao;

import com.centit.admin.system.po.FOptinfo;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-03
 **/
@Repository
@Mapper
public interface FOptinfoDao {

    /**
     * 新增
     */
    int insert(FOptinfo entity);

    /**
     * 更新
     */
    int update(FOptinfo entity);

    /**
     * 删除
     */
    int delete(FOptinfo entity);

    /**
     * 查询详情
     */
    FOptinfo queryDetail(FOptinfo entity);

    /**
     * 查询列表
     */
    List<FOptinfo> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询用户权限菜单
     */
    List<FOptinfo> queryUserOpt(HashMap<String, Object> reqMap);

    /**
     * 查询上级父级所有
     */
    List<FOptinfo> queryUpOpt(HashMap<String, Object> reqMap);

}
