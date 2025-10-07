package com.centit.admin.shopping.po;

import com.centit.admin.shopping.dto.GoodsPhotoDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-22
 **/
@Data
public class GoodsPhotoPo implements Serializable {

    private String goodsId;

    private String photoId;

    private String photoUrl;

    public GoodsPhotoPo() {
    }

    public GoodsPhotoPo(GoodsPhotoDTO goodsPhotoDto){
        this.photoId= goodsPhotoDto.getPhotoId();
    }
}
