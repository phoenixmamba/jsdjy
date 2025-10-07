package com.centit.shopping.dao;

import com.centit.shopping.po.TInterfacecheckMsgLog;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-08-02
 **/
@Repository
@Mapper
public interface TInterfacecheckMsgLogDao {

    /**
     * 新增
     */
    int insert(TInterfacecheckMsgLog entity);

    /**
     * 更新
     */
    int update(TInterfacecheckMsgLog entity);

    /**
     * 删除
     */
    int delete(TInterfacecheckMsgLog entity);

    /**
     * 查询详情
     */
    TInterfacecheckMsgLog queryDetail(TInterfacecheckMsgLog entity);

    /**
     * 查询列表
     */
    List<TInterfacecheckMsgLog> queryList(HashMap<String, Object> reqMap);

    List<TInterfacecheckMsgLog> queryInHourData(HashMap<String, Object> reqMap);
}
