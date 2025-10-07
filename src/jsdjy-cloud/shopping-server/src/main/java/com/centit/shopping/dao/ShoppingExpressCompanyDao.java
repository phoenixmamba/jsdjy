package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingExpressCompany;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-25
 **/
@Repository
@Mapper
public interface ShoppingExpressCompanyDao {

    /**
     * 新增
     */
    int insert(ShoppingExpressCompany entity);

    /**
     * 更新
     */
    int update(ShoppingExpressCompany entity);

    /**
     * 删除
     */
    int delete(ShoppingExpressCompany entity);

    /**
     * 查询详情
     */
    ShoppingExpressCompany queryDetail(ShoppingExpressCompany entity);

    /**
     * 查询列表
     */
    List<ShoppingExpressCompany> queryList(HashMap<String, Object> reqMap);

}
