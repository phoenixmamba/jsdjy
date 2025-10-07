package com.centit.admin.system.po;

import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-03
 **/
@Data
public class FOptinfo implements Serializable , Comparable<FOptinfo>{


    /**
     * 菜单id
     */
    private String optId;

    /**
     * 菜单名称
     */
    private String optName;

    /**
     * 父级菜单名称
     */
    private String preOptId;

    /**
     * 与angularjs路由匹配
     */
    private String optRoute;

    /**
     * 链接
     */
    private String optUrl;

    private String formCode;

    /**
     *  S:实施业务, O:普通业务, W:流程业务, I :项目业务
     */
    private String optType;

    private Integer msgNo;

    private String msgPrm;

    private String isInToolbar;

    private Integer imgIndex;

    /**
     * 顶级菜单ID
     */
    private String topOptId;

    /**
     * 排序号这个顺序只需在同一个父业务下排序
     */
    private Integer orderInd;

    private String flowCode;

    /**
     * D : DIV I:iFrame
     */
    private String pageType;

    /**
     * 图标
     */
    private String icon;

    private Integer height;

    private Integer width;

    /**
     * 更新时间
     */
    private String updateDate;

    private String createDate;

    private String creator;

    private String updator;

    private List<FOptinfo> children = new ArrayList<>();

    private List<FOptdef> optMethods = new ArrayList<>();

    private JSONObject attributes =new JSONObject();

    public JSONObject getAttributes(){
        JSONObject obj =new JSONObject();
        obj.put("external",false);
        return obj;
    }

    @Override
    public int compareTo(FOptinfo o) {
        return this.orderInd - o.orderInd;
    }
}
