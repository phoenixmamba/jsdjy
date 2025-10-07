package com.centit.admin.shopping.dao;

import com.centit.admin.shopping.po.GoodsSpecPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-22
 **/
@Repository
@Mapper
public interface GoodsSpecDao {

    /**
     * 新增
     */
    int insert(GoodsSpecPo entity);

    /**
     * 更新
     */
    int update(GoodsSpecPo entity);

    /**
     * 删除
     */
    int delete(GoodsSpecPo entity);

    /**
     * 查询详情
     */
    GoodsSpecPo queryDetail(GoodsSpecPo entity);

    /**
     * 查询列表
     */
    List<GoodsSpecPo> queryList(HashMap<String, Object> reqMap);

    int batchInsertGoodsSpecs(List<GoodsSpecPo> goodsSpecPoList);
}
