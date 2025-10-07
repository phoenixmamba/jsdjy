package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-07-14
 **/
@Data
public class TDjAppointment implements Serializable {


    private String id;

    private String company;

    private String contactMan;

    private String contactNumber;

    private String appointmentTime;

    private Integer peopleNumber;

    private String remark;

    private String userId;

    private String addTime;

    private String userName;

    public String getUsername(){
        try{
            if(null !=getUserId()){
                return CommonUtil.getShoppingUserByUserId(getUserId()).getNickName();
            }
        }catch (Exception e){
            return "";
        }
        return "";
    }
}
