package com.centit.shopping.common.contst;

import java.math.BigDecimal;

/**
 * @描述：
 * @作者： zhouchaoxi
 * @日期：2018/8/8
 */
public class PayConfig {

    private String orderId;
    private String body;
    private String type;
    private BigDecimal price;
    private String openId;
    private String ip;
    private String appId;
    private String mchId;
    private String partenerKey;
    private String payTime;

    public PayConfig(String orderId, BigDecimal price, String appId, String mchId, String partenerKey) {
        this.appId = appId;
        this.mchId = mchId;
        this.partenerKey = partenerKey;
        this.orderId = orderId;
        this.price = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getPartenerKey() {
        return partenerKey;
    }

    public void setPartenerKey(String partenerKey) {
        this.partenerKey = partenerKey;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    @Override
    public String toString() {
        return "PayConfig{" +
                "orderId='" + orderId + '\'' +
                ", body='" + body + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                ", openId='" + openId + '\'' +
                ", ip='" + ip + '\'' +
                ", appId='" + appId + '\'' +
                ", mchId='" + mchId + '\'' +
                ", partenerKey='" + partenerKey + '\'' +
                ", payTime='" + payTime + '\'' +
                '}';
    }
}
