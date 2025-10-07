package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-08-17
 **/
@Data
public class ShoppingImgtext implements Serializable {


    private String id;

    private String addTime;

    private String addUser;

    private Integer sn;

    private String type;

    public String getTypeName(){
        FDatadictionary fDatadictionary = CommonUtil.getFDatadictionary("IMGTEXT_TYPE",getType());
        return fDatadictionary==null?"":fDatadictionary.getDataValue();
    }

    private String title;

    private String cover;

    private String content;


}
