package com.centit.mallserver.model;

import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 用户信息
 * @Date : 2024/12/19 14:51
 **/
@Data
public class UserInfo {
    private String id;

    private String mzuserid;

    private String addTime;

    private String mobile;

    /**
     * 0女,1男,2保密
     */
    private Integer sex;

    private String nickName;

    private String levelName;

}
