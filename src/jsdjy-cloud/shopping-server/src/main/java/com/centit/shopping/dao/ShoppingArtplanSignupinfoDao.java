package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtplanSignupinfo;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-12-14
 **/
@Repository
@Mapper
public interface ShoppingArtplanSignupinfoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtplanSignupinfo entity);

    /**
     * 更新
     */
    int update(ShoppingArtplanSignupinfo entity);

    /**
     * 删除
     */
    int delete(ShoppingArtplanSignupinfo entity);

    /**
     * 查询详情
     */
    ShoppingArtplanSignupinfo queryDetail(ShoppingArtplanSignupinfo entity);

    /**
     * 查询列表
     */
    List<ShoppingArtplanSignupinfo> queryList(HashMap<String, Object> reqMap);

    List<ShoppingArtplanSignupinfo> querySignupInfos(HashMap<String, Object> reqMap);
    int querySignupInfoCount(HashMap<String, Object> reqMap);

}
