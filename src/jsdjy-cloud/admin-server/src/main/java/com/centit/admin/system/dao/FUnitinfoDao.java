package com.centit.admin.system.dao;

import com.centit.admin.system.po.FUnitinfo;
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
public interface FUnitinfoDao {

    /**
     * 新增
     */
    int insert(FUnitinfo entity);

    /**
     * 更新
     */
    int update(FUnitinfo entity);

    /**
     * 删除
     */
    int delete(FUnitinfo entity);

    /**
     * 查询详情
     */
    FUnitinfo queryDetail(FUnitinfo entity);

    /**
     * 查询列表
     */
    List<FUnitinfo> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询下级部门数量
     */
    int queryChildUnitCount(FUnitinfo entity);

}
