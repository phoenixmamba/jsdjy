package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingRechargeActivityRecord;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-10-11
 **/
@Repository
@Mapper
public interface ShoppingRechargeActivityRecordDao {

    /**
     * 新增
     */
    int insert(ShoppingRechargeActivityRecord entity);

    /**
     * 更新
     */
    int update(ShoppingRechargeActivityRecord entity);

    /**
     * 删除
     */
    int delete(ShoppingRechargeActivityRecord entity);

    /**
     * 查询详情
     */
    ShoppingRechargeActivityRecord queryDetail(ShoppingRechargeActivityRecord entity);

    /**
     * 查询列表
     */
    List<ShoppingRechargeActivityRecord> queryList(HashMap<String, Object> reqMap);

}
