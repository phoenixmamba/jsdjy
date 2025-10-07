package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.TUserToken;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>用户token表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2020-08-05
 **/
@Repository
@Mapper
public interface TUserTokenDao {

    /**
     * 新增
     */
    int insert(TUserToken entity);

    /**
     * 更新
     */
    int update(TUserToken entity);

    /**
     * 删除
     */
    int delete(TUserToken entity);

    /**
     * 查询详情
     */
    TUserToken queryDetail(TUserToken entity);

    /**
     * 查询列表
     */
    List<TUserToken> queryList(HashMap<String, Object> reqMap);

    /**
     * 有则更新无则插入merge into
     */
    int mergeInto(TUserToken entity);


    /**
     * 查询数量
     */
    int queryCount(TUserToken entity);

    /**
     * 查询token是否过期失效，大于0则为过期，等于0则过期
     */
    int queryLoginTokenExpire(TUserToken entity);

}
