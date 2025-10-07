package com.centit.zuulgateway.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2023-06-05
 **/
@Data
public class TConcurrencySwitch implements Serializable {


    /**
     * 高峰时控制卖座同步演出信息的开关，打开时表示仅同步新增的演出信息，on：打开；off：关闭
     */
    private String syncMzSwitch;

    /**
     * app首页接口开关
     */
    private String homepageApiSwitch;

    /**
     * 高并发时需要更新的演出id
     */
    private String syncProjectid;

    private String htmlVersion;
}
