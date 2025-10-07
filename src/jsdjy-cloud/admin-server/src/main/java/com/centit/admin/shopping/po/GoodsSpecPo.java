package com.centit.admin.shopping.po;

import com.centit.admin.shopping.dto.GoodsSpecDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商品规格实体类
 * @Date : 2021-02-22
 **/
@Data
public class GoodsSpecPo implements Serializable {

    private String goodsId;

    private String specId;

    public GoodsSpecPo() {
    }

    public GoodsSpecPo(GoodsSpecDTO goodsSpecDTO){
        this.specId= goodsSpecDTO.getSpecId();
    }

}
