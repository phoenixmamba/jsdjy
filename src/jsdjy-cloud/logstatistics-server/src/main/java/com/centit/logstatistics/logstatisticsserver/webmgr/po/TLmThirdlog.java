package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2021-07-05；
 **/
@Data
public class TLmThirdlog implements Serializable {


    private String id;

    private Integer logtype;

    private String loginfo;

    private String logtime;

    private String logip;

    private String reqmethod;

    private String serverpath;

    private String reqtime;

    private String reqinfo;

    private String rettime;

    private String retinfo;

    private Integer status;

    public Integer getMiles(){
        if(null !=getReqtime()&&null!=getRettime()){
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                Date reqDate = sf.parse(getReqtime());
                Date retDate = sf.parse(getRettime());
                int miles = (int) ((retDate.getTime() - reqDate.getTime()));
                return miles;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

}
