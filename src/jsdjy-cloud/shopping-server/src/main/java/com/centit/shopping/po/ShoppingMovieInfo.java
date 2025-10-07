package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-13
 **/
@Data
public class ShoppingMovieInfo implements Serializable {


    private String id;

    /**
     * 电影名称
     */
    private String movieName;
    /**
     * 电影名称
     */
    private String movieOtherName;

    /**
     * 电影主图
     */
    private String movieImage;

    /**
     * 电影时长 单位分钟
     */
    private String movieDuration;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 上映时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date releaseTime;

    /**
     * 上映地点
     */
    private String releaseLocation;

    /**
     * 简介
     */
    private String synopsis;

    /**
     * 0未上映
     * 1上映中
     * 2已下映
     */
    private Integer status;

    /**
     * 添加时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;

    /**
     * 添加人
     */
    private String createUser;


    List<ShoppingMovieActorInfo> actorInfos;
    List<ShoppingMoviePhoto> photos;


}
