package com.centit.zuulgateway.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2020-05-28
 **/
@Data
public class TLmOperlog implements Serializable {


    private String id;

    private String logtype;

    private String loginfo;

    private String logtime;

    private String userid;

    private String username;

    private String deptcode;

    private String identcode;

    private String servername;

    private String versionid;

    private String devicetype;

    private String osversion;

    private String logip;

    private String serverclass;

    private String servermethod;

    private String serverpath;

    private String reqinfo;

    private String retinfo;

    private String delflag;

    private String reqmethod;


}
