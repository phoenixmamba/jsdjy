package com.centit.admin.system.po;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;
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
public class FRolepower implements Serializable {


    private String roleCode;

    private String optCode;

    /**
     * 用逗号隔开的数据范围结合（空\all 表示全部）
     */
    private String optScopeCodes;

    private String updateDate;

    private String createDate;

    private String creator;

    private String updator;


    private JSONObject id;

    public JSONObject getId(){
        JSONObject obj = new JSONObject();
        obj.put("optCode",optCode);
        obj.put("roleCode",roleCode);
        return obj;
    }

    private String lastModifyDate;

    public String getLastModifyDate(){
        if(StringUtils.isNotBlank(getUpdateDate())){
            return getUpdateDate();
        }else{
            return "";
        }
    }
}
