package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2022-08-08
 **/
@Data
public class TExportFile implements Serializable {


    private String id;

    private String dataType;

    private String fileName;

    private Integer taskStatus;

    private String finishTime;

    private String createUser;

    private String createTime;


}
