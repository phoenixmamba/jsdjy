package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;

import com.centit.shopping.utils.StringUtil;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-25
 **/
@Data
public class ShoppingCoupon implements Serializable {


    private String id;

    private String right_No;

    private String createdate;

    private String right_Type;

    private String right_Display;

    private String right_Content;

    private String act_Rule;

    private String memo;

    private Integer stack_Used;

    private Integer discount_First;

    private String time_Type;

    private String start_Date;

    private String end_Date;

    private Integer time_Unit;

    private Integer fix_Month;

    private Integer max_Money;

    private Integer min_Money;

    /**
     * 0：未删除；1：已删除
     */
    private String isdelete;

    /**
     * 0：下架；1：上架
     */
    private String ispub;

    /**
     * 单人限领数量，0标识不限制
     */
    private Integer perLimit;

    private Integer acPerLimit;

    private Integer offline;

    private String writeOffCount;

//    public String getStart_Date(){
//        if(null !=getTime_Type()&&getTime_Type().equals("limit")){
//            return createdate;
//        }else{
//            return start_Date;
//        }
//    }
//
//    public String getEnd_Date(){
//        if(null !=getTime_Type()&&getTime_Type().equals("limit")){
//            if(getTime_Unit()==1){   //
//                return StringUtil.addDay(getCreatedate(),getFix_Month());
//            }else{
//                return StringUtil.addMonth(getCreatedate(),getFix_Month());
//            }
//        }else{
//            return end_Date;
//        }
//    }
}
