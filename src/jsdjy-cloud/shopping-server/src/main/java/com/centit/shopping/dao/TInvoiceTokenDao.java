package com.centit.shopping.dao;

import com.centit.shopping.po.TInvoiceToken;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-11-26
 **/
@Repository
@Mapper
public interface TInvoiceTokenDao {

    /**
     * 新增
     */
    int insert(TInvoiceToken entity);

    /**
     * 更新
     */
    int update(TInvoiceToken entity);

    /**
     * 删除
     */
    int delete(TInvoiceToken entity);

    /**
     * 查询详情
     */
    TInvoiceToken queryDetail(TInvoiceToken entity);

    /**
     * 查询列表
     */
    List<TInvoiceToken> queryList(HashMap<String, Object> reqMap);

}
