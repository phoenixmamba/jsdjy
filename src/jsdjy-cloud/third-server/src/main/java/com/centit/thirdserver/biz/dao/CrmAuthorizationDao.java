package com.centit.thirdserver.biz.dao;

import com.centit.thirdserver.biz.po.CrmAuthorization;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/25 10:42
 **/
@Repository
@Mapper
public interface CrmAuthorizationDao {
    int insertOnDuplicateKey(CrmAuthorization record);

    CrmAuthorization selectByPrimaryKey(String roleType);

}