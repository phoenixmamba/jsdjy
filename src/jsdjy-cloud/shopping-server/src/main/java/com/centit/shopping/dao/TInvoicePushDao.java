package com.centit.shopping.dao;

import com.centit.shopping.po.TInvoicePush;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-11-24
 **/
@Repository
@Mapper
public interface TInvoicePushDao {

    /**
     * 新增
     */
    int insert(TInvoicePush entity);

    /**
     * 更新
     */
    int update(TInvoicePush entity);

    /**
     * 删除
     */
    int delete(TInvoicePush entity);

    /**
     * 查询详情
     */
    TInvoicePush queryDetail(TInvoicePush entity);

    /**
     * 查询列表
     */
    List<TInvoicePush> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询指定用户推送记录
     */
    List<TInvoicePush> queryUserPushList(HashMap<String, Object> reqMap);

}
