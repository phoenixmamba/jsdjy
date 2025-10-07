package com.centit.shopping.dao;

import com.centit.shopping.po.TicketCouponBind;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-01-06
 **/
@Repository
@Mapper
public interface TicketCouponBindDao {

    /**
     * 新增
     */
    int insert(TicketCouponBind entity);

    /**
     * 更新
     */
    int update(TicketCouponBind entity);

    /**
     * 删除
     */
    int delete(TicketCouponBind entity);

    /**
     * 查询详情
     */
    TicketCouponBind queryDetail(TicketCouponBind entity);

    /**
     * 查询列表
     */
    List<TicketCouponBind> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 查询未绑定指定优惠码的用户列表
     */
    List<HashMap<String, Object>> queryUnBindUserList(HashMap<String, Object> reqMap);

    int queryUnBindUserCount(HashMap<String, Object> reqMap);

}
