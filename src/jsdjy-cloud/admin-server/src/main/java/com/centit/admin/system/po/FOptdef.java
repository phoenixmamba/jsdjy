package com.centit.admin.system.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-03
 **/
@Data
public class FOptdef implements Serializable {


    private String optCode;

    private String optId;

    private String optName;

    /**
     * 操作参数 方法
     */
    private String optMethod;

    private String optUrl;

    private String optDesc;

    private Integer optOrder;

    /**
     * 是否为流程操作方法 F：不是  T ： 是
     */
    private String isInWorkflow;

    private String updateDate;

    private String createDate;

    private String optReq;

    private String creator;

    private String updator;


}
