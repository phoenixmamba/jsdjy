package com.centit.jobserver.dao;

import com.centit.jobserver.po.ArtPushPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-03-09
 **/
@Repository
@Mapper
public interface ArtPushDao {

    List<ArtPushPo> selectToPushActivitys(HashMap<String, Object> reqMap);
    List<ArtPushPo> selectToPushPlans(HashMap<String, Object> reqMap);
    List<String> selectArtactivityUserMobiles(@Param(value = "activityId") String activityId, @Param(value = "cartType") Integer cartType);
    int updateActivityPushStatus(ArtPushPo artPushPo);
    int updatePlanPushStatus(ArtPushPo artPushPo);
}
