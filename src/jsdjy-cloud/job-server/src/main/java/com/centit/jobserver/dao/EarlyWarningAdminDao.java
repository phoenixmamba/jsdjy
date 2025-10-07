package com.centit.jobserver.dao;

import com.centit.jobserver.po.EarlyWarningAdminPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/23 19:43
 **/
@Mapper
public interface EarlyWarningAdminDao {
    List<EarlyWarningAdminPo> selectAll();

}