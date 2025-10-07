package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-19
 **/
@Data
public class TicketRedeemProject implements Serializable {


    private String projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目座位类型；1=有座自助选座，2=无座
     */
    private Integer projectSeatType;

    /**
     * 项目销售状态；1=待销售，2=销售中，3=销售结束
     */
    private Integer projectSaleState;

    /**
     * 一级项目分类id
     */
    private String firstClassId;

    /**
     * 一级项目分类名称
     */
    private String firstClassName;

    /**
     * 二级项目分类id
     */
    private String secondClassId;

    /**
     * 二级项目分类名称
     */
    private String secondClassName;

    /**
     * 项目海报图片地址
     */
    private String projectImgUrl;

    private String projectRound;

    /**
     * 项目简介；格式：富文本
     */
    private String projectIntroduce;

    /**
     * 移动端排序号
     */
    private Integer sn;

    /**
     * 开始销售时间
     */
    private String saleStartTime;

    /**
     * 截止销售时间
     */
    private String saleEndTime;

    private String isDelete;

    private String isBindall;

    private String writeOffCount;
}
