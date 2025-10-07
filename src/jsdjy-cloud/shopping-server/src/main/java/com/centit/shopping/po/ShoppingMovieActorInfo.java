package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>演职人员<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-13
 **/
@Data
public class ShoppingMovieActorInfo implements Serializable {


    private Long id;

    /**
     * 电影id
     */
    private String movieId;

    /**
     * 演员名称
     */
    private String actorName;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 演职人员照片
     */
    private String photoId;

    /**
     * 职员0 演员1
     */
    private String type;

    /**
     * 排序
     */
    private Integer sort;


}
