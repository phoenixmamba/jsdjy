package com.centit.thirdserver.biz.po;

import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/21 22:43
 **/
@Data
public class ThirdApiLogPo {
    private Integer id;

    /**
    * 日志类型 1：crm；2：麦座；3：速停车；4：微信；5：支付宝
    */
    private Integer logtype;

    /**
    * 接口功能简单描述，例如：获取用户信息
    */
    private String loginfo;

    /**
    * 记录时间
    */
    private String logtime;

    /**
    * 请求方服务器IP
    */
    private String logip;

    /**
    * 请求类型GET、POST等
    */
    private String reqmethod;

    /**
    * 接口服务地址
    */
    private String serverpath;

    /**
    * 请求时间
    */
    private String reqtime;

    /**
    * 请求报文信息
    */
    private String reqinfo;

    /**
    * 接口返回时间
    */
    private String rettime;

    /**
    * 返回报文信息
    */
    private String retinfo;

    private Integer status;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", logtype=").append(logtype);
        sb.append(", loginfo=").append(loginfo);
        sb.append(", logtime=").append(logtime);
        sb.append(", logip=").append(logip);
        sb.append(", reqmethod=").append(reqmethod);
        sb.append(", serverpath=").append(serverpath);
        sb.append(", reqtime=").append(reqtime);
        sb.append(", reqinfo=").append(reqinfo);
        sb.append(", rettime=").append(rettime);
        sb.append(", retinfo=").append(retinfo);
        sb.append(", status=").append(status);
        sb.append("]");
        return sb.toString();
    }
}