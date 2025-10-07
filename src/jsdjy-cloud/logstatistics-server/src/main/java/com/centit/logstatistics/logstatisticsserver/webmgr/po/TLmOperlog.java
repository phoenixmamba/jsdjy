package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.util.Date;
import java.io.Serializable;

import com.centit.logstatistics.logstatisticsserver.webmgr.utils.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * <p>接口运行日志<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-06
 **/
@Data
public class TLmOperlog implements Serializable {


    private String id;

    /**
     * 日志类型
     */
    private String logtype;

    /**
     * 日志类型信息
     */
    private String loginfo;

    /**
     * 记录时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String logtime;

    /**
     * 操作人
     */
    private String userid;

    private String username;

    public String getUsername(){
        if(null !=getUserid()){
            if(getLogtype().equals("1")){ //移动端
                ShoppingUser user = CommonUtil.getShoppingUserByUserId(getUserid());
                if(null !=user){
                    return user.getNickName();
                }
            }else{
                //根据userid获取移动端用户信息
                FUserinfo userinfo =CommonUtil.getFUserInfo(getUserid());
                if(null !=userinfo){
                    return userinfo.getUserName();
                }
            }
        }
        return "";
    }

    /**
     * 单位编号
     */
    private String deptcode;

    /**
     * 适用终端号
     */
    private String identcode;

    /**
     * 接口服务名
     */
    private String servername;

    /**
     * 版本编号
     */
    private String versionid;

    /**
     * 设备类型
     */
    private String devicetype;

    /**
     * 设备系统版本
     */
    private String osversion;

    /**
     * 终端IP
     */
    private String logip;

    /**
     * 接口服务类
     */
    private String serverclass;

    /**
     * 接口服务方法
     */
    private String servermethod;

    /**
     * 接口服务地址
     */
    private String serverpath;

    /**
     * 请求报文信息
     */
    private String reqinfo;

    /**
     * 删除标志
     */
    private String delflag;

    /**
     * 请求类型get、post等
     */
    private String reqmethod;

    private String retinfo;


}
