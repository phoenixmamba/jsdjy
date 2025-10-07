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
 * @Date : 2021-03-25
 **/
@Data
public class ShoppingArtactivityPhoto implements Serializable {


    private String activityId;

    private String photoId;

    private String photoUrl;

    public String getPhotoUrl(){
        return CommonUtil.getSysConfig().getImageWebServer()+getPhotoId();
    }


}
