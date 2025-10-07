package com.centit.admin.system.po;

import java.io.Serializable;

import com.centit.admin.util.CommonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-03
 **/
@Data
public class FUserunit implements Serializable {


    private String userUnitId;

    private String unitCode;

    private String userCode;

    /**
     * T：为主， F：兼职
     */
    private String isPrimary;

    /**
     * 职务级别，岗位
     */
    private String userStation;

    private String userStationText;

    public String getUserStationText() {
        if(StringUtils.isNotBlank(getUserStation())){
            return CommonUtil.getCodeValue("StationType",getUserStation());
        }else{
            return "";
        }

    }

    /**
     * 职务；RANK 代码不是 0开头的可以进行授予
     */
    private String userRank;

    private String userRankText;

    public String getUserRankText() {
        if(StringUtils.isNotBlank(getUserRank())){
            return CommonUtil.getCodeValue("RankType",getUserRank());
        }else{
            return "";
        }

    }

    /**
     * 任职备注
     */
    private String rankMemo;

    private Integer userOrder;

    private String updateDate;

    private String createDate;

    private String creator;

    private String updator;

    private String tempType;

    private String corpid;

    private String userName;

    private String unitName;

    private String regCellPhone;

    private String lastModifyDate;

    public String getLastModifyDate(){
        if(StringUtils.isNotBlank(getUpdateDate())){
            return getUpdateDate();
        }else{
            return "";
        }
    }
}
