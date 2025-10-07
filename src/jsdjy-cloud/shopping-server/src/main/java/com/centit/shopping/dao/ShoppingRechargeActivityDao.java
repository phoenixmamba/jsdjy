package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingRechargeActivity;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-09-30
 **/
@Repository
@Mapper
public interface ShoppingRechargeActivityDao {

    /**
     * 新增
     */
    int insert(ShoppingRechargeActivity entity);

    /**
     * 更新
     */
    int update(ShoppingRechargeActivity entity);

    int unpubActicitys(ShoppingRechargeActivity entity);

    /**
     * 删除
     */
    int delete(ShoppingRechargeActivity entity);

    /**
     * 查询详情
     */
    ShoppingRechargeActivity queryDetail(ShoppingRechargeActivity entity);

    /**
     * 查询列表
     */
    List<ShoppingRechargeActivity> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

}
