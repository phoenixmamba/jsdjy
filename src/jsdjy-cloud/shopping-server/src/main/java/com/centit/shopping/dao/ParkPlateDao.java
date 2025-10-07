package com.centit.shopping.dao;

import com.centit.shopping.po.ParkPlate;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-20
 **/
@Repository
@Mapper
public interface ParkPlateDao {

    /**
     * 新增
     */
    int insert(ParkPlate entity);

    /**
     * 更新
     */
    int update(ParkPlate entity);

    /**
     * 更新
     */
    int cancelDefaultPlateNo(ParkPlate entity);

    /**
     * 删除
     */
    int delete(ParkPlate entity);

    /**
     * 查询详情
     */
    ParkPlate queryDetail(ParkPlate entity);

    /**
     * 查询列表
     */
    List<ParkPlate> queryList(HashMap<String, Object> reqMap);

}
