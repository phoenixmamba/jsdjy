package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-24
 **/
@Data
public class TExportFile implements Serializable {


    private String id;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 任务状态 0：生成中；1：已完成；2：失败
     */
    private Integer taskStatus;

    /**
     * 文件生成时间
     */
    private String finishTime;

    private String createUser;

    private String createTime;


}
