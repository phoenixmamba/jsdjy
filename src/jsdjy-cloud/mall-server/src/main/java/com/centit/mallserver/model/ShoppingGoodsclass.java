package com.centit.mallserver.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商品分类
 * @Date : 2021-02-22
 **/
@Data
public class ShoppingGoodsclass implements Serializable {

    private String id;

    private String className;

    private Integer sequence;

    private String parentId;

    public Boolean isEnd;

}
