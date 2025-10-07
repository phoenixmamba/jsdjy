package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivitySignupinfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-22
 **/
@Repository
@Mapper
public interface ShoppingArtactivitySignupinfoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtactivitySignupinfo entity);

    /**
     * 更新
     */
    int update(ShoppingArtactivitySignupinfo entity);

    /**
     * 删除
     */
    int delete(ShoppingArtactivitySignupinfo entity);

    /**
     * 查询详情
     */
    ShoppingArtactivitySignupinfo queryDetail(ShoppingArtactivitySignupinfo entity);

    /**
     * 查询列表
     */
    List<ShoppingArtactivitySignupinfo> queryList(HashMap<String, Object> reqMap);

    List<ShoppingArtactivitySignupinfo> querySignupInfos(HashMap<String, Object> reqMap);
    int querySignupInfoCount(HashMap<String, Object> reqMap);

    List<HashMap<String, Object>> querySignupInfosTest(HashMap<String, Object> reqMap);
}
