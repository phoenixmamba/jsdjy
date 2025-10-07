package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingUser;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2021-08-23
 **/
@Repository
@Mapper
public interface ShoppingUserDao {

    /**
     * 查询列表
     */
    List<ShoppingUser> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询详情
     */
    ShoppingUser queryDetail(ShoppingUser entity);

}
