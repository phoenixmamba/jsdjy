package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivitySignupinfo;
import com.centit.shopping.po.ShoppingArtclassSignupinfo;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-24
 **/
@Repository
@Mapper
public interface ShoppingArtclassSignupinfoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtclassSignupinfo entity);

    /**
     * 更新
     */
    int update(ShoppingArtclassSignupinfo entity);

    /**
     * 删除
     */
    int delete(ShoppingArtclassSignupinfo entity);

    /**
     * 查询详情
     */
    ShoppingArtclassSignupinfo queryDetail(ShoppingArtclassSignupinfo entity);

    /**
     * 查询列表
     */
    List<ShoppingArtclassSignupinfo> queryList(HashMap<String, Object> reqMap);

    List<ShoppingArtclassSignupinfo> querySignupInfos(HashMap<String, Object> reqMap);
    int querySignupInfoCount(HashMap<String, Object> reqMap);

}
