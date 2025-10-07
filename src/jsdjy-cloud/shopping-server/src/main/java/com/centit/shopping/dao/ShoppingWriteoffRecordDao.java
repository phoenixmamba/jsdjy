package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingWriteoffRecord;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-05
 **/
@Repository
@Mapper
public interface ShoppingWriteoffRecordDao {

    /**
     * 新增
     */
    int insert(ShoppingWriteoffRecord entity);

    /**
     * 更新
     */
    int update(ShoppingWriteoffRecord entity);

    /**
     * 删除
     */
    int delete(ShoppingWriteoffRecord entity);

    /**
     * 查询详情
     */
    ShoppingWriteoffRecord queryDetail(ShoppingWriteoffRecord entity);

    /**
     * 查询列表
     */
    List<ShoppingWriteoffRecord> queryList(HashMap<String, Object> reqMap);

    List<HashMap<String, Object>> queryWriteRecordList(HashMap<String, Object> reqMap);

    int queryWriteRecordCount(HashMap<String, Object> reqMap);

}
