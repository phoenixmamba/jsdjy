package com.centit.jobserver.dao;

import com.centit.jobserver.po.ApiCheckMsgLogPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/25 9:46
 **/
@Mapper
public interface ApiCheckMsgLogDao {

    int insert(ApiCheckMsgLogPo record);

    List<ApiCheckMsgLogPo> selectInHourData(int type);
}