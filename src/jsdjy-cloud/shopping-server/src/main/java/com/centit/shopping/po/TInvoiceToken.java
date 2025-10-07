package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-11-26
 **/
@Data
public class TInvoiceToken implements Serializable {


    private String appKey;

    private String appSecret;

    /**
     * 诺诺平台accessToken
     */
    private String accessToken;

    /**
     * token获取时间
     */
    private String createTime;

    /**
     * token失效时间（比诺诺平台的实际失效时间提前一个小时）
     */
    private String expiresTime;


}
