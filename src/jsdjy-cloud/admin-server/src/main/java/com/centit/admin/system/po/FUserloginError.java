package com.centit.admin.system.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-08-04
 **/
@Data
public class FUserloginError implements Serializable {


    private String id;

    private String userCode;

    private String loginTime;


}
