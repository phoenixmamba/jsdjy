package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-13
 **/
@Data
public class ShoppingMoviePhoto implements Serializable {


    private Long id;

    private String photoId;

    private String movieId;


}
