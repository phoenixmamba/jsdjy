package com.centit.admin.shopping.dao;

import com.centit.admin.shopping.po.GoodsPhotoPo;
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
public interface GoodsPhotoDao {

    /**
     * 新增
     */
    int insert(GoodsPhotoPo entity);

    /**
     * 更新
     */
    int update(GoodsPhotoPo entity);

    /**
     * 删除
     */
    int delete(GoodsPhotoPo entity);

    /**
     * 查询详情
     */
    GoodsPhotoPo queryDetail(GoodsPhotoPo entity);

    /**
     * 查询列表
     */
    List<GoodsPhotoPo> queryList(HashMap<String, Object> reqMap);

    List<GoodsPhotoPo> selectGoodsPhotos(String goodsId);

    int batchInsertGoodsPhotos(List<GoodsPhotoPo> shoppingGoodsSpecPoList);
}
