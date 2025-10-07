package com.centit.jobserver.dao;

import com.centit.jobserver.po.ParkDataHourlyFlowPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>车场每小时的车流数<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2023-10-12
 **/
@Repository
@Mapper
public interface ParkDataHourlyFlowDao {

    int mergeIntoDatas(List<ParkDataHourlyFlowPo> list);
}
