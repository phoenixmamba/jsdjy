package com.centit.admin.shopping.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-22
 **/
@Data
public class GoodsSpecDTO implements Serializable {

    /**
     * 属性id
     */
    @NotNull(message = "规格属性id字段不能为空")
    private String specId;

}
