package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-21
 **/
@Data
public class TOnOndemandhistory implements Serializable {


    /**
     * 主键id
     */
    private String id;

    /**
     * 点播专题关联id
     */
    private String ondemandid;

    /**
     * 用户id
     */
    private String userid;

    /**
     * 类型id
     */
    private String classid;

    /**
     * 类型名称
     */
    private String classname;

    /**
     * 退出时长
     */
    private Float exithour;

    private Integer exitcount;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 修改时间
     */
    private Date updatetime;

    /**
     * 是否免费 0是 1否
     */
    private String isbuy;

    private String isclick;


}
