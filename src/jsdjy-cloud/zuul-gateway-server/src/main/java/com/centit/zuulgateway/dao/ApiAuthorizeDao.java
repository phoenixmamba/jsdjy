package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.ApiAuthorize;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>授权信息<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2020-05-06
 **/
@Repository
@Mapper
public interface ApiAuthorizeDao {



    /**
     * 检测Ip白名单是否存在
     */
    List<ApiAuthorize> queryList(ApiAuthorize entity);

    /**
     * 检测Ip白名单是否存在
     */
    int checkIPValid(String ip);

    /**
     *  检测Ip白名单是否存在、IP最后一个字符为*的通配符则匹配
     */
    int checkIPValid2(String ip);

    /**
     * 检测accessToken是否过期
     */
    int checkAccessTokenExpire(ApiAuthorize entity);


}
