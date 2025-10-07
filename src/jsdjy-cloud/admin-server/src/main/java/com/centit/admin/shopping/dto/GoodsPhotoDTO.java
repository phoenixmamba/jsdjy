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
public class GoodsPhotoDTO implements Serializable {
    /**
     * 图片id
     */
    @NotNull(message = "图片id不能为空")
    private String photoId;
}
