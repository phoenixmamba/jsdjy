package com.centit.admin.system.po;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-04
 **/
@Data
public class FDatadictionary implements Serializable {


    /**
     * 字典编码
     */
    private String catalogCode;

    /**
     * 字典项编码
     */
    private String dataCode;

    /**
     * 树型字典的父类代码
     */
    private String extraCode;

    private String extraCode2;

    /**
     * N正常，D已停用，用户可以自解释这个字段
     */
    private String dataTag;

    /**
     * 值
     */
    private String dataValue;

    /**
     * F : 框架固有的 U:用户 S：系统  G国标
     */
    private String dataStyle;

    /**
     * 描述
     */
    private String dataDesc;

    /**
     * 上次更新时间
     */
    private String lastModifyDate;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 排序
     */
    private Integer dataOrder;

    private String fullKey;

    public String getFullKey(){
        return getCatalogCode()+"."+getDataCode();
    }

    private JSONObject id;
    public JSONObject getId(){
        JSONObject obj = new JSONObject();
        obj.put("catalogCode",getCatalogCode());
        obj.put("dataCode",getDataCode());
        return obj;
    }
}
