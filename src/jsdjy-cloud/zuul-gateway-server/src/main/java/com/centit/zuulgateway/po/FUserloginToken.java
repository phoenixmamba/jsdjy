package com.centit.zuulgateway.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-06-15
 **/
@Data
public class FUserloginToken implements Serializable {


    private String usercode;

    private String token;

    private String expiretime;

    private String isValid;


}
