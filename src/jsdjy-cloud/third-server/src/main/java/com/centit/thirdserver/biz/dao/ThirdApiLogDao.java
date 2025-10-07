package com.centit.thirdserver.biz.dao;

import com.centit.thirdserver.biz.po.ThirdApiLogPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/21 22:43
 **/
@Repository
@Mapper
public interface ThirdApiLogDao {

    int insertSelective(ThirdApiLogPo record);
}