package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.TExportFile;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2022-08-08
 **/
@Repository
@Mapper
public interface TExportFileDao {

    /**
     * 新增
     */
    int insert(TExportFile entity);

    /**
     * 更新
     */
    int update(TExportFile entity);

    /**
     * 删除
     */
    int delete(TExportFile entity);

    /**
     * 查询详情
     */
    TExportFile queryDetail(TExportFile entity);

    /**
     * 查询列表
     */
    List<TExportFile> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

}
