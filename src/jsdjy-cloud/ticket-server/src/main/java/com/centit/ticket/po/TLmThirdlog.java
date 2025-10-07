package com.centit.ticket.po;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-07-05
 **/
@Data
public class TLmThirdlog implements Serializable {


    private String id;

    /**
     * 日志类型 1：crm；2：麦座；3：速停车；0：其它
     */
    private Integer logtype;

    /**
     * 接口功能简单描述，例如：获取用户信息
     */
    private String loginfo;

    /**
     * 记录时间
     */
    private String logtime;

    /**
     * 请求方服务器IP
     */
    private String logip;

    /**
     * 请求类型GET、POST等
     */
    private String reqmethod;

    /**
     * 接口服务地址
     */
    private String serverpath;

    /**
     * 请求时间
     */
    private String reqtime;

    /**
     * 请求报文信息
     */
    private String reqinfo;

    /**
     * 接口返回时间
     */
    private String rettime;

    /**
     * 返回报文信息
     */
    private String retinfo;

    private int status;

    private Integer miles;

    public Integer getMiles(){
        if(null !=getReqtime()&&null !=getRettime()){
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                Date date1 = sf.parse(getReqtime());
                Date date2 =  sf.parse(getRettime());
                int hours = (int) (date1.getTime() - date2.getTime());
                return hours;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }
}
