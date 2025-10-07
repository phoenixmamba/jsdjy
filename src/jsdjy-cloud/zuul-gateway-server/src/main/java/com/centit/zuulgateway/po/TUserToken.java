package com.centit.zuulgateway.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p>用户token表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2020-08-05
 **/
@Data
public class TUserToken implements Serializable {


    /**
     * 用户编码
     */
    private String userCode;

    /**
     * token
     */
    private String token;

    /**
     * 服务/客户端 名称
     */
    private String servName;

    /**
     * 生效时间
     */
    private String activeTime;

    /**
     * 有效期（单位秒）
     */
    private String validperiod;

    /**
     * 是否有效：T/F
     */
    private String isValid;

    /**
     * tokenId
     */
    private String tokenId;


}
