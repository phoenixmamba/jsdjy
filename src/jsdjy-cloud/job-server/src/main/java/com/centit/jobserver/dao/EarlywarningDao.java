package com.centit.jobserver.dao;

import com.centit.jobserver.po.EarlywarningPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 定时预警任务Mapper接口
 * @Date : 2024/11/26 21:14
 **/
@Mapper
public interface EarlywarningDao {

    List<EarlywarningPo> selectList(HashMap<String,Object> reqMap);

    int updateByPrimaryKeySelective(EarlywarningPo record);
}