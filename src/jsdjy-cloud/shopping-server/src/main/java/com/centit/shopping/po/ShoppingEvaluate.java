package com.centit.shopping.po;

import java.math.BigDecimal;
import java.util.*;
import java.io.Serializable;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;

/**
 * <p>商品评价<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-16
 **/
@Data
public class ShoppingEvaluate implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String evaluateAdminInfo;

    private Integer evaluateBuyerVal;

    private String evaluateInfo;

    private String evaluateSellerInfo;

    private String evaluateSellerTime;

    private Integer evaluateSellerVal;

    private Integer evaluateStatus;

    private String evaluateType;

    private String goodsSpec;

    private String evaluateGoodsId;

    private String evaluateSellerUserId;

    private String evaluateUserId;

    private Map<String,Object> evaluateUserInfo = new HashMap<>();

    public Map<String,Object> getEvaluateUserInfo(){
        ShoppingUser user = CommonUtil.getShoppingUserByUserId(getEvaluateUserId());
        Map<String,Object> map = new HashMap<>();
        map.put("userId",user.getId());
        map.put("userName",user.getNickName());
        map.put("userPhoto",user.getPhotoId());
        return map;
    }

    private String ofId;

    private Integer descriptionEvaluate;

    private Integer serviceEvaluate;

    private Integer shipEvaluate;

    private Integer isRecommend;

    private List<ShoppingEvaluatePhoto> photos = new ArrayList<ShoppingEvaluatePhoto>();

    public List<ShoppingEvaluatePhoto> getPhotos(){
        return CommonUtil.getShoppingEvaluatePhotos(getId());
    }
}
