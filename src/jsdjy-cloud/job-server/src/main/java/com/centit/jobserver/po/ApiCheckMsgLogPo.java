package com.centit.jobserver.po;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/25 9:46
 **/
public class ApiCheckMsgLogPo {
    private Integer id;

    /**
    * 1：麦座；2：CRM；3：速停车
    */
    private Integer type;

    private String sendTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", type=").append(type);
        sb.append(", sendTime=").append(sendTime);
        sb.append("]");
        return sb.toString();
    }
}