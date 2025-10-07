package com.centit.file.webmgr.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2023-12-20
 **/
@Data
public class TicketProjectImg implements Serializable {


    private String projectId;

    private String projectImgUrl;

    private String imgId;

    private String createTime;


}
