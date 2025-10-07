package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-07-19
 **/
@Data
public class TInTest implements Serializable {


    private Long id;

    private Date addtime;

    private String mzstr;

    private String crmstr;


}
