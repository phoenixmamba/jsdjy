package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-06-02
 **/
@Data
public class TSystemStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String ip;

    /**
     * 已使用内存
     */
    private float memoryUesd;

    /**
     * 总内存
     */
    private float memoryTotal;

    /**
     * 空闲内存
     */
    private float memoryFree;

    /**
     * cpu占用
     */
    private double cpuIdle;

    /**
     * cpu空闲
     */
    private double cpuCombined;

    /**
     * 接收包

     */
    private long rxPackages;

    /**
     * 接收错误包
     */
    private long rxErrors;

    /**
     * 接收丢包
     */
    private long rxDropped;

    /**
     * 发送包
     */
    private long txPackages;

    /**
     * 发送错误包
     */
    private long txErrors;

    /**
     * 发送丢包
     */
    private long txDropped;

    /**
     * 磁盘总量
     */
    private float fileSystemTotal;

    /**
     * 磁盘剩余
     */
    private float fileSystemFree;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date date;

    /**
     * 下载速度
     */
    private float rxSpeed;

    /**
     * 上传速度
     */
    private float txSpeed;

    private String remark;

}
