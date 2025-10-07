package com.centit.shopping.po;

import java.io.Serializable;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-22
 **/
@Data
public class ShoppingGoodsPhoto implements Serializable {


    private String goodsId;

    private String photoId;

    private String photoUrl;

    public String getPhotoUrl(){
        return CommonUtil.getSysConfig().getImageWebServer()+getPhotoId();
    }
}
