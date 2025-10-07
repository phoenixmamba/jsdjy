package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-12-14
 **/
@Data
public class ShoppingArtplanSignupinfo implements Serializable {


    private String id;

    private String activityId;

    private String ofId;

    private String signupInfo;

    private String signupTime;


}
