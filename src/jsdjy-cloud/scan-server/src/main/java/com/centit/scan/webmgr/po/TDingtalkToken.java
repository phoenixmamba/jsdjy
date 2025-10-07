package com.centit.scan.webmgr.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-12-09
 **/
@Data
public class TDingtalkToken implements Serializable {


    private String agentId;

    private String appKey;

    private String appSecret;

    private String accessToken;

    private String invaliedTime;


}
