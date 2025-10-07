package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.FUserinfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>系统用户<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2020-04-10
 **/
@Repository
@Mapper
public interface FUserinfoDao {


    /**
     * 查询详情
     */
    FUserinfo queryDetail(FUserinfo entity);

    /**
     * 根据Properties查询详情
     */
    FUserinfo queryDetailByProperties(FUserinfo entity);

}
