package com.centit.pay.biz.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-06-03
 **/
@Data
public class CrmAuthorization implements Serializable {


    private String roleType;

    private String authorization;

    private String updateTime;

    private String invalidTime;


}
