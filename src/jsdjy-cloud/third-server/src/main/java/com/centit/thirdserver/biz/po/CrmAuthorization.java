package com.centit.thirdserver.biz.po;

import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/25 10:42
 **/
@Data
public class CrmAuthorization {
    private String roleType;

    private String authorization;

    private String updateTime;

    private String invalidTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", roleType=").append(roleType);
        sb.append(", authorization=").append(authorization);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", invalidTime=").append(invalidTime);
        sb.append("]");
        return sb.toString();
    }
}