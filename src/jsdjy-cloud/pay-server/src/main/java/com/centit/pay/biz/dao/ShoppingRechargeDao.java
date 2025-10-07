package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingRecharge;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-13
 **/
@Repository
@Mapper
public interface ShoppingRechargeDao {

    /**
     * 新增
     */
    int insert(ShoppingRecharge entity);

    /**
     * 更新
     */
    int update(ShoppingRecharge entity);

    /**
     * 删除
     */
    int delete(ShoppingRecharge entity);

    /**
     * 查询详情
     */
    ShoppingRecharge queryDetail(ShoppingRecharge entity);

    /**
     * 查询列表
     */
    List<ShoppingRecharge> queryList(HashMap<String, Object> reqMap);

    /**
     * 更新充值状态
     */
    int setRechargeStatus(HashMap<String, Object> reqMap);

    ShoppingRecharge queryRecharge(HashMap<String, Object> reqMap);

}
