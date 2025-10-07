package com.centit.shopping.dao;

import com.centit.shopping.po.TInvoice;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-11-08
 **/
@Repository
@Mapper
public interface TInvoiceDao {

    /**
     * 新增
     */
    int insert(TInvoice entity);

    /**
     * 更新
     */
    int update(TInvoice entity);

    /**
     * 删除
     */
    int delete(TInvoice entity);

    /**
     * 查询详情
     */
    TInvoice queryDetail(TInvoice entity);

    /**
     * 查询列表
     */
    List<TInvoice> queryList(HashMap<String, Object> reqMap);

    int queryListCount(HashMap<String, Object> reqMap);

    List<TInvoice> queryRecordList(HashMap<String, Object> reqMap);

    int queryRecordListCount(HashMap<String, Object> reqMap);

    List<TInvoice> queryProjectRecordList(HashMap<String, Object> reqMap);

    int queryProjectRecordListCount(HashMap<String, Object> reqMap);

}
