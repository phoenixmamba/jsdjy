package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.GUserinfo;
import com.centit.zuulgateway.po.GUserinfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
@Repository
@Mapper
public interface GUserinfoDao {
    /**
     * 新增
     */
//    int insert(GUserinfo entity);

    /**
     * 新增2：新增前根据登录账号和密码判断用户是否存在
     */
    int insert(GUserinfo entity);

    /**
     * 更新
     */
    int update(GUserinfo entity);

    int updateMobile(GUserinfo entity);

    /**
     * 删除
     */
    int delete(GUserinfo entity);

    /**
     * 查询详情
     */
    GUserinfo queryDetail(GUserinfo entity);

    /**
     * 查询详情Vo
     */
    GUserinfoVo queryDetailVo(GUserinfoVo entity);

    /**
     * 查询详情queryDetailByPropertiesVo
     */
    GUserinfoVo queryDetailByPropertiesVo(GUserinfoVo entity);

    /**
     * 查询数量
     */
    int queryCount(GUserinfo entity);

    /**
     * 查询分页列表
     */
    List<GUserinfo> queryPageList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表-数量
     */
    int queryPageListCount(HashMap<String, Object> reqMap);

    /**
     * 查询列表
     */
    List<GUserinfo> queryList(HashMap<String, Object> reqMap);

    GUserinfo queryCrmUrl(HashMap<String, Object> reqMap);

    List<GUserinfo> queryUserInfoList(HashMap<String, Object> reqMap);


}
