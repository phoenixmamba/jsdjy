package com.centit.shopping.dao;

import com.centit.shopping.po.TInvoiceHeader;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>发票抬头<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-11-24
 **/
@Repository
@Mapper
public interface TInvoiceHeaderDao {

    /**
     * 新增
     */
    int insert(TInvoiceHeader entity);

    /**
     * 更新
     */
    int update(TInvoiceHeader entity);

    /**
     * 删除
     */
    int delete(TInvoiceHeader entity);

    /**
     * 查询详情
     */
    TInvoiceHeader queryDetail(TInvoiceHeader entity);

    /**
     * 查询列表
     */
    List<TInvoiceHeader> queryList(HashMap<String, Object> reqMap);

    /**
     * 将其它的抬头设置为非默认
     */
    int setOtherHeadersNotDefault(TInvoiceHeader entity);
}
