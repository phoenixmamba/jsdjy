package com.centit.shopping.dao;

import com.centit.shopping.po.PlatformMonitorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface PlatformMonitorDao {
    List<PlatformMonitorInfo> selectRegisterUser(HashMap reqMap);

    int queryUserCount(HashMap<String, Object> reqMap);

    List<PlatformMonitorInfo> selectActivityUser(HashMap reqMap);

    List<PlatformMonitorInfo> selectEquipmentStatistics(HashMap reqMap);

    List<PlatformMonitorInfo> selectInstallVersionStatistics(HashMap reqMap);

    List<PlatformMonitorInfo> selectRegisterUserByDate(@Param("list") List<Date> between,@Param("searchType")String searchType);

    Date queryMostEarlyDate();

    List<HashMap<String, Object>> queryMoneyList(HashMap<String, Object> reqMap);
}
