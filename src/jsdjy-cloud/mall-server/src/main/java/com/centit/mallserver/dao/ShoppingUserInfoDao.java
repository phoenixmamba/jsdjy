package com.centit.mallserver.dao;

import com.centit.mallserver.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-22
 **/
@Repository
@Mapper
public interface ShoppingUserInfoDao {

    UserInfo selectUserDetail(String userId);

}
